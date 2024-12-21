package com.example.simpletodo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import androidx.work.Configuration
import androidx.work.WorkManager

@HiltAndroidApp
class TodoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 配置 WorkManager
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
            
        WorkManager.initialize(this, config)
    }
} 