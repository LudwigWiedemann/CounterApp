package com.googletutorial.jcounter.common

import java.time.LocalDate
import java.time.LocalDateTime

data class DayEntry(
    val id: Int?,
    val dateTime: LocalDateTime,
    var count: Int,
){
    constructor(
        date: LocalDateTime,
        count: Int) : this(null, date, count)

    fun isFromToday(): Boolean {
        return getDateFromLocalDateTime(dateTime) == LocalDate.now()
    }

    private fun getDateFromLocalDateTime(dateTime: LocalDateTime): LocalDate {
        return with(dateTime) {
            LocalDate.of(
                year,
                month,
                dayOfMonth
            )
        }
    }
}