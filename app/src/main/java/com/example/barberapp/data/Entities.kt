package com.example.barberapp.data

import androidx.room.*


// Tabela Barbershop
@Entity(tableName = "barbershops")
data class Barbershop(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
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
            parentColumns = ["id"],
            childColumns = ["barbershopId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Barber(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val bio: String,
    val barbershopId: Int
)

// Tabela Service
@Entity(tableName = "services")
data class Service(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String
)

// Tabela BarberService (conexão entre Barber e Service)
@Entity(
    tableName = "barber_services",
    foreignKeys = [
        ForeignKey(
            entity = Barber::class,
            parentColumns = ["id"],
            childColumns = ["barberId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Service::class,
            parentColumns = ["id"],
            childColumns = ["serviceId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
// Serviços por barbeiro
data class BarberService(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val barberId: Int,
    val serviceId: Int,
    val duration: Int,
    val price: Double,
)

// Tabela BarberSchedule
@Entity(
    tableName = "barber_schedules",
    foreignKeys = [
        ForeignKey(
            entity = Barber::class,
            parentColumns = ["id"],
            childColumns = ["barberId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BarberSchedule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val barberId: Int,
    val dayOfWeek: Int, // 0 (Domingo) a 6 (Sábado)
    val startTime: String, // Ex.: "09:00"
    val endTime: String // Ex.: "17:00"
)

// Tabela Client
@Entity(tableName = "clients")
data class Client(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
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
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BarberService::class,
            parentColumns = ["id"],
            childColumns = ["barberServiceId"],
            onDelete = ForeignKey.CASCADE
        ),
        /*ForeignKey(
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
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientId: Int,
    //val barberId: Int,
    //val serviceId: Int,
    val barberServiceId: Int,
    val date: String, // formato: YYYY-MM-DD
    val time: String, // formato: HH:MM
    val status: String // "Ativo", "Concluído", "Cancelado"
)







