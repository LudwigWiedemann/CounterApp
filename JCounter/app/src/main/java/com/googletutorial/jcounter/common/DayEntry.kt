package com.googletutorial.jcounter.common

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class DayEntry(
    val id: Int?,
    val date: LocalDateTime,
    val counter: Int,
) {
    constructor(
        date: LocalDateTime,
        counter: Int) : this(null, date, counter)

    fun isFromSameDayAs(toCompare: DayEntry): Boolean {
        val simpleDate = LocalDate.of(date.year, date.month, date.dayOfMonth)
        val toCompareSimpleDate = LocalDate.of(toCompare.date.year, toCompare.date.month, toCompare.date.dayOfMonth)
        return simpleDate == toCompareSimpleDate
    }

    fun isFromToday(): Boolean {
        val toCompare = LocalDateTime.now()
        return date == toCompare
    }
}