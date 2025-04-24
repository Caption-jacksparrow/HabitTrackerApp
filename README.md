# Habit Tracker App

A simple habit tracking application built with Kotlin that helps users create, track, and maintain habits.

## Features

- Create and manage habits with customizable names, descriptions, and colors
- Track habit completion with daily, weekly, monthly, or custom frequencies
- View detailed statistics about habit completion rates and streaks
- Set reminders for habits at specific times
- Daily summary notifications to review your progress
- Clean and intuitive Material Design UI
- Dark mode support

## Screenshots

(Screenshots would be available after building the app)

## Architecture

This app is built using:

- **Kotlin** - Primary programming language
- **MVVM Architecture** - For separation of concerns and maintainability
- **Room Database** - For local data persistence
- **LiveData & Flow** - For reactive UI updates
- **Coroutines** - For asynchronous operations
- **Navigation Component** - For fragment navigation
- **WorkManager** - For scheduling background tasks
- **Material Design Components** - For UI elements

## Project Structure

- `app/src/main/java/com/example/habittracker/`
  - `data/` - Database, entities, and DAOs
  - `ui/` - Activities, fragments, and view models
  - `notifications/` - Notification and reminder handling
  - `utils/` - Utility classes

## Building the App

### Using GitHub Actions

This repository is configured with GitHub Actions to automatically build the app when changes are pushed to the main branch.

1. Fork or clone this repository
2. Push changes to the main branch
3. GitHub Actions will automatically build the app
4. Download the APK from the Actions tab in your GitHub repository

### Building Locally

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/habit-tracker-app.git
   ```

2. Open the project in Android Studio

3. Build the project:
   ```
   ./gradlew build
   ```

4. Install the app on your device:
   ```
   ./gradlew installDebug
   ```

## Testing

The app includes both unit tests and instrumented tests:

- Run unit tests:
  ```
  ./gradlew test
  ```

- Run instrumented tests:
  ```
  ./gradlew connectedAndroidTest
  ```

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Material Design Icons
- Android Jetpack libraries
