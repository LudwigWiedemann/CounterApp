package com.googletutorial.jcounter.common

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.CursorIndexOutOfBoundsException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.time.LocalDateTime
import java.time.Month
import java.util.*

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    fun dbHasValues(): Boolean {
        val cursor = readableDatabase.query(
            TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
        return cursor.moveToFirst()
    }

    fun getLatestEntry(): DayEntry {
        val cursor = readableDatabase.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_DAYOFMONTH, COLUMN_MONTH, COLUMN_YEAR, COLUMN_HOUR, COLUMN_MINUTE, COLUMN_SECOND, COLUMN_COUNTER),
            null,
            null,
            null,
            null,
            "$COLUMN_ID DESC",
            "1"
        )
        Log.i(TAG, getLastEntryFromCursor(cursor).counter.toString() )
        return getLastEntryFromCursor(cursor)
    }

    fun addNewEntryToDb(newEntry: DayEntry) {
        val values = ContentValues().apply {
            with(newEntry) {
                put(COLUMN_DAYOFMONTH, date.dayOfMonth)
                put(COLUMN_MONTH, date.month.value)
                put(COLUMN_YEAR, date.year)
                put(COLUMN_HOUR, date.hour)
                put(COLUMN_MINUTE, date.minute)
                put(COLUMN_SECOND, date.second)
                put(COLUMN_COUNTER, counter)
            }
        }
        writableDatabase.insert(TABLE_NAME, null, values)
    }

    fun updateTodaysEntryInDb(updatedEntry: DayEntry) {
        val values = ContentValues()
        values.put(COLUMN_COUNTER, updatedEntry.counter)
        writableDatabase.update(
            TABLE_NAME,
            values,
            "$COLUMN_ID = ?",
            arrayOf(getLatestEntry().id.toString())
        )
    }

//    fun updateTodaysDbEntry(newEntry: DayEntry) {
//        if (dbHasValues()) {
//            val latestEntry = getLatestEntry()
//            Log.i("updateDbAfterCounterChange", latestEntry.isFromSameDayAs(newEntry).toString())
//            if (latestEntry.isFromSameDayAs(newEntry)) {
//                updateTodaysEntryInDb(newEntry)
//                return
//            }
//        }
//        val emptyEntry: DayEntry
//        with(newEntry) {
//            emptyEntry = DayEntry(date, 0)
//        }
//        addNewEntryToDb(emptyEntry)
//    }

    fun getAllEntriesFromDb(): ArrayList<DayEntry> {
        val cursor = readableDatabase.query(
            TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
        return getAllEntriesFromCursor(cursor)
    }

    private fun getAllEntriesFromCursor(cursor: Cursor?): ArrayList<DayEntry> {
        val list = arrayListOf<DayEntry>()
        with(cursor!!) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_ID))
                val dayOfMonth: Int = getInt(getColumnIndexOrThrow(COLUMN_DAYOFMONTH))
                val monthInt: Int = getInt(getColumnIndexOrThrow(COLUMN_MONTH))
                val year: Int = getInt(getColumnIndexOrThrow(COLUMN_YEAR))
                val hour: Int = getInt(getColumnIndexOrThrow(COLUMN_HOUR))
                val minute: Int = getInt(getColumnIndexOrThrow(COLUMN_MINUTE))
                val second: Int = getInt(getColumnIndexOrThrow(COLUMN_SECOND))
                val counter: Int = getInt(getColumnIndexOrThrow(COLUMN_COUNTER))

                val month = Month.of(monthInt)
                val date = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second)
                list.add(DayEntry(id, date, counter))
            }
        }
        return list
    }

    private fun getLastEntryFromCursor(cursor: Cursor?): DayEntry {
        var entry: DayEntry
        if (cursor!!.moveToLast()) {
            with(cursor) {
                moveToFirst()
                val id = getInt(getColumnIndexOrThrow(COLUMN_ID))
                val dayOfMonth: Int = getInt(getColumnIndexOrThrow(COLUMN_DAYOFMONTH))
                val monthInt: Int = getInt(getColumnIndexOrThrow(COLUMN_MONTH))
                val year: Int = getInt(getColumnIndexOrThrow(COLUMN_YEAR))
                val hour: Int = getInt(getColumnIndexOrThrow(COLUMN_HOUR))
                val minute: Int = getInt(getColumnIndexOrThrow(COLUMN_MINUTE))
                val second: Int = getInt(getColumnIndexOrThrow(COLUMN_SECOND))
                val counter: Int = getInt(getColumnIndexOrThrow(COLUMN_COUNTER))

                val month = Month.of(monthInt)
                val date = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second)
                entry = DayEntry(id, date, counter)
            }
            return entry
        } else {
            Log.i(TAG, "Could not find entries in the database")
            return DayEntry(LocalDateTime.now(), 0)
        }
    }


    fun getTotalJCountFromDB(): Int {
        var totalJCount = 0
        for (entry in getAllEntriesFromDb()) {
            totalJCount += entry.counter
        }
        return totalJCount
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_TABLE)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val TAG = "DatabaseHelper"
        const val DATABASE_VERSION = 2
        const val DATABASE_NAME = "JCounter.db"
        const val TABLE_NAME = "DAY_ENTRY"
        const val COLUMN_ID = "_id"
        const val COLUMN_DAYOFMONTH = "day_of_month"
        const val COLUMN_MONTH = "month"
        const val COLUMN_YEAR = "year"
        const val COLUMN_HOUR = "hour"
        const val COLUMN_MINUTE = "minute"
        const val COLUMN_SECOND = "second"
        const val COLUMN_COUNTER = "counter"
        const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
        const val SQL_CREATE_TABLE = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_DAYOFMONTH INTEGER," +
                "$COLUMN_MONTH INTEGER," +
                "$COLUMN_YEAR INTEGER," +
                "$COLUMN_HOUR INTEGER," +
                "$COLUMN_MINUTE INTEGER," +
                "$COLUMN_SECOND INTEGER," +
                "$COLUMN_COUNTER INTEGER" +
                ")"

    }


}

