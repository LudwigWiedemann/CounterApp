package com.googletutorial.jcounter.counter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.googletutorial.jcounter.common.DatabaseHelper
import com.googletutorial.jcounter.common.DayEntry
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

class CounterViewModel(private val dbHelper: DatabaseHelper) : ViewModel() {

//    private var latestEntry = dbHelper.getLatestEntry()

    private val _totalCount = MutableLiveData<Int>()
    val totalCount: LiveData<Int>
        get() = _totalCount

    private val _todaysCount = MutableLiveData<Int>()
    val todaysCount: LiveData<Int>
        get() = _todaysCount

    fun updateTodaysCountInDb(count: Int) {
        val entry = DayEntry(LocalDateTime.now(), count)
        dbHelper.updateTodaysEntryInDb(entry)
    }

//    fun fillWithEntriesUntilToday() {
//        Log.i(TAG, " hasValues:${dbHelper.dbHasValues()}")
//
//        if (!dbHelper.dbHasValues()) {
//            val newEntry = DayEntry(LocalDateTime.now(), 0)
//            writeNewEntryToDb(newEntry)
//        } else {
//            latestEntry = dbHelper.getLatestEntry()
//            val latestDate = latestEntry.date
//            if (latestDate != LocalDateTime.now()) {
//                latestEntry = dbHelper.getLatestEntry()
//                val currentDate = LocalDateTime.now()
//                var dayDifferenceUntilToday = Duration.between(latestDate, currentDate).toDays()-1
//                Log.i(TAG, " difference: $dayDifferenceUntilToday")
//                while (dayDifferenceUntilToday >= 0) {
//                    val newDate = LocalDateTime.now().minusDays(dayDifferenceUntilToday)
//                    dayDifferenceUntilToday--
//                    Log.i(TAG, "newDate: $newDate")
//                    val newEntry = DayEntry(newDate, 0)
//                    writeNewEntryToDb(newEntry)
//                }
//            }
//        }
//    }

    private fun addNewEntryToDb(entry: DayEntry) {
        dbHelper.addNewEntryToDb(entry)
    }

    fun getTodaysCount(): Int {
        val latestEntry = dbHelper.getLatestEntry()
        return latestEntry.counter
    }

    fun getTotalCount(): Int {
        val allEntries = dbHelper.getAllEntriesFromDb()
        var totalCount = 0
        for (entry in allEntries) {
            totalCount += entry.counter
        }
        return totalCount
    }

//    fun getTodaysCounterFromDbAndUpdateCouterValue() {
//        databaseJob?.cancel()
//        databaseJob = databaseScope.launch {
//            if (dbHelper.dbHasValues()) {
//                latestEntry = dbHelper.getLatestEntry()
//                if (latestEntry.isFromToday()) {
//                    Log.i("latestEntry", latestEntry.isFromToday().toString())
//                    _counterValue.postValue(latestEntry.counter)
//                    return@launch
//                }
//            }
//            _counterValue.postValue(0)
//        }
//    }
//
//    fun getTotalJCountFromDbAndUpdateTotalCountValue() {
//        databaseJob?.cancel()
//        databaseJob = databaseScope.launch {
//            Log.i("test total", dbHelper.getTotalJCountFromDB().toString())
//            _totalCount.postValue(dbHelper.getTotalJCountFromDB())
//        }
//    }
//
//    fun updateTodaysCounterInDb(counterValue: Int) {
//        databaseJob?.cancel()
//        databaseJob = databaseScope.launch {
//            val now = LocalDateTime.now()
//            val newEntry = DayEntry(
//                now,
//                counterValue
//            )
//
//            dbHelper.updateDb(newEntry)
//            getTotalJCountFromDbAndUpdateTotalCountValue()
//        }
//    }


    fun updateTodaysCount(i: Int) {
        _todaysCount.value = _todaysCount.value?.plus(i)
        setTotalCount(getTotalCount())


    }

    fun setTodaysCount(i: Int) {
        _todaysCount.value = i

    }

    fun setTotalCount(i: Int) {
        _totalCount.value = i

    }

    override fun onCleared() {
        super.onCleared()
    }

    companion object {
        val TAG = "CounterViewModel"
    }

}