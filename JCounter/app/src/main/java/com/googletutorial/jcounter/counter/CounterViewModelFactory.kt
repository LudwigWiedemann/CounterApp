package com.googletutorial.jcounter.counter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.googletutorial.jcounter.common.DatabaseHelper
import java.lang.IllegalArgumentException

class CounterViewModelFactory(private val dbHelper: DatabaseHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CounterViewModel::class.java)) {
            return CounterViewModel(dbHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}