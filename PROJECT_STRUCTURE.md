# Habit Tracker App - Project Structure

## Project Overview
```
HabitTrackerApp/
├── .github/workflows/          # GitHub Actions workflow configurations
├── app/                        # Main application module
│   ├── src/                    # Source code
│   │   ├── androidTest/        # Instrumented tests
│   │   ├── main/               # Main source code
│   │   │   ├── java/           # Kotlin/Java code
│   │   │   └── res/            # Resources
│   │   └── test/               # Unit tests
│   └── build.gradle            # App-level build configuration
├── build.gradle                # Project-level build configuration
├── settings.gradle             # Project settings
├── README.md                   # Project documentation
└── USER_GUIDE.md               # User guide
```

## Detailed File Structure

### Configuration Files
- `/HabitTrackerApp/.github/workflows/android.yml` - GitHub Actions workflow for CI/CD
- `/HabitTrackerApp/build.gradle` - Project-level build configuration
- `/HabitTrackerApp/settings.gradle` - Project settings
- `/HabitTrackerApp/app/build.gradle` - App-level build configuration with dependencies

### Documentation
- `/HabitTrackerApp/README.md` - Project overview and build instructions
- `/HabitTrackerApp/USER_GUIDE.md` - Detailed user guide

### Application Code

#### Core Files
- `/HabitTrackerApp/app/src/main/AndroidManifest.xml` - App manifest
- `/HabitTrackerApp/app/src/main/java/com/example/habittracker/HabitTrackerApplication.kt` - Application class

#### Data Layer
- `/HabitTrackerApp/app/src/main/java/com/example/habittracker/data/Habit.kt` - Habit entity
- `/HabitTrackerApp/app/src/main/java/com/example/habittracker/data/HabitEntry.kt` - Habit entry entity
- `/HabitTrackerApp/app/src/main/java/com/example/habittracker/data/HabitDao.kt` - Data access object for habits
- `/HabitTrackerApp/app/src/main/java/com/example/habittracker/data/HabitEntryDao.kt` - Data access object for habit entries
- `/HabitTrackerApp/app/src/main/java/com/example/habittracker/data/HabitDatabase.kt` - Room database setup
- `/HabitTrackerApp/app/src/main/java/com/example/habittracker/HabitRepository.kt` - Repository pattern implementation

#### UI Layer
- `/HabitTrackerApp/app/src/main/java/com/example/habittracker/ui/MainActivity.kt` - Main activity
- `/HabitTrackerApp/app/src/main/java/com/example/habittracker/ui/habits/HabitsFragment.kt` - Habits list fragment
- `/HabitTrackerApp/app/src/main/java/com/example/habittracker/ui/habits/HabitAdapter.kt` - RecyclerView adapter for habits
- `/HabitTrackerApp/app/src/main/java/com/example/habittracker/ui/habits/HabitsViewModel.kt` - ViewModel for habits
- `/HabitTrackerApp/app/src/main/java/com/example/habittracker/ui/habits/HabitDetailFragment.kt` - Habit detail fragment
- `/HabitTrackerApp/app/src/main/java/com/example/habittracker/ui/habits/AddEditHabitFragment.kt` - Add/edit habit fragment
- `/HabitTrackerApp/app/src/main/java/com/example/habittracker/ui/statistics/StatisticsFragment.kt` - Statistics fragment
- `/HabitTrackerApp/app/src/main/java/com/example/habittracker/ui/statistics/StatisticsViewModel.kt` - ViewModel for statistics
- `/HabitTrackerApp/app/src/main/java/com/example/habittracker/ui/settings/SettingsFragment.kt` - Settings fragment

#### Notifications
- `/HabitTrackerApp/app/src/main/java/com/example/habittracker/notifications/NotificationHelper.kt` - Notification handling
- `/HabitTrackerApp/app/src/main/java/com/example/habittracker/notifications/HabitReminderReceiver.kt` - Broadcast receiver
- `/HabitTrackerApp/app/src/main/java/com/example/habittracker/notifications/ReminderScheduler.kt` - Reminder scheduling

#### Utilities
- `/HabitTrackerApp/app/src/main/java/com/example/habittracker/utils/HabitTrackingUtils.kt` - Utility functions

### Resources

#### Layouts
- `/HabitTrackerApp/app/src/main/res/layout/activity_main.xml` - Main activity layout
- `/HabitTrackerApp/app/src/main/res/layout/fragment_habits.xml` - Habits fragment layout
- `/HabitTrackerApp/app/src/main/res/layout/item_habit.xml` - Habit list item layout
- `/HabitTrackerApp/app/src/main/res/layout/fragment_habit_detail.xml` - Habit detail layout
- `/HabitTrackerApp/app/src/main/res/layout/fragment_add_edit_habit.xml` - Add/edit habit layout
- `/HabitTrackerApp/app/src/main/res/layout/fragment_statistics.xml` - Statistics layout
- `/HabitTrackerApp/app/src/main/res/layout/fragment_settings.xml` - Settings layout

#### Navigation
- `/HabitTrackerApp/app/src/main/res/navigation/mobile_navigation.xml` - Navigation graph
- `/HabitTrackerApp/app/src/main/res/menu/bottom_nav_menu.xml` - Bottom navigation menu

#### Drawables
- `/HabitTrackerApp/app/src/main/res/drawable/ic_habits.xml` - Habits icon
- `/HabitTrackerApp/app/src/main/res/drawable/ic_statistics.xml` - Statistics icon
- `/HabitTrackerApp/app/src/main/res/drawable/ic_settings.xml` - Settings icon
- `/HabitTrackerApp/app/src/main/res/drawable/selected_color_circle.xml` - Selected color indicator

#### Values
- `/HabitTrackerApp/app/src/main/res/values/strings.xml` - String resources
- `/HabitTrackerApp/app/src/main/res/values/colors.xml` - Color resources
- `/HabitTrackerApp/app/src/main/res/values/styles.xml` - Style resources
- `/HabitTrackerApp/app/src/main/res/values/arrays.xml` - Array resources

### Tests
- `/HabitTrackerApp/app/src/androidTest/java/com/example/habittracker/HabitTrackerInstrumentedTest.kt` - Basic instrumented test
- `/HabitTrackerApp/app/src/androidTest/java/com/example/habittracker/data/HabitDatabaseTest.kt` - Database tests
- `/HabitTrackerApp/app/src/test/java/com/example/habittracker/utils/HabitTrackingUtilsTest.kt` - Utility tests
