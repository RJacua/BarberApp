import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.UtilityClasses.AppointmentWithDuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    /**
     * Load schedules
     * Load schedules and filter available time slots.
     *
     * @param barberId
     * @param dayOfWeek
     * @param date
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadSchedules(barberId: Int, dayOfWeek: Int, date: String) {
        viewModelScope.launch(Dispatchers.IO) {
        // Retrieve schedules and appointments from the database
        val schedules = scheduleDao.getSchedulesByDay(barberId, dayOfWeek)
        val appointments = appointmentDao.getAppointmentsWithDurationByBarber(barberId, date)
        Log.d("Horario", "Schedules found: $schedules")
        Log.d("Horario", "Appointments found: $appointments")

        if (schedules.isNotEmpty()) {
            // Extract working hours
            val hours = schedules.first().hours
            Log.d("Horario", "Working hours: $hours")

            // Filter available slots
            val availableSlots = filterAvailableSlots(hours, appointments, date)
            val (morning, afternoon) = partitionSlots(availableSlots)

            // Update LiveData for morning and afternoon slots
            // 🔹 Update LiveData on the Main Thread
            withContext(Dispatchers.Main) {
                _morningSlots.value = morning
                _afternoonSlots.value = afternoon
            }
//            _morningSlots.value = morning
//            _afternoonSlots.value = afternoon
            Log.d("Horario", "Morning: $morning, Afternoon: $afternoon")

//            // Atualiza o LiveData na Main Thread
//            _morningSlots.postValue(morning)
//            _afternoonSlots.postValue(afternoon)
        }
        }
    }
    /**
     * Filter available slots
     * Filter the list of available time slots by removing already booked ones
     *
     * @param hours
     * @param appointments
     * @param selectedDate
     * @return
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun filterAvailableSlots(
        hours: String, appointments: List<AppointmentWithDuration>, selectedDate: String
    ): List<String> {
        // Generate all possible time slots
        val allSlots = hours.split(",").flatMap { generateSlots(it.toInt()) }
        Log.d("Horario", "Todos os Horarios: $allSlots")

        val now = getRoundedCurrentTime()
        val todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-M-d"))

        // Filter slots based on the selected date
        val filteredSlots = if (selectedDate == todayDate) {
            allSlots.filter { parseTime(it) >= now }
        } else {
            allSlots
        }

        // Calculate blocked slots from existing appointments
        val blockedSlots = appointments.flatMap { appointment ->
            val startTime = parseTime(appointment.time)
            val durationInMinutes = appointment.duration.hours * 60 + appointment.duration.minutes
            val endTime = startTime.plusMinutes(durationInMinutes.toLong())

            generateSequence(startTime) { it.plusMinutes(15) }.takeWhile {
                    !it.isAfter(
                        endTime.minusMinutes(
                            15
                        )
                    )
                }.map { formatTime(it) }.toList()
        }.toSet()

        Log.d("Horario", "Horarios Bloqueados: $blockedSlots")

        // Remove blocked slots from the list of all slots
        val availableSlots = filteredSlots.filter { it !in blockedSlots }
        Log.d("Horario", "Horarios Disponiveis: $availableSlots")

        return availableSlots
    }

    /**
     * Get rounded current time
     * Get the current time and round it to the next 15-minute block
     *
     * @return
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getRoundedCurrentTime(): LocalTime {
        val now = LocalTime.now()
        val roundedMinutes = ((now.minute / 15) + 1) * 15

        return if (roundedMinutes >= 60) {
            now.plusHours(1).withMinute(0).withSecond(0)
        } else {
            now.withMinute(roundedMinutes).withSecond(0)
        }
    }

    /**
     * Partition slots
     * Partition slots into morning and afternoon categories
     *
     * @param slots
     * @return
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun partitionSlots(slots: List<String>): Pair<List<String>, List<String>> {
        val morning = slots.filter { parseTime(it) < LocalTime.of(12, 0) }
        val afternoon = slots.filter { parseTime(it) >= LocalTime.of(12, 0) }
        return Pair(morning, afternoon)
    }

    /**
     * Generate slots
     * Generate 15-minute intervals for a given hour
     *
     * @param hour
     * @return
     */
    private fun generateSlots(hour: Int): List<String> {
        return (0..45 step 15).map { minute ->
            String.format("%02d:%02d:00", hour, minute)
        }
    }

    /**
     * Parse time
     * Parse a time string into a LocalTime object, adding leading zeroes if necessary
     *
     * @param time
     * @return
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseTime(time: String): LocalTime {
        val formattedTime = if (time.length == 7) "0$time" else time
        val timeWithSeconds = if (formattedTime.length == 5) "$formattedTime:00" else formattedTime
        return LocalTime.parse(timeWithSeconds, DateTimeFormatter.ofPattern("HH:mm:ss"))
    }

    /**
     * Format time
     * Format a LocalTime object into a string with HH:mm:ss format
     *
     * @param time
     * @return
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatTime(time: LocalTime): String {
        return time.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
    }
}
