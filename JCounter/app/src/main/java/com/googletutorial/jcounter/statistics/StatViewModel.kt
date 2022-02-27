package com.googletutorial.jcounter.statistics

import androidx.lifecycle.ViewModel
import com.googletutorial.jcounter.common.DatabaseHelper
import java.time.LocalDate

class StatViewModel(
    private val dbHelper: DatabaseHelper) : ViewModel() {
    var startDate = dbHelper.getOldestEntry().date
    var endDate: LocalDate = LocalDate.now()


    fun getAverage(): Float{
        return dbHelper.getAverageBetweenDates(startDate, endDate)
    }
}