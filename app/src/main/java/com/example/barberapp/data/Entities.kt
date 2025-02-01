package com.example.barberapp.data

import androidx.room.*
import java.sql.Time

// Barbershop table
@Entity(
    tableName = "barbershops",
    indices = [Index(value = ["name"], unique = true)]
    )
data class Barbershop(
    @PrimaryKey(autoGenerate = false) val barbershopId: Int,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Float
)

// Barber table
@Entity(
    tableName = "barbers",
    indices = [Index(value = ["email"], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = Barbershop::class,
            parentColumns = arrayOf("barbershopId"),
            childColumns = arrayOf("barbershopId"),
            //onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Barber(
    @PrimaryKey(autoGenerate = true) val barberId: Int = 0,
    val barbershopId: Int,
    val name: String,
    val email: String,
    val password: String,
    val bio: String

)

// Service table
@Entity(tableName = "services")
data class Service(
    @PrimaryKey(autoGenerate = false) val serviceId: Int,
    val name: String,
    val description: String
)

// BarberService table (conection between barber and Service)
@Entity(
    tableName = "barber_services",
    foreignKeys = [
        ForeignKey(
            entity = Barber::class,
            parentColumns = ["barberId"],
            childColumns = ["barberId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Service::class,
            parentColumns = ["serviceId"],
            childColumns = ["serviceId"],
            //onDelete = ForeignKey.CASCADE
        )
    ]
)
// Services by Barber
data class BarberService(
    @PrimaryKey(autoGenerate = true) val barberServiceId: Int = 0,
    val barberId: Int,
    val serviceId: Int,
    val duration: Time,
    val price: Double,
    val isActive: Boolean
)

// BarberSchedule table
@Entity(
    tableName = "barber_schedules",
    foreignKeys = [
        ForeignKey(
            entity = Barber::class,
            parentColumns = ["barberId"],
            childColumns = ["barberId"],
            //onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BarberSchedule(
    @PrimaryKey(autoGenerate = true) val barberScheduleId: Int = 0,
    val barberId: Int,
    val dayOfWeek: Int,
    val hours: String
)

// Client table
@Entity(
    tableName = "clients",
    indices = [Index(value = ["email"], unique = true)]
)
data class Client(
    @PrimaryKey(autoGenerate = true) val clientId: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val phone: String
)

// Appointmenttable
@Entity(
    tableName = "appointments",
    foreignKeys = [
        ForeignKey(
            entity = Client::class,
            parentColumns = ["clientId"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BarberService::class,
            parentColumns = ["barberServiceId"],
            childColumns = ["barberServiceId"],
        )
    ]
)
data class Appointment(
    @PrimaryKey(autoGenerate = true) val AppointmentId: Int = 0,
    val clientId: Int,
    val barberServiceId: Int,
    val date: String, // format: YYYY-MM-DD
    val time: String, // format: HH:MM
    val status: String // "Active", "Missed", "Canceled", "Completed".
)
@Entity(
    tableName = "ratings",
    foreignKeys = [
        ForeignKey(
            entity = Client::class,
            parentColumns = ["clientId"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Rating(
    @PrimaryKey(autoGenerate = true) val ratingId: Int = 0,
    val clientId: Int,
    val photoUrl: String,
    val rating: Double,
    val comment: String,
)






