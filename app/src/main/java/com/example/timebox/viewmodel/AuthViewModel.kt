package com.example.timebox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timebox.analytics.AnalyticsLogger
import com.example.timebox.data.OtpManager
import com.example.timebox.data.OtpVerificationResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val otpManager = OtpManager()
    private val analyticsLogger = AnalyticsLogger

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _otpForNotification = MutableSharedFlow<String>()
    val otpForNotification = _otpForNotification.asSharedFlow()

    private val _otpTimer = MutableStateFlow(0)
    val otpTimer: StateFlow<Int> = _otpTimer.asStateFlow()
    private var otpTimerJob: Job? = null

    private val _sessionTimer = MutableStateFlow(0L)
    val sessionTimer: StateFlow<Long> = _sessionTimer.asStateFlow()
    private var sessionTimerJob: Job? = null

    fun login(email: String) {
        val otp = otpManager.generateOtp(email)
        analyticsLogger.logOtpGenerated(email, otp)

        viewModelScope.launch {
            _otpForNotification.emit(otp)
        }

        _authState.value = AuthState.OtpSent(email)
        startOtpTimer()
    }

    fun verifyOtp(otp: String) {
        val currentEmail = when(val state = _authState.value) {
            is AuthState.OtpSent -> state.email
            is AuthState.OtpVerificationFailed -> state.email
            else -> return
        }

        when (val result = otpManager.validateOtp(currentEmail, otp)) {
            is OtpVerificationResult.Success -> {
                analyticsLogger.logOtpValidationSuccess(currentEmail)
                startSession()
            }
            is OtpVerificationResult.Failure.Incorrect -> {
                analyticsLogger.logOtpValidationFailed(currentEmail, "Incorrect OTP")
                _authState.value = AuthState.OtpVerificationFailed(currentEmail, FailureReason.Incorrect(result.remainingAttempts))
            }
            is OtpVerificationResult.Failure.Expired -> {
                analyticsLogger.logOtpValidationFailed(currentEmail, "OTP Expired")
                 _authState.value = AuthState.OtpVerificationFailed(currentEmail, FailureReason.Expired)
            }
            is OtpVerificationResult.Failure.MaxAttemptsReached -> {
                 analyticsLogger.logOtpValidationFailed(currentEmail, "Max attempts reached")
                 _authState.value = AuthState.OtpVerificationFailed(currentEmail, FailureReason.MaxAttemptsReached)
            }
            is OtpVerificationResult.Failure.Invalid -> {
                // This case should ideally not happen if the UI flow is correct
                analyticsLogger.logOtpValidationFailed(currentEmail, "Invalid OTP")
            }
        }
    }

    fun resendOtp() {
        val currentEmail = when(val state = _authState.value) {
            is AuthState.OtpSent -> state.email
            is AuthState.OtpVerificationFailed -> state.email
            else -> return
        }
        login(currentEmail)
    }

    fun logout() {
        val currentState = _authState.value
        if (currentState is AuthState.SessionActive) {
            analyticsLogger.logLogout(currentState.email)
        }
        stopSessionTimer()
        _authState.value = AuthState.LoggedOut
    }

    private fun startSession() {
        val currentEmail = (_authState.value as? AuthState.OtpSent)?.email ?: return
        _authState.value = AuthState.SessionActive(currentEmail, System.currentTimeMillis())
        startSessionTimer()
    }

    private fun startOtpTimer(durationSeconds: Int = 60) {
        otpTimerJob?.cancel()
        _otpTimer.value = durationSeconds
        otpTimerJob = viewModelScope.launch {
            while (_otpTimer.value > 0) {
                delay(1000)
                _otpTimer.value--
            }
        }
    }

    private fun startSessionTimer() {
        sessionTimerJob?.cancel()
        sessionTimerJob = viewModelScope.launch {
            _sessionTimer.value = 0
            while (true) {
                delay(1000)
                _sessionTimer.value++
            }
        }
    }

    private fun stopSessionTimer() {
        sessionTimerJob?.cancel()
        sessionTimerJob = null
        _sessionTimer.value = 0
    }

    override fun onCleared() {
        super.onCleared()
        otpTimerJob?.cancel()
        sessionTimerJob?.cancel()
    }
}
