package com.example.timebox

import android.app.Application
import com.example.timebox.analytics.AnalyticsLogger

class TimeBoxApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AnalyticsLogger.init()
    }
}
