package com.simpleweather.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.simpleweather.R
import com.simpleweather.domain.model.SavedCity

class SavedCitiesAdapter(
    private val onCityClick: (SavedCity) -> Unit,
    private val onDeleteClick: (SavedCity) -> Unit
) : ListAdapter<SavedCity, SavedCitiesAdapter.CityViewHolder>(CityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_saved_city, parent, false)
        return CityViewHolder(view, onCityClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CityViewHolder(
        itemView: View,
        private val onCityClick: (SavedCity) -> Unit,
        private val onDeleteClick: (SavedCity) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val cityNameTextView: TextView = itemView.findViewById(R.id.cityNameTextView)
        private val countryTextView: TextView = itemView.findViewById(R.id.countryTextView)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(city: SavedCity) {
            cityNameTextView.text = city.cityName
            countryTextView.text = city.country

            itemView.setOnClickListener {
                onCityClick(city)
            }

            deleteButton.setOnClickListener {
                onDeleteClick(city)
            }
        }
    }

    class CityDiffCallback : DiffUtil.ItemCallback<SavedCity>() {
        override fun areItemsTheSame(oldItem: SavedCity, newItem: SavedCity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SavedCity, newItem: SavedCity): Boolean {
            return oldItem == newItem
        }
    }
}
