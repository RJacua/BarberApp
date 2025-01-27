import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.UtilityClasses.AppointmentWithDuration
import java.time.LocalDate
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

            val availableSlots = filterAvailableSlots(hours, appointments, date)

            val (morning, afternoon) = partitionSlots(availableSlots)
            _morningSlots.value = morning
            _afternoonSlots.value = afternoon

            Log.d("Horario", "Manhã: $morning, Tarde: $afternoon")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun filterAvailableSlots(
        hours: String,
        appointments: List<AppointmentWithDuration>,
        selectedDate: String
    ): List<String> {
        val allSlots = hours.split(",").flatMap { generateSlots(it.toInt()) }

        Log.d("Horario", "Todos os horários: $allSlots")

        // Obtém a hora atual arredondada para o próximo bloco de 15 minutos
        val now = getRoundedCurrentTime()
        Log.d("Horario", "Hora atual arredondada: $now")

        // Obtém a data de hoje automaticamente
        val todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-M-d"))
        Log.d("Horario", "data atual: $todayDate")
        Log.d("Horario", "data atual: $selectedDate")

        // Se a data selecionada for hoje, aplica a lógica de restrição de horário
        val filteredSlots = if (selectedDate == todayDate) {
            allSlots.filter { parseTime(it) >= now }
        } else {
            allSlots
        }

        // Identifica os horários bloqueados por marcações existentes
        val blockedSlots = mutableSetOf<String>()
        appointments.forEach { appointment ->
            val startTime = parseTime(appointment.time)
            val durationInMinutes = appointment.duration.hours * 60 + appointment.duration.minutes
            val endTime = startTime.plusMinutes(durationInMinutes.toLong())

            var current = startTime
            while (!current.isAfter(endTime.minusMinutes(15))) {
                blockedSlots.add(formatTime(current))
                current = current.plusMinutes(15)
            }
        }

        Log.d("Horario", "Horários bloqueados: $blockedSlots")

        // Filtra horários disponíveis removendo os já bloqueados
        val availableSlots = filteredSlots.filter { it !in blockedSlots }
        Log.d("Horario", "Horários disponíveis: $availableSlots")

        return availableSlots
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getRoundedCurrentTime(): LocalTime {
        val now = LocalTime.now()
        val minutes = now.minute
        val roundedMinutes = ((minutes / 15) + 1) * 15

        return if (roundedMinutes >= 60) {
            now.plusHours(1).withMinute(0).withSecond(0)
        } else {
            now.withMinute(roundedMinutes).withSecond(0)
        }
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
        if (hour == 9) {
            for (minute in minutes) {
                slots.add("0$hour:$minute:00") // Adicionando segundos
            }
        } else {
            for (minute in minutes) {
                slots.add("$hour:$minute:00") // Adicionando segundos
            }
        }
        return slots
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseTime(time: String): LocalTime {
        val formattedTime = if (time.length == 7) "0$time" else time
        val timeWithSeconds = if (formattedTime.length == 5) "$formattedTime:00" else formattedTime

        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return LocalTime.parse(timeWithSeconds, formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatTime(time: LocalTime): String {
        return String.format("%02d:%02d:%02d", time.hour, time.minute, time.second)
    }
}
