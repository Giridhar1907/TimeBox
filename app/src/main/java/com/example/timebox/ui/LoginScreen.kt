package com.example.timebox.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.timebox.viewmodel.AuthViewModel
import com.example.timebox.viewmodel.AuthState
import com.example.timebox.viewmodel.FailureReason
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(authViewModel: AuthViewModel) {
    val authState by authViewModel.authState.collectAsState()
    val otpTimer by authViewModel.otpTimer.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        authViewModel.otpForNotification.collect { otp ->
            scope.launch {
                snackbarHostState.showSnackbar("OTP: $otp", duration = androidx.compose.material3.SnackbarDuration.Short)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    when (val state = authState) {
                        is AuthState.Idle, is AuthState.LoggedOut -> {
                            Text("Login", style = MaterialTheme.typography.headlineMedium)
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { authViewModel.login(email) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Get OTP")
                            }
                        }

                        is AuthState.OtpSent, is AuthState.OtpVerificationFailed -> {
                            val currentEmail = if (state is AuthState.OtpSent) state.email else (state as AuthState.OtpVerificationFailed).email

                            Text("Enter OTP", style = MaterialTheme.typography.headlineMedium)
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = currentEmail,
                                onValueChange = {},
                                label = { Text("Email") },
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = otp,
                                onValueChange = { otp = it },
                                label = { Text("OTP") },
                                isError = state is AuthState.OtpVerificationFailed,
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (state is AuthState.OtpVerificationFailed) {
                                val errorMessage = when (val reason = state.reason) {
                                    is FailureReason.Incorrect -> "Incorrect OTP. ${reason.remainingAttempts} attempts remaining."
                                    is FailureReason.Expired -> "OTP has expired. Please request a new one."
                                    is FailureReason.MaxAttemptsReached -> "Max attempts reached. Please request a new OTP."
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { authViewModel.verifyOtp(otp) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Verify OTP")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "OTP expires in: ${otpTimer}s",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            TextButton(onClick = { authViewModel.resendOtp() }) {
                                Text("Resend OTP")
                            }
                        }

                        is AuthState.SessionActive -> {
                            // Handled by navigation
                        }
                    }
                }
            }
        }
    }
}
