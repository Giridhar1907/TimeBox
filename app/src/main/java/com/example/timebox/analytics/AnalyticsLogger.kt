package com.example.timebox.analytics

import timber.log.Timber

object AnalyticsLogger {

    fun init() {
        Timber.plant(Timber.DebugTree())
    }

    fun logOtpGenerated(email: String, otp: String) {
        Timber.d("OTP for %s: %s", email, otp)
    }

    fun logOtpValidationSuccess(email: String) {
        Timber.d("OTP validation successful for email: %s", email)
    }

    fun logOtpValidationFailed(email: String, reason: String) {
        Timber.w("OTP validation failed for email: %s, reason: %s", email, reason)
    }

    fun logLogout(email: String) {
        Timber.d("User logged out: %s", email)
    }
}
