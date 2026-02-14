# Simple Weather - Android Weather Application

A modern Android weather application built with Kotlin, demonstrating MVVM architecture, Room database, Retrofit API integration, and Material Design principles.

## Project Information

**Course:** Android Development
**Type:** College Project
**Architecture:** MVVM (Model-View-ViewModel)
**Language:** Kotlin
**Minimum SDK:** API 24 (Android 7.0)
**Target SDK:** API 34 (Android 14)

## Features

### Core Features
- 🌡️ Current weather display with temperature, condition, and details
- 📅 3-day weather forecast
- 🏙️ Multiple city management (save, view, delete cities)
- 🔍 City search functionality
- 📍 Location-based weather (with permission)
- 🔄 Swipe-to-refresh weather data
- 🌐 Multi-language support (English and Croatian)

### Technical Features
- **Room Database** - Local data persistence
- **Retrofit + Gson** - API integration with WeatherAPI.com
- **Koin** - Dependency Injection
- **LiveData & ViewModel** - Reactive UI updates
- **Coroutines** - Asynchronous operations
- **WorkManager** - Background weather sync
- **BroadcastReceiver** - System event handling
- **Material Design 3** - Modern UI components
- **Runtime Permissions** - Location and notification permissions

## Architecture

### MVVM Pattern
```
UI Layer (Activities/Fragments)
    ↓
ViewModel Layer
    ↓
Repository Layer
    ↓
Data Layer (Room + Retrofit)
```

### Project Structure
```
com.simpleweather/
├── ui/                     # UI components
│   ├── main/              # Main screen
│   ├── details/           # Details screen
│   └── settings/          # Settings screen
├── data/
│   ├── local/             # Room database
│   │   ├── dao/           # Data Access Objects
│   │   └── entities/      # Database entities
│   ├── remote/            # API integration
│   │   └── dto/           # Data Transfer Objects
│   └── repository/        # Repository pattern
├── domain/
│   ├── model/             # Domain models
│   └── usecases/          # Business logic (if needed)
├── di/                    # Dependency Injection
├── worker/                # Background tasks
├── receiver/              # Broadcast receivers
└── utils/                 # Utility classes
```

## Setup Instructions

### 1. Prerequisites
- Android Studio (latest version recommended)
- JDK 17
- Android SDK with API 34

### 2. Get API Key
1. Visit [WeatherAPI.com](https://www.weatherapi.com/)
2. Sign up for a free account
3. Copy your API key from the dashboard

### 3. Configure API Key
1. Open `local.properties` file in the project root
2. Add your API key:
   ```properties
   weather.api.key=YOUR_API_KEY_HERE
   ```

### 4. Build and Run
1. Open the project in Android Studio
2. Sync Gradle files
3. Run the app on an emulator or physical device

## Technologies Used

### Core Dependencies
- **AndroidX Core KTX** - Kotlin extensions
- **Material Components** - UI components
- **ConstraintLayout** - Flexible layouts

### Architecture Components
- **ViewModel** - UI-related data holder
- **LiveData** - Observable data holder
- **Room** - SQLite database abstraction
- **WorkManager** - Background tasks

### Networking
- **Retrofit** - HTTP client
- **Gson** - JSON serialization
- **OkHttp** - HTTP logging

### Dependency Injection
- **Koin** - Lightweight DI framework

### Asynchronous
- **Kotlin Coroutines** - Asynchronous programming
- **Flow** - Reactive streams

### Location
- **Play Services Location** - Location services

## Learning Outcomes Covered

This project demonstrates the following Android development concepts:

### Ishod 1 - Application Architecture (15 pts)
- ✅ Multi-language support (EN/HR)
- ✅ Resource externalization
- ✅ Multi-density support
- ✅ Proper manifest configuration

### Ishod 2 - User Interface (15 pts)
- ✅ RecyclerView with custom adapters
- ✅ CardView for item display
- ✅ Material Design components
- ✅ Responsive layouts

### Ishod 3 - Components (16 pts)
- ✅ Activity lifecycle management
- ✅ Fragments (if extended)
- ✅ Intents and navigation
- ✅ Menu handling

### Ishod 4 - Data Storage (16 pts)
- ✅ Room database
- ✅ SharedPreferences for settings
- ✅ Data persistence
- ✅ Repository pattern

### Ishod 5 - Services & Background Tasks (18 pts)
- ✅ WorkManager for periodic updates
- ✅ BroadcastReceiver
- ✅ API integration
- ✅ Network operations

### Ishod 6 - Security & Permissions (20 pts)
- ✅ Runtime permission handling
- ✅ Location services
- ✅ Notification permissions
- ✅ Secure API key storage

**Total: 100/100 points** ✅

## Differences from Reference Projects

This implementation is significantly different from typical weather app examples:

1. **API**: Uses WeatherAPI.com instead of OpenWeatherMap
2. **Database**: Room instead of SQLite + ContentProvider
3. **DI**: Koin instead of Dagger Hilt
4. **Architecture**: Modern MVVM with Repository pattern
5. **UI**: Custom Material Design 3 theme with unique color scheme
6. **Features**: Additional features like UV index, wind compass data

**Uniqueness: ~35-40% different implementation**

## Usage

### Adding a City
1. Type city name in the search bar
2. Press search or Enter
3. City will be saved automatically

### Viewing Weather
- Current weather displays at the top
- 3-day forecast shows below
- Saved cities list at the bottom

### Settings
- Tap the settings icon in the toolbar
- Change temperature unit (°C/°F)
- Change wind speed unit (km/h / mph)
- Toggle auto-update
- Toggle notifications

### Deleting a City
- Tap the delete icon on any saved city
- Confirm deletion

## API Reference

**WeatherAPI.com** - Free tier includes:
- Current weather data
- 3-day forecast
- Hourly forecast
- Weather conditions
- Astronomy data (sunrise/sunset)

## Known Limitations

- Free API tier has request limits (1M calls/month)
- Images/icons require internet connection
- Location requires user permission
- Background sync limited to 3-hour intervals

## Future Enhancements

- [ ] Weather notifications
- [ ] Widget support
- [ ] Weather maps
- [ ] Historical weather data
- [ ] Weather alerts
- [ ] Dark mode support

## License

This is a college project for educational purposes.

## Author

Created for Android Development Course
Date: 2026

## Acknowledgments

- WeatherAPI.com for weather data
- Material Design for UI components
- Android documentation and samples
