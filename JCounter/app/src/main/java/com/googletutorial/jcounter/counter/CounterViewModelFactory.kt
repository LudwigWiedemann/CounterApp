package com.googletutorial.jcounter.counter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.googletutorial.jcounter.common.DatabaseHelper
import com.googletutorial.jcounter.common.DayEntry
import java.lang.IllegalArgumentException

class CounterViewModelFactory(private val dbHelper: DatabaseHelper,
                              private val dayEntryId: Int,
                              private val dateString: String,
                              private val count: Int  ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CounterViewModel::class.java)) {
            return CounterViewModel(dbHelper, dayEntryId, dateString, count) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}