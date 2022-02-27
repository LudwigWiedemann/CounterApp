package com.googletutorial.jcounter.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.googletutorial.jcounter.common.DatabaseHelper
import java.lang.IllegalArgumentException
import java.time.LocalDate

class StatViewModelFactory(private val dbHelper: DatabaseHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatViewModel::class.java)) {
            return StatViewModel(dbHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}