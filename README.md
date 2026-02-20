<div align="center" style="width: 150px; margin: 0 auto;">
  <img src="assets/banner.png" alt="VGM APEX Logo">
  <h1>Video Game Music Audio Player EX</h1>
</div>

## 🎮 A Spotify-like Experience for Video Game Music Enthusiasts

VGM APEX is a dedicated audio streaming platform designed specifically for video game music lovers. Explore thousands of tracks from your favorite games, create custom playlists, and discover new soundtracks all in one elegant application.

## ✨ Features

- **Extensive Game Music Library**: Browse through thousands of tracks from classic to modern video games
- **Seamless Looping**: Loop your favourite soundtrack seamlessly without having to listen to the intro everytime, just like in the actual games!
- **Album & Track Info**: View detailed information about tracks and albums with beautiful album art
- **Search & History**: Instant search functionality with search history and play history tracking
- **Offline Support**: Download your favorite tracks for offline listening
- **Shuffle Mode**: Randomize your listening experience with the shuffle functionality
- **Queue Management**: View and manage your current play queue
- **Player Controls**: 
  - Seek through tracks with the SeekBar
  - Control playback from your phone's notification panel
  - Play all tracks in a category at once (Album, Uploader, Random Picks, etc.)
- **Modern UI Elements**:
  - Shimmer animations for loading content
  - Interactive tap animations
  - Marquee effect for long track names
  - Dark/Light Theme that changes automatically with system settings
- **Customizable UI**
  - Choose between different themes and color accents
  - Choose between different layouts for the home screen
  - Adjust animation speed
- Regular updates with new features and improvements
- ~~**Personalized Experience**: Create custom playlists, mark favorites, and get recommendations based on your listening habits~~ (Coming Soon!)
- ~~**Advanced Search**: Find music by game, composer, genre, console, or year~~ (Coming Soon!)
- ~~**Social Features**: Share your favorite tracks and playlists with friends~~ (Coming Soon!)

## 📱 Screenshots
<div align="center">
  <img src="assets/screenshot_home.jpg" alt="Home Screen" width="200"/>
  <img src="assets/screenshot_library.jpg" alt="Library Screen" width="200"/>
  <img src="assets/screenshot_search.jpg" alt="Searches Screen" width="200"/>
  <img src="assets/screenshot_player.jpg" alt="Player Screen" width="200"/>
</div>

## 🚀 Getting Started

### Prerequisites

- Android 9+
- Internet connection for streaming (offline mode coming soon!)

### Download

#### Mobile
- [Download from GitHub Releases](https://github.com/V-Play-Games/VGM-APEX/releases/latest)
- [Download on Google Play] (Coming Soon!)

## 🔧 For Developers

### Tech Stack

This is a Kotlin Multiplatform project targeting Android and Server.

- **Frontend**: Kotlin, Jetpack Compose
- **Backend**: Ktor
- **Database**: MongoDB
- **Audio Storage**: GitHub Repositories :3 (S3 buckets soon maybe?)

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
    - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
    - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.

* [/server](./server/src/main/kotlin) is for the Ktor server application.

* [/shared](./shared/src) is for the code that will be shared between all targets in the project.
  The most important subfolder is [commonMain](./shared/src/commonMain/kotlin). If preferred, you
  can add code to the platform-specific folders here too.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:

- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run Server

To build and run the development version of the server, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:

- on macOS/Linux
  ```shell
  ./gradlew :server:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :server:run
  ```
