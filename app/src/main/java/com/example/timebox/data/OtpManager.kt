package com.example.timebox.data

import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class OtpManager {

    private data class OtpData(
        val otp: String,
        val creationTime: Long,
        var attempts: Int = 0
    )

    private val otpStore = ConcurrentHashMap<String, OtpData>()

    companion object {
        private const val OTP_EXPIRATION_SECONDS = 60
        private const val MAX_ATTEMPTS = 3
    }

    fun generateOtp(email: String): String {
        val otp = (100000..999999).random().toString()
        otpStore[email] = OtpData(otp, System.currentTimeMillis())
        return otp
    }

    fun validateOtp(email: String, otp: String): OtpVerificationResult {
        val otpData = otpStore[email] ?: return OtpVerificationResult.Failure.Invalid

        if (isOtpExpired(otpData)) {
            otpStore.remove(email) // Clean up expired OTP
            return OtpVerificationResult.Failure.Expired
        }

        if (otpData.attempts >= MAX_ATTEMPTS) {
            return OtpVerificationResult.Failure.MaxAttemptsReached
        }

        if (otpData.otp == otp) {
            otpStore.remove(email) // OTP is single-use
            return OtpVerificationResult.Success
        } else {
            otpData.attempts++
            val remainingAttempts = MAX_ATTEMPTS - otpData.attempts
            return OtpVerificationResult.Failure.Incorrect(remainingAttempts)
        }
    }

    /**
     * Logs the current state of the otpStore to Logcat for debugging.
     */
    fun logOtpStoreStateForDebugging() {
        if (otpStore.isEmpty()) {
            Timber.d("OtpStore is currently empty.")
            return
        }
        Timber.d("--- Current OtpStore State ---")
        otpStore.forEach { (email, data) ->
            val elapsedTime = (System.currentTimeMillis() - data.creationTime) / 1000
            Timber.d("  Email: %s | OTP: %s | Attempts: %d | Age: %ds", email, data.otp, data.attempts, elapsedTime)
        }
        Timber.d("----------------------------")
    }

    private fun isOtpExpired(otpData: OtpData): Boolean {
        val elapsedTime = System.currentTimeMillis() - otpData.creationTime
        return TimeUnit.MILLISECONDS.toSeconds(elapsedTime) > OTP_EXPIRATION_SECONDS
    }
}

sealed class OtpVerificationResult {
    data object Success : OtpVerificationResult()
    sealed class Failure : OtpVerificationResult() {
        data object Invalid : Failure()
        data object Expired : Failure()
        data class Incorrect(val remainingAttempts: Int) : Failure()
        data object MaxAttemptsReached : Failure()
    }
}
