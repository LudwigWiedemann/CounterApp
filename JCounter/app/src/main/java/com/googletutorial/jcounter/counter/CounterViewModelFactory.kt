package com.googletutorial.jcounter.counter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.googletutorial.jcounter.common.DatabaseHelper
import com.googletutorial.jcounter.common.DayEntry
import java.lang.IllegalArgumentException

class CounterViewModelFactory(private val dbHelper: DatabaseHelper,
                              private val dayEntry: DayEntry?) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CounterViewModel::class.java)) {
            return CounterViewModel(dbHelper, dayEntry) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}