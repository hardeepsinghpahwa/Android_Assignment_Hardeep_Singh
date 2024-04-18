package com.example.assignment

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}