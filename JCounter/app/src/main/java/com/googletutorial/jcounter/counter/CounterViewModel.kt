package com.googletutorial.jcounter.counter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.googletutorial.jcounter.common.DatabaseHelper
import com.googletutorial.jcounter.common.DayEntry
import java.time.Duration
import java.time.LocalDateTime

class CounterViewModel(
    private val dbHelper: DatabaseHelper,
    dE: DayEntry?
) : ViewModel() {

    init {
        bringDbUpToDate()
    }

    private val _dayEntry = MutableLiveData(dE ?: dbHelper.getLatestEntry())
    val dayEntry: LiveData<DayEntry>
        get() = _dayEntry

    fun increaseCount() {
        var dayEntry: DayEntry
        with(_dayEntry.value!!) {
            val nId = id
            val nDateTime = dateTime
            val nCount = ++count
            val nTotalCountAllOtherEntries = ++totalCount
            dayEntry = DayEntry(nId, nDateTime, nCount, nTotalCountAllOtherEntries)
        }
        _dayEntry.value = dayEntry
    }

    fun decreaseCount() {
        with(_dayEntry.value!!) {
            val nId = id
            val nDateTime = dateTime
            val nCount = --count
            val nTotalCountAllOtherEntries = --totalCount
            _dayEntry.value = DayEntry(nId, nDateTime, nCount, nTotalCountAllOtherEntries)
        }
    }

    fun bringDbUpToDate() {
        if (dbHelper.dbHasValues()) {
            val latestEntry = dbHelper.getLatestEntry()
            if (latestEntry.isFromToday()) {
                Log.i(TAG, "On init the latest Entry is from ${latestEntry.dateTime}")
                return
            } else {
                val todaysDate = LocalDateTime.now()
                val lastEntriesDate = latestEntry.dateTime
                var timeDifferenceBetweenDates =
                    Duration.between(lastEntriesDate, todaysDate).toDays()
                Log.i(TAG, "TimeDifference is $timeDifferenceBetweenDates")

                while (timeDifferenceBetweenDates-- > 0) {
                    val dateForNewEntry = todaysDate.minusDays(timeDifferenceBetweenDates)
                    val newEntry = DayEntry(dateForNewEntry, 0, 0)
                    dbHelper.addNewEntryToDb(newEntry)
                }
            }
        } else {
            dbHelper.addNewEntryToDb(DayEntry(LocalDateTime.now(), 0, 0))
        }
    }

    fun updateDatabase() {
        dbHelper.updateTodaysEntryInDb(_dayEntry.value!!)
    }

    fun refreshDayEntry() {
        val id =  _dayEntry.value!!.id
        _dayEntry.value = dbHelper.getEntryFromId(id)
    }

    companion object {
        val TAG = "CounterViewModel"
    }

}