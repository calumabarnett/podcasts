# Project State

This document is the single source of truth for the project. It tracks decisions, progress, and technical designs.

## 1. Project Overview

We are building a minimalist podcast application. The application is modeled after the Google Podcasts user experience.

- **Objective**: Create a clean, fast, and simple podcast client.
- **Platforms**: Android and iOS. We prioritize Android first.
- **Architecture**: Kotlin Multiplatform (KMP). Data and domain layers are abstract and platform independent.

## 2. Technical Stack

- **UI Framework**: Compose Multiplatform with Material 3.
- **Audio Playback**: Android uses Media3. iOS uses a native wrapper around AVPlayer.
- **Local Storage**: Room KMP. State is stored locally. No cloud sync is used.
- **Networking**: Ktor Client.
- **Feed Parsing**: kotlinx-serialization-xml.
- **Dependency Injection**: Koin.
- **Testing Frameworks**: Kotest for common logic unit and behavior tests. Standard Compose UI tests for user interface tests.

## 3. Product Features

### Home Screen
- A grid of subscribed podcast shows.
- A list of episodes currently in progress.

### Search Screen
- Search for podcasts using the Podcast Index API.
- Since real credentials might not be present, a mock search fallback is available based on the actual API specification.

### Episode Details Screen
- Show the description, publish date, and duration.
- Include a play button.

### Player UI
- Bottom sheet mini player.
- Full screen player showing:
  - Play and pause controls.
  - Seek bar.
  - Skip forward and backward buttons.
  - Playback speed control.
- No sleep timer is included.

### Downloads
- Streaming only. No offline download features.

## 4. Architectural Rules

1. **Continuous Review**: We review code, design, and performance decisions critically.
2. **Challenge Decisions**: Push back against choices that harm quality or performance.
3. **No Code Pollution**: Write clean, idiomatic code. No placeholder comments, no explanatory comments, and no todo notes.
4. **Simple Documentation**: Use plain English, no semicolons, and no cliches.

## 5. Next Steps / Repository Initialization Roadmap

1. **Gradle and Multiplatform Configuration**: [COMPLETED]
   - Set up root `build.gradle.kts`, `settings.gradle.kts`, and Gradle wrapper.
   - Set up `shared` module with multiplatform targets (Android, iOS).
   - Set up `androidApp` module for the Android-specific target and application entry point.
   - Configure BuildKonfig to handle API keys securely.
2. **Domain Models**: [COMPLETED]
   - Define core immutable models: `Podcast`, `Episode`, `PlaybackState`.
3. **Networking & Feed Parsing**: [COMPLETED]
   - Set up Ktor HTTP Client.
   - Set up kotlinx-serialization-xml.
   - Implement Podcast Index API search client (with a mock mode if keys are absent or empty).
4. **Local Database**: [COMPLETED]
   - Set up Room KMP database in `shared`.
   - Implement DAOs and entities for Subscribed Shows and In-Progress Episodes.
5. **Audio Player Abstract & Implementation**: [COMPLETED]
   - Define a shared interface for playback control (`AudioPlayer`).
   - Implement Android platform-specific `AudioPlayer` using Android Media3.
6. **Compose Multiplatform UI**: [COMPLETED]
   - Home Screen (grid of subscriptions, list of in-progress episodes).
   - Search Screen.
   - Episode Details Screen.
   - Mini Player bottom sheet and Full-Screen Player with Play/Pause, seek bar, skip controls, and speed control.
7. **Verification and Testing**: [COMPLETED]
   - Behavior-driven tests for view models, search service, and database.
