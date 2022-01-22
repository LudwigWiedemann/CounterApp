package com.googletutorial.jcounter.counter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.googletutorial.jcounter.common.DatabaseHelper
import java.lang.Exception
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class CounterViewModel(
    private val dbHelper: DatabaseHelper,
    private var dayEntryId: Int,
    var dateString: String,
    iCount: Int,
    var timeList: ArrayList<LocalTime>
) : ViewModel() {

    private val _count = MutableLiveData<Int>()
    val count: LiveData<Int>
    get() = _count

    init {
        fillDbUntilNow()
        if (dayEntryId == -1) {
            with(getLatestEntry()) {
                dayEntryId = id!!
                dateString = getDateStringFromDate(date)
                _count.value = count
            }
        } else {
            _count.value = iCount
        }
    }

    fun increaseCount() {
        _count.value = _count.value!! + 1
        timeList.add(LocalTime.now())
    }

    fun decreaseCount() {
        _count.value = _count.value!! - 1
        timeList.removeAt(timeList.lastIndex)
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
        dbHelper.updateCountForEntryWithId(count.value!!, dayEntryId, timeList)
    }

    private fun getDateStringFromDate(d: LocalDate) =
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

    companion object {
        const val TAG = "CounterViewModel"
    }

}