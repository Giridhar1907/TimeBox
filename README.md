# TimeBox: A Modern Android Passwordless Auth Demo

![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android)
![Language](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin)

TimeBox is a sample Android application built to demonstrate a clean, modern, and robust passwordless authentication flow. It uses a locally generated One-Time Password (OTP) and is built entirely with **Jetpack Compose** and a state-driven **MVVM architecture**.

This project serves as a reference for implementing best practices in modern Android development, including unidirectional data flow, clear separation of concerns, and effective handling of UI state and business logic without a backend.


---

## Features

- ✅ **100% Kotlin & Jetpack Compose**: A completely modern UI toolkit and language.
- 🔐 **Passwordless OTP Login**: Secure login flow using a locally generated 6-digit OTP.
- 🕒 **Strict OTP Rules**:
    - **60-second** expiry timer for every OTP.
    - **3 maximum** validation attempts before the OTP is invalidated.
    - Requesting a new OTP automatically invalidates the old one.
- 📈 **Live Session Tracking**: An active session screen that displays the session start time and a live-updating duration timer that survives configuration changes.
- 🏗️ **Clean, State-Driven Architecture**: The UI is a direct function of the application's state, ensuring predictability and stability.
- 🪵 **Developer-Focused Logging**: Integrated with **Timber** for structured logging of key auth events. Includes a debug function to inspect the in-memory OTP data store.

---

## Architecture

The application follows a well-defined **MVVM (Model-View-ViewModel)** architecture with a unidirectional data flow.

```
   [ UI Layer (Compose) ] <--- Observes State -- [ ViewModel ] ---> Interacts With --> [ Data Layer (Manager) ]
           |                                        |                                        |
      (LoginScreen,      (Holds AuthState &      (Handles OTP Logic &      
       SessionScreen)       Business Logic)           In-Memory Store)
           |
           '---- Emits Events ----------------------->'
```

1.  **UI Layer (Views)**: Built with Jetpack Compose. Observes state changes from the `AuthViewModel` and emits user events (like button clicks).
2.  **ViewModel Layer**: The `AuthViewModel` holds the application state (`AuthState`) as a `StateFlow`. It executes business logic and updates the state in response to UI events.
3.  **Data Layer**: The `OtpManager` is a pure Kotlin class that encapsulates all data-related rules, such as OTP generation, storage, and validation.

---

## Key Concepts Demonstrated

This project is a practical example of:

- **State Management**: Using a `sealed class` (`AuthState`) to represent all possible UI states.
- **State Hoisting**: Managing state at the highest logical level (`AuthViewModel`) and passing it down.
- **Side-Effect Handling**: Using `LaunchedEffect` to handle one-time events like navigation and showing `Snackbars`.
- **ViewModel & Coroutines**: Using `viewModelScope` to manage long-running tasks like timers, ensuring they are lifecycle-aware.
- **Dependency Encapsulation**: Using a wrapper (`AnalyticsLogger`) to decouple the ViewModel from the specific logging library.
- **Clean Architecture Principles**: A strict separation of concerns between UI, state, and data logic.

---

## Setup and Usage

### Prerequisites

- Android Studio Iguana | 2023.2.1 or newer.
- Git.

### Get Started

1.  **Clone the Repository**:
    ```sh
    git clone <repository-url>
    ```
2.  **Open in Android Studio**:
    - Launch Android Studio.
    - Select **Open** and navigate to the cloned project directory.
    - Let Gradle sync and download all dependencies.
3.  **Run the App**:
    - Select the `app` run configuration.
    - Choose a target device (emulator or physical).
    - Click the **Run** button (▶️).

### How to Log In

Because the app works locally, the OTP is shown to you for testing:

1.  **Snackbar**: After requesting an OTP, a Snackbar will pop up for 3 seconds with the code.
2.  **Logcat**: The OTP is also printed to **Logcat**, which you can view in Android Studio.

