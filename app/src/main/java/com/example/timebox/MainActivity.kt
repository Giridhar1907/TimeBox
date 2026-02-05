package com.example.timebox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.timebox.ui.LoginScreen
import com.example.timebox.ui.SessionScreen
import com.example.timebox.ui.theme.TimeBoxTheme
import com.example.timebox.viewmodel.AuthViewModel
import com.example.timebox.viewmodel.AuthState

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimeBoxTheme {
                AppNavigation(authViewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(authViewModel: AuthViewModel = viewModel()) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()

    // This LaunchedEffect will handle navigation events in a lifecycle-aware manner.
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.SessionActive -> {
                navController.navigate("session") { popUpTo("auth") { inclusive = true } }
            }
            is AuthState.LoggedOut -> {
                navController.navigate("auth") { popUpTo("session") { inclusive = true } }
            }
            else -> Unit // Other states are handled within the LoginScreen itself
        }
    }

    NavHost(navController = navController, startDestination = "auth") {
        composable("auth") {
            LoginScreen(authViewModel = authViewModel)
        }
        composable("session") {
            val sessionState = authState as? AuthState.SessionActive
            if (sessionState != null) {
                SessionScreen(
                    authViewModel = authViewModel,
                    sessionStartTime = sessionState.sessionStartTime,
                    email = sessionState.email
                )
            }
        }
    }
}
