package com.googletutorial.jcounter.overview

import android.util.Log
import androidx.lifecycle.ViewModel
import com.googletutorial.jcounter.common.DatabaseHelper
import com.googletutorial.jcounter.common.DayEntry

class OverviewViewModel(private val dbHelper: DatabaseHelper) : ViewModel() {

    fun getDatasetForAdapter(): ArrayList<DayEntry> {
        Log.i(TAG, dbHelper.getAllEntriesFromDb().size.toString())
        return dbHelper.getAllEntriesFromDb()
    }

    companion object {
        const val TAG = "OverviewViewModel"
    }

}