package com.simpleweather.ui.details

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simpleweather.databinding.ActivityDetailsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CITY_ID = "city_id"
    }

    private lateinit var binding: ActivityDetailsBinding
    private val viewModel: DetailsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        val cityId = intent.getIntExtra(EXTRA_CITY_ID, -1)
        if (cityId != -1) {
            viewModel.loadWeatherDetails(cityId)
            setupObservers()
        } else {
            finish()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.weatherInfo.observe(this) { weather ->
            weather?.let {
                binding.detailsTextView.text = buildString {
                    append("City: ${it.cityName}, ${it.country}\n")
                    append("Temperature: ${it.getDisplayTemp(viewModel.useCelsius)}\n")
                    append("Feels like: ${it.getFeelsLikeTemp(viewModel.useCelsius)}\n")
                    append("Condition: ${it.condition}\n")
                    append("Humidity: ${it.humidity}%\n")
                    append("Wind: ${it.getWindSpeed(viewModel.useKph)} ${it.windDir}\n")
                    append("Pressure: ${it.pressureMb} hPa\n")
                    append("Visibility: ${it.visibilityKm} km\n")
                    append("UV Index: ${it.uv}\n")
                }
            }
        }

        viewModel.forecast.observe(this) { forecasts ->
            // Display forecast data if needed
        }
    }
}
