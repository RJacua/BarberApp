package com.example.barberapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [
    Client::class,
    Barber::class,
    Barbershop::class,
    Service::class,
    BarberSchedule::class,
    BarberService::class,
    Appointment::class
], version = 1)
@TypeConverters(LocalDateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun clientDao(): ClientDao
    abstract fun barberDao(): BarberDao
    abstract fun barbershopDao(): BarberShopDao
    abstract fun serviceDao(): ServiceDao
    abstract fun barberscheduleDao(): BarberScheduleDao
    abstract  fun barberserviceDao(): BarberServiceDao
    abstract fun appointmentDao(): AppointmentDao

    companion object {

        private val lock = Any()

        @Volatile
        private var instance: AppDatabase? = null


        operator fun invoke(context: Context): AppDatabase {

            return instance ?: synchronized(lock) {
                if (instance != null) return instance!!

                instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "app.db"
                ).allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()

                return instance!!
            }
        }
    }
}