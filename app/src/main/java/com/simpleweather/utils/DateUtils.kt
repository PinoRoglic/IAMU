package com.simpleweather.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    private val shortDayFormat = SimpleDateFormat("EEE", Locale.getDefault())

    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp * 1000))
    }

    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp * 1000))
    }

    fun formatDateTime(timestamp: Long): String {
        return dateTimeFormat.format(Date(timestamp * 1000))
    }

    fun getDayOfWeek(dateString: String): String {
        return try {
            val date = dateFormat.parse(dateString)
            date?.let { dayFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    fun getShortDayOfWeek(dateString: String): String {
        return try {
            val date = dateFormat.parse(dateString)
            date?.let { shortDayFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    fun isToday(dateString: String): Boolean {
        val today = dateFormat.format(Date())
        return dateString == today
    }
}
