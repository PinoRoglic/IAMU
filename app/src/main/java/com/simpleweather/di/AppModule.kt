package com.simpleweather.di

import androidx.room.Room
import com.simpleweather.data.local.WeatherDatabase
import com.simpleweather.data.remote.ApiClient
import com.simpleweather.data.remote.WeatherApiService
import com.simpleweather.data.repository.WeatherRepository
import com.simpleweather.data.repository.WeatherRepositoryImpl
import com.simpleweather.ui.details.DetailsViewModel
import com.simpleweather.ui.main.MainViewModel
import com.simpleweather.ui.settings.SettingsViewModel
import com.simpleweather.utils.PreferencesManager
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Database
    single {
        Room.databaseBuilder(
            androidContext(),
            WeatherDatabase::class.java,
            WeatherDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    // DAOs
    single { get<WeatherDatabase>().weatherDao() }
    single { get<WeatherDatabase>().cityDao() }
    single { get<WeatherDatabase>().forecastDao() }

    // API Service
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

    // Preferences Manager
    single { PreferencesManager(androidContext()) }

    // ViewModels
    viewModel { MainViewModel(repository = get(), preferencesManager = get()) }
    viewModel { DetailsViewModel(repository = get(), preferencesManager = get()) }
    viewModel { SettingsViewModel(preferencesManager = get()) }
}
