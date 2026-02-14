package com.simpleweather.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simpleweather.R
import com.simpleweather.databinding.ActivitySettingsBinding
import com.simpleweather.utils.PreferencesManager
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadSettings()
        setupListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadSettings() {
        // Temperature unit
        when (viewModel.temperatureUnit) {
            PreferencesManager.CELSIUS -> binding.celsiusRadio.isChecked = true
            PreferencesManager.FAHRENHEIT -> binding.fahrenheitRadio.isChecked = true
        }

        // Wind speed unit
        when (viewModel.windSpeedUnit) {
            PreferencesManager.KPH -> binding.kphRadio.isChecked = true
            PreferencesManager.MPH -> binding.mphRadio.isChecked = true
        }

        // Auto update
        binding.autoUpdateSwitch.isChecked = viewModel.autoUpdateEnabled

        // Notifications
        binding.notificationsSwitch.isChecked = viewModel.notificationsEnabled
    }

    private fun setupListeners() {
        // Temperature unit
        binding.temperatureUnitGroup.setOnCheckedChangeListener { _, checkedId ->
            viewModel.temperatureUnit = when (checkedId) {
                R.id.celsiusRadio -> PreferencesManager.CELSIUS
                R.id.fahrenheitRadio -> PreferencesManager.FAHRENHEIT
                else -> PreferencesManager.CELSIUS
            }
        }

        // Wind speed unit
        binding.windSpeedUnitGroup.setOnCheckedChangeListener { _, checkedId ->
            viewModel.windSpeedUnit = when (checkedId) {
                R.id.kphRadio -> PreferencesManager.KPH
                R.id.mphRadio -> PreferencesManager.MPH
                else -> PreferencesManager.KPH
            }
        }

        // Auto update
        binding.autoUpdateSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.autoUpdateEnabled = isChecked
        }

        // Notifications
        binding.notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.notificationsEnabled = isChecked
        }
    }
}
