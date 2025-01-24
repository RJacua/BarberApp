package com.example.barberapp.data

import androidx.room.*
import java.sql.Time

// Tabela Barbershop
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

// Tabela Barber
@Entity(
    tableName = "barbers",
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

// Tabela Service
@Entity(tableName = "services")
data class Service(
    @PrimaryKey(autoGenerate = false) val serviceId: Int,
    val name: String,
    val description: String
)

// Tabela BarberService (conexão entre Barber e Service)
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
// Serviços por barbeiro
data class BarberService(
    @PrimaryKey(autoGenerate = true) val barberServiceId: Int = 0,
    val barberId: Int,
    val serviceId: Int,
    val duration: Time,
    val price: Double
)

// Tabela BarberSchedule
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

// Tabela Client
@Entity(tableName = "clients")
data class Client(
    @PrimaryKey(autoGenerate = true) val clientId: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val phone: String
)

// Tabela Appointment (Marcação)
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
            onDelete = ForeignKey.CASCADE
        )
        /*,ForeignKey(
            entity = Barber::class,
            parentColumns = ["id"],
            childColumns = ["barberId"],
            onDelete = ForeignKey.CASCADE
        ),*/
        /*ForeignKey(
            entity = Service::class,
            parentColumns = ["id"],
            childColumns = ["serviceId"],
            onDelete = ForeignKey.CASCADE
        )*/
    ]
)
data class Appointment(
    @PrimaryKey(autoGenerate = true) val AppointmentId: Int = 0,
    val clientId: Int,
    //val barberId: Int,
    //val serviceId: Int,
    val barberServiceId: Int,
    val date: String, // formato: YYYY-MM-DD
    val time: String, // formato: HH:MM
    val status: String // "Ativo", "Concluído", "Cancelado"
)







