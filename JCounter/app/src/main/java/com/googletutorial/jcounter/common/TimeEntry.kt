package com.googletutorial.jcounter.common

import java.time.LocalTime

data class TimeEntry(val id: Int, val time: LocalTime) {
    fun getTimeString(): String {
        return with(time) {
            "Lighting time: ${addLeadingZero(hour.toString())}:${addLeadingZero(minute.toString())}"
        }
    }

    private fun addLeadingZero(text: String): String {
        return if (text.length <= 1) {
            "0$text"
        } else {
            text
        }
    }
}