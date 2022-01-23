package com.googletutorial.jcounter.counter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.googletutorial.jcounter.common.DatabaseHelper
import com.googletutorial.jcounter.common.DayEntry
import com.googletutorial.jcounter.common.TimeEntry
import java.lang.Exception
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class CounterViewModel(
    private val dbHelper: DatabaseHelper,
    private var _dayEntry: DayEntry?
) : ViewModel() {

    private val _count = MutableLiveData<Int>()
    val count: LiveData<Int>
    get() = _count
    private var dayEntry: DayEntry
    init {
        fillDbUntilNow()
        if (_dayEntry == null) {
            _dayEntry = getLatestEntry()
        }
        dayEntry = _dayEntry!!
        _count.value = dayEntry.getCount()
        Log.i(TAG, "hh" + dayEntry.toString())

    }

    fun getDayEntry(): DayEntry = dayEntry

    fun increaseCount() {
        dayEntry.timeList.add(TimeEntry(-3, LocalTime.now()))
        _count.value = dayEntry.getCount()
    }

    fun removeTimeEntry(entry: TimeEntry) {
        dayEntry.timeList.remove(entry)
        _count.value = dayEntry.getCount()
    }

    fun decreaseCount() {
        if (_count.value != 0) {
            dayEntry.timeList.removeAt(dayEntry.timeList.lastIndex)
            _count.value = dayEntry.getCount()
        } else {
            _count.value = -1
            _count.value = 0
        }
    }

    fun fillDbUntilNow() {
        if (dbHelper.dbHasValues()) {
            val latestEntry = getLatestEntry()
            if (latestEntry.isFromToday()) {
                Log.i(TAG, "On init the latest Entry is from today: ${latestEntry.date}. No entries created")
                return
            } else {
                val lastDateTime = LocalDateTime.of(latestEntry.date.year, latestEntry.date.month, latestEntry.date.dayOfMonth, LocalTime.now().hour, LocalTime.now().minute, LocalTime.now().second)
                val timeDifference = getDayDifferenceToToday(lastDateTime)
                if (timeDifference > 0) {
                    createPastEmptyEntries(timeDifference)
                } else {
                    deleteEntriesInFuture(timeDifference)
                }
            }
        }
        if (!dbHelper.dbHasValues()) {
            dbHelper.addEntryToDbForDate(LocalDateTime.now())
        }
    }

    fun reloadTimeList() {
        dayEntry.timeList = dbHelper.getTimeListForDayEntryID(dayEntry.id!!)
    }

    private fun createPastEmptyEntries(timeDifference: Long) {
        var timeDifference1 = timeDifference
        while (timeDifference1 > 0) {
            val dateForNewEntry = LocalDateTime.now().minusDays(timeDifference1 - 1)
            dbHelper.addEntryToDbForDate(dateForNewEntry)
            timeDifference1--
        }
    }

    private fun deleteEntriesInFuture(timeDifference: Long) {
        var timeDifference1 = timeDifference
        while (timeDifference1 <= 0) {
            try {
                val latestEntry = dbHelper.getLatestEntry()
                dbHelper.deleteTimeListForEntryId(latestEntry.id!!)
                dbHelper.deleteEntryWithId(latestEntry.id)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
            timeDifference1++
        }
    }

    fun getTotalJCount(): Int {
        return dbHelper.getTotalJCountFromDB()
    }

    fun updateDatabase() {
        dbHelper.updateTimeListForDayEntryId(dayEntry.id!!, dayEntry.timeList)
    }

    fun getDateStringFromDate(d: LocalDate) =
        "${d.dayOfWeek}, ${d.dayOfMonth}. ${d.month} ${d.year}"

    private fun getLatestEntry() = dbHelper.getLatestEntry()

    private fun getDayDifferenceToToday(
        lastEntriesDate: LocalDateTime,
    ): Long {
        //        Log.i(TAG, "TimeDifference is $timeDifferenceBetweenDates")
        return Duration.between(lastEntriesDate, LocalDateTime.now()).toDays()
    }

    override fun onCleared() {
        super.onCleared()
        dbHelper.closeDb()
    }

    fun getDatasetForAdapter() = dayEntry.timeList

    companion object {
        const val TAG = "CounterViewModel"
    }

}