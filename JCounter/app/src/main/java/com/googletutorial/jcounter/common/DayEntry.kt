package com.googletutorial.jcounter.common

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class DayEntry(
    val id: Int?,
    val date: LocalDate,
    var timeList: ArrayList<TimeEntry>
){
    constructor(
        date: LocalDate,
        timeList: ArrayList<TimeEntry>) : this(null, date, timeList)

    fun isFromToday(): Boolean = date == LocalDate.now()

    fun getCount(): Int = timeList.size


}