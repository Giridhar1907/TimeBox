package com.example.timebox.viewmodel

sealed class AuthState {
    data object Idle : AuthState()
    data class OtpSent(val email: String) : AuthState()
    data class OtpVerificationFailed(val email: String, val reason: FailureReason) : AuthState()
    data class SessionActive(val email: String, val sessionStartTime: Long) : AuthState()
    data object LoggedOut : AuthState()
}

sealed class FailureReason {
    data class Incorrect(val remainingAttempts: Int) : FailureReason()
    data object Expired : FailureReason()
    data object MaxAttemptsReached : FailureReason()
}
