package com.googletutorial.jcounter.common

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class DayEntry(
    val id: Int?,
    val date: LocalDate,
    var count: Int,
    var timeList: ArrayList<LocalTime>
){
    constructor(
        date: LocalDate,
        count: Int,
        timeList: ArrayList<LocalTime>) : this(null, date, count, timeList)

    fun isFromToday(): Boolean {
        return date == LocalDate.now()
    }


}