# Simple Weather App - Architecture Documentation

## Table of Contents
1. [Overview](#overview)
2. [Architecture Pattern](#architecture-pattern)
3. [Project Structure](#project-structure)
4. [Layer Breakdown](#layer-breakdown)
5. [Data Flow](#data-flow)
6. [Key Components](#key-components)
7. [Dependencies](#dependencies)
8. [Design Decisions](#design-decisions)

---

## Overview

Simple Weather is an Android application that provides real-time weather information for multiple cities. The app follows clean architecture principles with MVVM pattern, ensuring separation of concerns, testability, and maintainability.

### Key Features
- Search weather by city name or current location
- Save multiple cities for quick access
- 7-day weather forecast
- Customizable settings (temperature units, wind speed units)
- Periodic background weather updates
- Push notifications for weather updates
- Offline-first approach with local caching

---

## Architecture Pattern

### MVVM (Model-View-ViewModel)

```
┌─────────────────────────────────────────────────┐
│                    VIEW LAYER                   │
│         (Activities, XML Layouts)               │
│  - MainActivity                                 │
│  - SettingsActivity                             │
│  - DetailsActivity                              │
│  - RecyclerView Adapters                        │
└────────────────┬────────────────────────────────┘
                 │ observes LiveData
                 │ calls methods
                 ▼
┌─────────────────────────────────────────────────┐
│               VIEWMODEL LAYER                   │
│              (Business Logic)                   │
│  - MainViewModel                                │
│  - SettingsViewModel                            │
│  - DetailsViewModel                             │
└────────────────┬────────────────────────────────┘
                 │ uses Repository
                 │ accesses Preferences
                 ▼
┌─────────────────────────────────────────────────┐
│                  MODEL LAYER                    │
│            (Data & Domain Logic)                │
│  - Repository (WeatherRepositoryImpl)           │
│  - Room Database                                │
│  - Retrofit API Service                         │
│  - Domain Models                                │
└─────────────────────────────────────────────────┘
```

### Why MVVM?

1. **Separation of Concerns**: UI logic is separated from business logic
2. **Testability**: ViewModels can be tested without Android framework
3. **Lifecycle Awareness**: ViewModels survive configuration changes
4. **Reactive UI**: LiveData automatically updates UI when data changes
5. **Maintainability**: Clear structure makes code easy to understand and modify

---

## Project Structure

```
com.simpleweather/
│
├── data/                           # Data Layer
│   ├── local/                      # Local data sources
│   │   ├── dao/                    # Room DAOs
│   │   │   ├── WeatherDao.kt
│   │   │   ├── CityDao.kt
│   │   │   └── ForecastDao.kt
│   │   ├── entities/               # Room Entities
│   │   │   ├── WeatherEntity.kt
│   │   │   ├── CityEntity.kt
│   │   │   └── ForecastEntity.kt
│   │   └── WeatherDatabase.kt      # Room Database
│   │
│   ├── remote/                     # Remote data sources
│   │   ├── dto/                    # Data Transfer Objects
│   │   │   └── WeatherApiResponse.kt
│   │   ├── ApiClient.kt            # Retrofit client
│   │   └── WeatherApiService.kt    # API endpoints
│   │
│   └── repository/                 # Repository pattern
│       ├── WeatherRepository.kt    # Interface
│       └── WeatherRepositoryImpl.kt # Implementation
│
├── domain/                         # Domain Layer
│   └── model/                      # Domain models
│       ├── WeatherInfo.kt
│       ├── DailyForecast.kt
│       └── SavedCity.kt
│
├── ui/                             # Presentation Layer
│   ├── main/                       # Main screen
│   │   ├── MainActivity.kt
│   │   ├── MainViewModel.kt
│   │   ├── ForecastAdapter.kt
│   │   └── SavedCitiesAdapter.kt
│   ├── settings/                   # Settings screen
│   │   ├── SettingsActivity.kt
│   │   └── SettingsViewModel.kt
│   └── details/                    # Details screen
│       ├── DetailsActivity.kt
│       └── DetailsViewModel.kt
│
├── di/                             # Dependency Injection
│   └── AppModule.kt                # Koin modules
│
├── utils/                          # Utilities
│   ├── PreferencesManager.kt       # SharedPreferences wrapper
│   ├── PermissionUtils.kt          # Permission handling
│   ├── NetworkUtils.kt             # Network checks
│   └── DateUtils.kt                # Date formatting
│
├── worker/                         # Background tasks
│   └── WeatherSyncWorker.kt        # WorkManager worker
│
├── receiver/                       # Broadcast receivers
│   └── WeatherReceiver.kt          # Weather update receiver
│
└── SimpleWeatherApp.kt             # Application class
```

---

## Layer Breakdown

### 1. View Layer (UI)

**Responsibility**: Display data and capture user interactions

**Components**:
- **Activities**: Host UI and coordinate ViewModels
- **XML Layouts**: Define UI structure
- **Adapters**: Bind data to RecyclerViews

**Example** (MainActivity):
```kotlin
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel() // Koin injection

    private fun setupObservers() {
        // Observe ViewModel data
        viewModel.weatherInfo.observe(this) { weather ->
            binding.temperatureTextView.text = weather.getDisplayTemp()
        }
    }

    private fun setupListeners() {
        // Forward user actions to ViewModel
        binding.searchButton.setOnClickListener {
            viewModel.searchCity(cityName)
        }
    }
}
```

**Key Principles**:
- Activities should be "dumb" - minimal logic
- No direct access to Repository or Database
- All data comes from ViewModel via LiveData
- All user actions go through ViewModel methods

---

### 2. ViewModel Layer

**Responsibility**: Manage UI state and handle business logic

**Components**:
- **MainViewModel**: Manages main screen state
- **SettingsViewModel**: Manages settings
- **DetailsViewModel**: Manages details screen

**Example** (MainViewModel):
```kotlin
class MainViewModel(
    private val repository: WeatherRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // UI State (private mutable, public immutable)
    private val _weatherInfo = MutableLiveData<WeatherInfo?>()
    val weatherInfo: LiveData<WeatherInfo?> = _weatherInfo

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // User action handler
    fun searchCity(cityName: String) {
        _isLoading.value = true

        viewModelScope.launch {
            val result = repository.getWeather(cityName)

            result.onSuccess { weather ->
                _weatherInfo.value = weather // Update UI
            }

            result.onFailure { error ->
                _error.value = error.message
            }

            _isLoading.value = false
        }
    }
}
```

**Key Principles**:
- No Android framework dependencies (except AndroidX lifecycle)
- Uses coroutines for async operations
- Exposes data via LiveData (immutable)
- Survives configuration changes (screen rotation)
- Single source of truth for UI state

---

### 3. Model Layer

#### 3.1 Repository Pattern

**Responsibility**: Abstract data sources and coordinate data operations

**WeatherRepositoryImpl**:
```kotlin
class WeatherRepositoryImpl(
    private val weatherApiService: WeatherApiService,
    private val weatherDao: WeatherDao,
    private val cityDao: CityDao,
    private val forecastDao: ForecastDao
) : WeatherRepository {

    override suspend fun getWeather(
        cityName: String,
        fetchFromRemote: Boolean
    ): Result<WeatherInfo> {
        return try {
            // Try local cache first
            val cached = weatherDao.getWeatherByCity(cityName)

            if (!fetchFromRemote && cached != null) {
                return Result.success(cached.toDomainModel())
            }

            // Fetch from API
            val response = weatherApiService.getCurrentWeather(cityName)

            // Save to database
            weatherDao.insertWeather(response.toEntity())

            Result.success(response.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

**Strategy**: Offline-first approach
1. Check local database first
2. If data is stale or missing, fetch from API
3. Save API response to database
4. Return domain model

#### 3.2 Room Database

**Entities**:
- `WeatherEntity`: Current weather data
- `CityEntity`: Saved cities
- `ForecastEntity`: 7-day forecast data

**Example Entity**:
```kotlin
@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val cityName: String,
    val temperature: Double,
    val condition: String,
    val humidity: Int,

    val timestamp: Long = System.currentTimeMillis()
)
```

**DAOs** use Flow for reactive updates:
```kotlin
@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather WHERE cityName = :cityName")
    fun getWeatherByCityFlow(cityName: String): Flow<WeatherEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)
}
```

#### 3.3 Retrofit API Service

```kotlin
interface WeatherApiService {
    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("q") cityName: String,
        @Query("key") apiKey: String = BuildConfig.WEATHER_API_KEY
    ): WeatherApiResponse

    @GET("forecast.json")
    suspend fun getForecast(
        @Query("q") cityName: String,
        @Query("days") days: Int = 7,
        @Query("key") apiKey: String = BuildConfig.WEATHER_API_KEY
    ): ForecastApiResponse
}
```

---

## Data Flow

### Example: User Searches for a City

```
1. USER ACTION
   └─> User types "London" and clicks search

2. VIEW (MainActivity)
   └─> binding.searchButton.setOnClickListener {
           viewModel.searchCity("London")
       }

3. VIEWMODEL (MainViewModel)
   └─> fun searchCity(cityName: String) {
           _isLoading.value = true  // Show loading spinner
           viewModelScope.launch {
               val result = repository.getWeather(cityName)
               ...
           }
       }

4. REPOSITORY (WeatherRepositoryImpl)
   └─> suspend fun getWeather(cityName: String): Result<WeatherInfo> {
           // Check cache
           val cached = weatherDao.getWeatherByCity(cityName)

           // Fetch from API if needed
           val response = weatherApiService.getCurrentWeather(cityName)

           // Save to database
           weatherDao.insertWeather(response.toEntity())

           return Result.success(response.toDomainModel())
       }

5. DATA SOURCES
   ├─> Room Database (local cache)
   └─> Retrofit API (remote data)

6. BACK TO VIEWMODEL
   └─> result.onSuccess { weatherInfo ->
           _weatherInfo.value = weatherInfo  // Update LiveData
           _isLoading.value = false
       }

7. BACK TO VIEW
   └─> viewModel.weatherInfo.observe(this) { weather ->
           binding.temperatureTextView.text = weather.temperature
           binding.cityNameTextView.text = weather.cityName
       }

8. UI UPDATES
   └─> User sees weather for London!
```

---

## Key Components

### Dependency Injection (Koin)

**AppModule** defines all dependencies:

```kotlin
val appModule = module {
    // Database
    single {
        Room.databaseBuilder(
            androidContext(),
            WeatherDatabase::class.java,
            "weather_db"
        ).build()
    }

    // DAOs
    single { get<WeatherDatabase>().weatherDao() }

    // API
    single<WeatherApiService> { ApiClient.weatherApiService }

    // Repository
    single<WeatherRepository> {
        WeatherRepositoryImpl(
            weatherApiService = get(),
            weatherDao = get(),
            cityDao = get(),
            forecastDao = get()
        )
    }

    // ViewModels
    viewModel { MainViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
}
```

**Benefits**:
- Centralized dependency management
- Easy to swap implementations (e.g., mock repository for testing)
- Automatic lifecycle management
- Constructor injection

---

### Background Processing

#### WorkManager

Periodic weather sync every 3 hours:

```kotlin
class WeatherSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Check if auto-update is enabled
        if (!preferencesManager.autoUpdateEnabled) {
            return Result.success()
        }

        // Sync all saved cities
        repository.syncAllCities()

        // Send broadcast
        sendBroadcast(Intent(WeatherReceiver.ACTION_WEATHER_UPDATE))

        // Show notification if enabled
        if (preferencesManager.notificationsEnabled) {
            showNotification()
        }

        return Result.success()
    }
}
```

**Scheduled in** `SimpleWeatherApp.onCreate()`:
```kotlin
val workRequest = PeriodicWorkRequestBuilder<WeatherSyncWorker>(
    3, TimeUnit.HOURS
).build()

WorkManager.getInstance(this).enqueueUniquePeriodicWork(
    "weather_sync",
    ExistingPeriodicWorkPolicy.KEEP,
    workRequest
)
```

---

### BroadcastReceiver

Handles weather update broadcasts:

```kotlin
class WeatherReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ACTION_WEATHER_UPDATE -> {
                // Check settings
                val prefsManager = PreferencesManager(context)
                if (!prefsManager.notificationsEnabled) return

                // Show notification
                showNotification(context)
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                // WorkManager auto-restarts
            }
        }
    }
}
```

**Registered in AndroidManifest.xml**:
```xml
<receiver
    android:name=".receiver.WeatherReceiver"
    android:enabled="true"
    android:exported="true">
    <intent-filter>
        <action android:name="com.simpleweather.WEATHER_UPDATE" />
    </intent-filter>
</receiver>
```

---

### Preferences Management

Centralized SharedPreferences wrapper:

```kotlin
class PreferencesManager(context: Context) {
    private val prefs = context.getSharedPreferences("weather_prefs", MODE_PRIVATE)

    var temperatureUnit: String
        get() = prefs.getString(KEY_TEMP_UNIT, CELSIUS) ?: CELSIUS
        set(value) = prefs.edit { putString(KEY_TEMP_UNIT, value) }

    var notificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS, true)
        set(value) = prefs.edit { putBoolean(KEY_NOTIFICATIONS, value) }

    val useCelsius: Boolean
        get() = temperatureUnit == CELSIUS
}
```

---

## Dependencies

### Core Libraries

| Library | Purpose | Version |
|---------|---------|---------|
| **Kotlin** | Programming language | Latest |
| **Coroutines** | Asynchronous programming | Latest |
| **AndroidX** | Modern Android components | Latest |

### Architecture Components

| Library | Purpose |
|---------|---------|
| **ViewModel** | UI state management |
| **LiveData** | Observable data holder |
| **Room** | Local database |
| **WorkManager** | Background tasks |

### Networking

| Library | Purpose |
|---------|---------|
| **Retrofit** | REST API client |
| **Gson** | JSON serialization |
| **OkHttp** | HTTP client |

### Dependency Injection

| Library | Purpose |
|---------|---------|
| **Koin** | Dependency injection |

### UI

| Library | Purpose |
|---------|---------|
| **Material Design** | UI components |
| **RecyclerView** | List display |
| **SwipeRefreshLayout** | Pull-to-refresh |

### Location

| Library | Purpose |
|---------|---------|
| **Play Services Location** | GPS location |

---

## Design Decisions

### 1. Why Room over Raw SQLite?
- **Type safety**: Compile-time SQL query verification
- **Less boilerplate**: No manual cursor handling
- **Reactive**: Built-in Flow/LiveData support
- **Migrations**: Easier database version management

### 2. Why Koin over Dagger?
- **Simplicity**: Less boilerplate, easier learning curve
- **Kotlin-first**: DSL designed for Kotlin
- **Fast setup**: No annotation processing
- **Good for small-medium apps**: Sufficient for this project size

### 3. Why Repository Pattern?
- **Abstraction**: ViewModel doesn't know about data sources
- **Testability**: Easy to mock for testing
- **Flexibility**: Can switch between local/remote easily
- **Single source of truth**: Coordinates multiple data sources

### 4. Why LiveData over Flow?
- **Lifecycle aware**: Automatically stops updates when Activity is destroyed
- **Android-optimized**: Designed for UI layer
- **Simple**: Easy to understand for beginners
- **Note**: Flow is used in DAOs for reactive database queries

### 5. Offline-First Strategy
- **Better UX**: App works without internet
- **Performance**: Local data is instant
- **Data saving**: Reduces API calls
- **Reliability**: Handles network failures gracefully

---

## Testing Strategy

### Unit Tests (ViewModels & Repository)

```kotlin
class MainViewModelTest {
    @Test
    fun `searchCity should update weatherInfo on success`() {
        // Given
        val mockRepo = mockk<WeatherRepository>()
        val viewModel = MainViewModel(mockRepo, mockPrefs)

        // When
        viewModel.searchCity("London")

        // Then
        assertEquals("London", viewModel.weatherInfo.value?.cityName)
    }
}
```

### Instrumentation Tests (UI)

```kotlin
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @Test
    fun testSearchCity() {
        // Launch activity
        val scenario = launchActivity<MainActivity>()

        // Type city name
        onView(withId(R.id.searchEditText))
            .perform(typeText("London"))

        // Click search
        onView(withId(R.id.searchButton))
            .perform(click())

        // Verify result
        onView(withId(R.id.cityNameTextView))
            .check(matches(withText("London")))
    }
}
```

---

## Performance Considerations

1. **Database queries on background threads**: All Room operations use coroutines
2. **Image loading**: (If added) Use Glide/Coil for efficient caching
3. **RecyclerView optimization**: ViewBinding + DiffUtil for efficient updates
4. **Lazy loading**: Data loaded only when needed
5. **Caching strategy**: Reduce API calls with local cache

---

## Security

1. **API Key**: Stored in `local.properties`, not in version control
2. **Permissions**: Runtime permissions for location and notifications
3. **Network security**: HTTPS only (enforced by API)
4. **Data validation**: Input sanitization for city names

---

## Future Improvements

1. **Add Compose UI**: Migrate to Jetpack Compose
2. **Add DataStore**: Replace SharedPreferences with DataStore
3. **Add Paging**: For large city lists
4. **Add offline mode indicator**: Visual feedback for network state
5. **Add widget**: Home screen weather widget
6. **Add animations**: Improve UX with transitions
7. **Add more weather data**: Air quality, sunrise/sunset, hourly forecast

---

## Conclusion

This architecture provides:
- ✅ Clean separation of concerns
- ✅ Easy to test
- ✅ Easy to maintain and extend
- ✅ Scalable for future features
- ✅ Follows Android best practices
- ✅ Production-ready code quality

The MVVM pattern combined with Repository pattern and dependency injection creates a robust, maintainable application that can easily adapt to changing requirements.
