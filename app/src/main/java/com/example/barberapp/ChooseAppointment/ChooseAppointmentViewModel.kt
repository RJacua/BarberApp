import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.UtilityClasses.AppointmentWithDuration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ChooseAppointmentViewModel(application: Application) : AndroidViewModel(application) {

    private val scheduleDao = AppDatabase(application).barberscheduleDao()
    private val appointmentDao = AppDatabase(application).appointmentDao()

    private val _morningSlots = MutableLiveData<List<String>>()
    val morningSlots: LiveData<List<String>> = _morningSlots

    private val _afternoonSlots = MutableLiveData<List<String>>()
    val afternoonSlots: LiveData<List<String>> = _afternoonSlots

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadSchedules(barberId: Int, dayOfWeek: Int, date: String) {
        val schedules = scheduleDao.getSchedulesByDay(barberId, dayOfWeek)
        val appointments = appointmentDao.getAppointmentsWithDurationByBarber(barberId, date)
        Log.d("Horario", "Horários encontrados: $schedules")
        Log.d("Horario", "Marcações encontradas: $appointments")

        if (schedules.isNotEmpty()) {
            val hours = schedules.first().hours
            Log.d("Horario", "Horários de trabalho: $hours")

            val availableSlots = filterAvailableSlots(hours, appointments)

            val (morning, afternoon) = partitionSlots(availableSlots)
            _morningSlots.value = morning
            _afternoonSlots.value = afternoon

            Log.d("Horario", "Manhã: $morning, Tarde: $afternoon")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun filterAvailableSlots(
        hours: String,
        appointments: List<AppointmentWithDuration>
    ): List<String> {
        val allSlots = hours.split(",").flatMap { generateSlots(it.toInt()) }

        Log.d("Horario", "Todos os horários: $allSlots")

        // Identifica os horários bloqueados
        val blockedSlots = mutableSetOf<String>()
        appointments.forEach { appointment ->
            val startTime = parseTime(appointment.time)

            // Converte a duração para minutos
            val durationInMinutes = appointment.duration.minutes // Obtém os minutos de Time

            val endTime = startTime.plusMinutes(durationInMinutes.toLong())

            var current = startTime
            while (current.isBefore(endTime)) {
                blockedSlots.add(formatTime(current))
                current = current.plusMinutes(15)
            }
        }
        Log.d("Horario", "Horários bloqueados: $blockedSlots")

        // Retorna apenas os horários disponíveis
        val availableSlots = allSlots.filter { it !in blockedSlots }
        Log.d("Horario", "Horários disponíveis: $availableSlots")
        return availableSlots
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun partitionSlots(slots: List<String>): Pair<List<String>, List<String>> {
        val morning = slots.filter { parseTime(it) < LocalTime.of(12, 0) }
        val afternoon = slots.filter { parseTime(it) >= LocalTime.of(12, 0) }
        return Pair(morning, afternoon)
    }

    private fun generateSlots(hour: Int): List<String> {
        val slots = mutableListOf<String>()
        val minutes = listOf("00", "15", "30", "45")
        if(hour == 9){
            for (minute in minutes) {
                slots.add("0$hour:$minute:00") // Alterado para adicionar segundos
            }
        }
        else {
            for (minute in minutes) {
                slots.add("$hour:$minute:00") // Alterado para adicionar segundos
            }
        }
        return slots
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseTime(time: String): LocalTime {
        // Garante que a hora tenha dois dígitos, adicionando um zero à esquerda se necessário
        val formattedTime = if (time.length == 7) "0$time" else time

        // Garante que a string esteja no formato HH:mm:ss
        val timeWithSeconds = if (formattedTime.length == 5) "$formattedTime:00" else formattedTime

        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return LocalTime.parse(timeWithSeconds, formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatTime(time: LocalTime): String {
        return String.format("%02d:%02d:%02d", time.hour, time.minute, time.second) // Adicionando segundos
    }
}