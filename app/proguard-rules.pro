# Keep Room classes
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Keep Retrofit classes
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keep class com.google.gson.** { *; }

# Keep data models
-keep class com.simpleweather.data.remote.dto.** { *; }
-keep class com.simpleweather.domain.model.** { *; }

# Keep Koin
-keep class org.koin.** { *; }

# Keep Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
