package com.example.barberapp

import android.app.Application

    class BarberApp : Application (){
        override fun onCreate() {
            super.onCreate()
            println("Hello World")
        }
}