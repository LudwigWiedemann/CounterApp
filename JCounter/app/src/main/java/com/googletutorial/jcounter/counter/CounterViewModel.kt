package com.googletutorial.jcounter.counter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.googletutorial.jcounter.common.DatabaseHelper
import com.googletutorial.jcounter.common.DayEntry
import java.lang.Exception
import java.time.Duration
import java.time.LocalDateTime

class CounterViewModel(
    private val dbHelper: DatabaseHelper,
    private var dayEntryId: Int,
    var dateString: String,
    iCount: Int
) : ViewModel() {

    private val _count = MutableLiveData<Int>()
    val count: LiveData<Int>
    get() = _count

    init {
        fillDbUntilNow()
        if (dayEntryId == -1) {
            with(getLatestEntry()) {
                dayEntryId = id!!
                dateString = getDateStringFromDate(dateTime)
                _count.value = count
            }
        } else {
            _count.value = iCount
        }
    }

    fun increaseCount() {
        _count.value = _count.value!! + 1
    }

    fun decreaseCount() {
        _count.value = _count.value!! - 1
    }

    fun fillDbUntilNow() {
        if (dbHelper.dbHasValues()) {
            val latestEntry = getLatestEntry()
            if (latestEntry.isFromToday()) {
                Log.i(TAG, "On init the latest Entry is from today: ${latestEntry.dateTime}. No entries created")
                return
            } else {
                val timeDifference = getTimeDifferenceBetweenDates(latestEntry.dateTime, LocalDateTime.now())
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
                dbHelper.deleteLatestEntry()
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
        dbHelper.updateCountForEntryWithId(count.value!!, dayEntryId)
    }

    private fun getDateStringFromDate(d: LocalDateTime) =
        "${d.dayOfWeek}, ${d.dayOfMonth}. ${d.month} ${d.year}"

    private fun getLatestEntry() = dbHelper.getLatestEntry()

    private fun getTimeDifferenceBetweenDates(
        lastEntriesDate: LocalDateTime,
        todaysDate: LocalDateTime?
    ): Long {
        //        Log.i(TAG, "TimeDifference is $timeDifferenceBetweenDates")
        return Duration.between(lastEntriesDate, todaysDate).toDays()
    }

    override fun onCleared() {
        super.onCleared()
        dbHelper.closeDb()
    }

    companion object {
        const val TAG = "CounterViewModel"
    }

}