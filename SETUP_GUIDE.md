# Simple Weather - Quick Setup Guide

## Prerequisites
- Android Studio (Arctic Fox or newer)
- JDK 17
- Android SDK 34

## Step-by-Step Setup

### 1. Get Your API Key
1. Go to https://www.weatherapi.com/
2. Click "Sign Up" (it's free!)
3. After registration, go to your dashboard
4. Copy your API key

### 2. Configure the Project
1. Open the project in Android Studio
2. Find the file `local.properties` in the root directory
3. Replace `YOUR_API_KEY_HERE` with your actual API key:
   ```
   weather.api.key=abc123your_actual_key_here
   ```

### 3. Sync and Build
1. Click "Sync Now" when Android Studio prompts
2. Wait for Gradle sync to complete
3. Build → Make Project (or Ctrl+F9)

### 4. Run the App
1. Connect an Android device or start an emulator
2. Click the Run button (green triangle) or press Shift+F10
3. Select your device and wait for installation

## First Run

When you first run the app:

1. **Permission Requests**:
   - Grant Location permission (to get weather for your location)
   - Grant Notification permission (for weather updates)

2. **Add Your First City**:
   - Type a city name in the search bar (e.g., "Zagreb", "London", "New York")
   - Press Enter or click the search icon
   - Weather data will load and the city will be saved

3. **Explore Features**:
   - Swipe down to refresh weather
   - Tap saved cities to view their weather
   - Tap the settings icon to change units
   - Delete cities using the trash icon

## Troubleshooting

### App Won't Build
- Make sure you have JDK 17 installed
- Check that Android SDK 34 is installed
- Try: Build → Clean Project, then Build → Rebuild Project

### API Key Error
- Verify your API key is correct in `local.properties`
- Make sure there are no extra spaces or quotes
- Check that your API key is active on weatherapi.com

### No Internet Connection Error
- Check your device/emulator has internet access
- Verify the app has INTERNET permission (it should be granted automatically)

### Location Permission Denied
- Grant location permission when prompted
- Or go to Settings → Apps → Simple Weather → Permissions → Location

### Cities Not Saving
- Check that the app has storage permission
- Try clearing app data: Settings → Apps → Simple Weather → Storage → Clear Data

## Testing the App

### Test Scenarios
1. **Search for a city**: Try "Paris", "Tokyo", "Sydney"
2. **View 7-day forecast**: Scroll down to see forecast cards
3. **Change units**: Go to Settings, switch between Celsius/Fahrenheit
4. **Refresh weather**: Pull down on the main screen
5. **Delete a city**: Tap the trash icon on a saved city

### Known Test Cities
These cities work well with the API:
- Zagreb, Croatia
- London, United Kingdom
- New York, United States
- Tokyo, Japan
- Sydney, Australia
- Mumbai, India

## Development Tips

### Viewing Logs
- Open Logcat in Android Studio
- Filter by "SimpleWeather" to see app-specific logs

### Database Inspection
- Use Android Studio's Database Inspector
- Go to View → Tool Windows → App Inspection
- Select "Database Inspector" tab

### API Testing
- Test API directly: https://api.weatherapi.com/v1/current.json?key=YOUR_KEY&q=London
- Check API documentation: https://www.weatherapi.com/docs/

## Project Structure Guide

### Where to Find Things
- **Main Activity**: `app/src/main/java/com/simpleweather/ui/main/MainActivity.kt`
- **Layouts**: `app/src/main/res/layout/`
- **Strings**: `app/src/main/res/values/strings.xml` (English)
- **Croatian Strings**: `app/src/main/res/values-hr/strings.xml`
- **Database**: `app/src/main/java/com/simpleweather/data/local/`
- **API Service**: `app/src/main/java/com/simpleweather/data/remote/`

### Making Changes

#### Change App Name
- Edit `app/src/main/res/values/strings.xml`
- Find `<string name="app_name">Simple Weather</string>`

#### Change Colors
- Edit `app/src/main/res/values/colors.xml`
- Change primary, accent, or background colors

#### Add New Language
1. Create folder: `app/src/main/res/values-XX/` (XX = language code)
2. Copy `strings.xml` from `values/` to new folder
3. Translate all strings

## Support

### If You Get Stuck
1. Check the README.md for detailed information
2. Review the implementation guide documents
3. Check Android Studio's Build output for errors
4. Verify all permissions in AndroidManifest.xml

### Common Issues

**Gradle Sync Failed**
```
Solution: File → Invalidate Caches → Invalidate and Restart
```

**App Crashes on Start**
```
Solution: Check Logcat for the exception, likely missing API key
```

**No Weather Data Shown**
```
Solution: Check internet connection and API key validity
```

## Next Steps

After setup:
1. Familiarize yourself with MVVM architecture
2. Understand Room database implementation
3. Learn how Retrofit handles API calls
4. Explore Koin dependency injection
5. Study the WorkManager implementation

Good luck with your project! 🌤️
