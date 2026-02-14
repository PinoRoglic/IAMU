package com.simpleweather.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.simpleweather.R
import com.simpleweather.databinding.ActivityMainBinding
import com.simpleweather.ui.settings.SettingsActivity
import com.simpleweather.utils.NetworkUtils
import com.simpleweather.utils.PermissionUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel()

    private lateinit var forecastAdapter: ForecastAdapter
    private lateinit var savedCitiesAdapter: SavedCitiesAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerViews()
        setupListeners()
        setupObservers()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Request location permission if not granted
        if (!PermissionUtils.hasLocationPermission(this)) {
            PermissionUtils.requestLocationPermission(this)
        }

        // Request notification permission for Android 13+
        if (!PermissionUtils.hasNotificationPermission(this)) {
            PermissionUtils.requestNotificationPermission(this)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupRecyclerViews() {
        // Forecast RecyclerView
        forecastAdapter = ForecastAdapter(viewModel.useCelsius)
        binding.forecastRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = forecastAdapter
        }

        // Saved Cities RecyclerView
        savedCitiesAdapter = SavedCitiesAdapter(
            onCityClick = { city ->
                viewModel.loadWeather(city.cityName)
            },
            onDeleteClick = { city ->
                showDeleteCityDialog(city.id, city.cityName)
            }
        )
        binding.savedCitiesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = savedCitiesAdapter
        }
    }

    private fun setupListeners() {
        // Search functionality
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val cityName = binding.searchEditText.text.toString().trim()
                if (cityName.isNotEmpty()) {
                    if (NetworkUtils.isNetworkAvailable(this)) {
                        viewModel.searchCity(cityName)
                        binding.searchEditText.text?.clear()
                    } else {
                        Toast.makeText(this, R.string.error_network, Toast.LENGTH_SHORT).show()
                    }
                }
                true
            } else {
                false
            }
        }

        // Search icon click
        binding.searchInputLayout.setEndIconOnClickListener {
            val cityName = binding.searchEditText.text.toString().trim()
            if (cityName.isNotEmpty()) {
                if (NetworkUtils.isNetworkAvailable(this)) {
                    viewModel.searchCity(cityName)
                    binding.searchEditText.text?.clear()
                } else {
                    Toast.makeText(this, R.string.error_network, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Swipe to refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (NetworkUtils.isNetworkAvailable(this)) {
                viewModel.refreshWeather()
            } else {
                Toast.makeText(this, R.string.error_network, Toast.LENGTH_SHORT).show()
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        // Test notification button
        binding.testNotificationFab.setOnClickListener {
            showTestNotification()
            Toast.makeText(this, "Test notification sent!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        viewModel.weatherInfo.observe(this) { weather ->
            if (weather != null) {
                binding.currentWeatherCard.isVisible = true
                binding.cityNameTextView.text = weather.cityName
                binding.countryTextView.text = weather.country
                binding.temperatureTextView.text = weather.getDisplayTemp(viewModel.useCelsius)
                binding.conditionTextView.text = weather.condition
                binding.feelsLikeTextView.text = getString(
                    R.string.feels_like,
                    weather.getFeelsLikeTemp(viewModel.useCelsius)
                )
                binding.humidityTextView.text = "${weather.humidity}%"
                binding.windTextView.text = weather.getWindSpeed(viewModel.useKph)
                binding.uvTextView.text = weather.uv.toInt().toString()
            } else {
                binding.currentWeatherCard.isVisible = false
            }
        }

        viewModel.forecast.observe(this) { forecasts ->
            if (forecasts.isNotEmpty()) {
                binding.forecastTitleTextView.isVisible = true
                binding.forecastRecyclerView.isVisible = true
                forecastAdapter.submitList(forecasts)
            } else {
                binding.forecastTitleTextView.isVisible = false
                binding.forecastRecyclerView.isVisible = false
            }
        }

        viewModel.savedCities.observe(this) { cities ->
            if (cities.isEmpty()) {
                binding.noCitiesTextView.isVisible = true
                binding.savedCitiesRecyclerView.isVisible = false
            } else {
                binding.noCitiesTextView.isVisible = false
                binding.savedCitiesRecyclerView.isVisible = true
                savedCitiesAdapter.submitList(cities)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        viewModel.isRefreshing.observe(this) { isRefreshing ->
            binding.swipeRefreshLayout.isRefreshing = isRefreshing
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    private fun showDeleteCityDialog(cityId: Int, cityName: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_city_title)
            .setMessage(getString(R.string.delete_city_message))
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteCity(cityId)
                Toast.makeText(this, R.string.city_deleted, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showTestNotification() {
        val currentWeather = viewModel.weatherInfo.value
        val notificationText = if (currentWeather != null) {
            "${currentWeather.cityName}: ${currentWeather.getDisplayTemp(viewModel.useCelsius)}, ${currentWeather.condition}"
        } else {
            "Simple Weather - Testing notifications!"
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = android.app.PendingIntent.getActivity(
            this,
            0,
            intent,
            android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val notification = androidx.core.app.NotificationCompat.Builder(
            this,
            com.simpleweather.SimpleWeatherApp.NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(notificationText)
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.notify(1001, notification)
    }

    private fun getCurrentLocation() {
        if (!PermissionUtils.hasLocationPermission(this)) {
            PermissionUtils.requestLocationPermission(this)
            return
        }

        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    viewModel.loadWeatherByCoordinates(it.latitude, it.longitude)
                } ?: run {
                    Toast.makeText(this, R.string.error_location, Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PermissionUtils.LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation()
                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
                }
            }
            PermissionUtils.NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
