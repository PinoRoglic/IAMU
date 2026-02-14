package com.simpleweather.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.simpleweather.R
import com.simpleweather.domain.model.DailyForecast
import com.simpleweather.utils.DateUtils

class ForecastAdapter(
    private val useCelsius: Boolean
) : ListAdapter<DailyForecast, ForecastAdapter.ForecastViewHolder>(ForecastDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forecast, parent, false)
        return ForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(getItem(position), useCelsius)
    }

    class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayTextView: TextView = itemView.findViewById(R.id.dayTextView)
        private val conditionTextView: TextView = itemView.findViewById(R.id.conditionTextView)
        private val maxTempTextView: TextView = itemView.findViewById(R.id.maxTempTextView)
        private val minTempTextView: TextView = itemView.findViewById(R.id.minTempTextView)
        private val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)

        fun bind(forecast: DailyForecast, useCelsius: Boolean) {
            dayTextView.text = DateUtils.getShortDayOfWeek(forecast.date)
            conditionTextView.text = forecast.condition
            maxTempTextView.text = forecast.getMaxTemp(useCelsius)
            minTempTextView.text = forecast.getMinTemp(useCelsius)

            // You can use a library like Glide or Coil to load the icon
            // For now, we'll use a placeholder
            // Glide.with(itemView.context).load("https:${forecast.conditionIcon}").into(iconImageView)
        }
    }

    class ForecastDiffCallback : DiffUtil.ItemCallback<DailyForecast>() {
        override fun areItemsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
            return oldItem == newItem
        }
    }
}
