# Minimalist Podcast Application

This is a minimalist podcast application modeled after the simple user experience of Google Podcasts. It is built using Kotlin Multiplatform (KMP), Compose Multiplatform, and Android Media3.

---

## Technical Stack

* **UI Framework**: Compose Multiplatform with Material 3
* **Audio Playback**: Android Media3 (ExoPlayer) on Android, native AVPlayer wrapper on iOS
* **Local Storage**: Room KMP (retains subscription status and episode listening positions)
* **Networking**: Ktor Client
* **Feed Parsing**: kotlinx-serialization-xml (using the `xmlutil` library)
* **Dependency Injection**: Koin
* **Testing Frameworks**: Kotest for business logic and standard Kotlin unit tests

---

## Directory Structure

The codebase is organized into modular components to keep the domain layers decoupled from platform-specific implementations:

* **`/shared`**: This module contains the core business logic and shared user interface:
  * `commonMain`: Immutable domain models, abstract interfaces, RSS XML feed parsing, the search client, and all Compose Multiplatform screens (Home, Search, Details, and Player).
  * `androidMain`: Android platform implementations, such as the Media3 playback service and the database builder.
  * `iosMain`: iOS platform implementations, including database builder setup and Koin target bindings.
* **`/androidApp`**: This module is the Android application entry point. It contains the launcher activity and initiates the shared Koin container.

---

## Setup Instructions

### Prerequisites
1. **Java Development Kit**: Make sure JDK 21 is installed.
2. **Android SDK**: Install the latest Android SDK platform packages (Compile SDK 35, Target SDK 34).

### API Credentials (Optional)
The application integrates with the **Podcast Index API** for discoverability.
1. Create a `local.properties` file in the root of the project if it does not exist.
2. Add your Podcast Index credentials using the following keys:
   ```properties
   podcast.index.api.key=YOUR_API_KEY
   podcast.index.api.secret=YOUR_API_SECRET
   ```
3. If these credentials are not supplied, the application automatically switches to a high-quality mock search mode. This mode uses curated real-world feeds like *The Daily* and *Huberman Lab*, making development and testing extremely simple.

---

## How to Run

To run and compile the application on different platforms, use the standard Gradle commands:

### Android Application
To build and install the debug application on an active Android device or emulator:
```bash
./gradlew :androidApp:installDebug
```

### Shared Multiplatform Module
To compile the Kotlin Multiplatform shared code specifically for the Android target:
```bash
./gradlew :shared:compileDebugKotlinAndroid
```

---

## How to Run Tests

To run the behavior-driven and unit tests designed for the common business logic and parsers, execute the following command:

```bash
./gradlew :shared:testDebugUnitTest
```

The test reports will be generated in HTML format. You can find them under:
`shared/build/reports/tests/testDebugUnitTest/index.html`

---

## How to Contribute

We follow modern software design standards and a strict testing-driven mindset. To contribute changes:

1. **Write Automated Tests**: For every change, write matching unit tests under the `commonTest` source set.
2. **Prune explanatory comments**: Write self-documenting and complete code. Avoid putting TODO comments, placeholders, or redundant explanatory text inside the source files.
3. **Verify Compilation**: Always run standard builds and verify all unit tests pass before proposing your changes.
