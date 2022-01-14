package com.googletutorial.jcounter.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.googletutorial.jcounter.common.DatabaseHelper
import java.lang.IllegalArgumentException

class OverviewViewModelFactory(private val dbHelper: DatabaseHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OverviewViewModel::class.java)) {
            return OverviewViewModel(dbHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}