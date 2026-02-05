# TimeBox: Passwordless Authentication App

A simple and modern Android application demonstrating a passwordless authentication flow using a locally generated OTP (One-Time Password). The app is built with Jetpack Compose, Kotlin, and a clean, state-driven MVVM architecture.

This project was designed to showcase best practices in modern Android development, including unidirectional data flow, clear separation of concerns, and robust handling of UI state and business logic.

---

## Features

- **Email + OTP Login**: Secure login using a locally generated 6-digit OTP.
- **Strict OTP Rules**: 
    - **60-second** expiry timer.
    - **3 maximum** validation attempts.
    - Generating a new OTP invalidates the old one and resets the attempt counter.
- **Live Session Screen**: After login, a session screen displays the start time and a live-updating session duration timer.
- **State-Driven UI**: The entire UI is a function of the application's state, ensuring predictable and stable behavior.
- **Robust Architecture**: A clear separation between the UI, ViewModel (business logic), and Data layers.
- **Developer Logging**: Integrated **Timber** for structured logging of key authentication events. Includes helpers for debugging the in-memory OTP store.

## Tech Stack & Architecture

- **UI**: 100% Jetpack Compose.
- **Architecture**: Model-View-ViewModel (MVVM)
- **State Management**: `ViewModel` with `StateFlow` to manage and expose UI state.
- **Asynchronous Operations**: Kotlin Coroutines for managing timers and other async tasks.
- **Navigation**: Jetpack Compose Navigation for moving between screens.
- **Logging**: Timber for structured and extensible logging.

### Architectural Principles Adhered To:

- **Single Source of Truth**: The `AuthViewModel` holds the state, which the UI observes.
- **Unidirectional Data Flow**: State flows down from the ViewModel to the UI, and events flow up from the UI to the ViewModel.
- **No UI Logic in ViewModels**: The ViewModel knows nothing about Composables or Android framework UI components.
- **Encapsulated Business Logic**: All OTP rules are handled exclusively within the `OtpManager` in the data layer.

---

## Setup and Usage Instructions

### Prerequisites
- Android Studio Iguana | 2023.2.1 or newer.
- Git installed on your machine.

### 1. Clone the Repository

Open your terminal and clone the project repository to your local machine:

```sh
git clone <repository-url>
```

### 2. Open in Android Studio

- Launch Android Studio.
- Select **Open an existing project**.
- Navigate to the cloned `TimeBox` directory and open it.
- Allow Android Studio to sync the Gradle files and download all dependencies.

### 3. Build and Run

Once the Gradle sync is complete, you can build and run the application on an emulator or a physical device directly from Android Studio.

- Select a run configuration (usually `app`).
- Choose a target device.
- Click the **Run** button (▶️).

### 4. How to Log In (Testing the Flow)

Since this is a local-only implementation, the OTP is not sent to an actual email address. For testing purposes, the OTP is made available in two ways:

1.  **Snackbar Notification**: After you enter an email and tap "Get OTP", a Snackbar will appear at the bottom of the screen for 3 seconds displaying the OTP (e.g., `OTP: 123456`).
2.  **Logcat**: The OTP is also logged to **Logcat**. You can view this in Android Studio (`View > Tool Windows > Logcat`) by filtering for the `D/AnalyticsLogger` tag.

---

## Project Structure

```
/app/src/main/java/com/example/timebox/
├── analytics/
│   └── AnalyticsLogger.kt  # Wrapper for the logging library (Timber).
├── data/
│   └── OtpManager.kt       # Handles all OTP generation, storage, and validation logic.
├── ui/
│   ├── LoginScreen.kt      # Composable for email input and OTP verification.
│   └── SessionScreen.kt    # Composable for the active session view.
├── viewmodel/
│   ├── AuthState.kt        # Sealed classes defining all possible UI states.
│   └── AuthViewModel.kt    # The brain of the app; holds state and business logic.
├── MainActivity.kt         # Main entry point; handles navigation.
└── TimeBoxApplication.kt   # Initializes analytics on app startup.
```
