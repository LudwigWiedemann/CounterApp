package com.googletutorial.jcounter.common

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    fun addEntryToDbForDate(dateTime: LocalDateTime) {
        val values = ContentValues().apply {
            with(dateTime) {
                put(COLUMN_DAYOFMONTH, dayOfMonth)
                put(COLUMN_MONTH, month.value)
                put(COLUMN_YEAR, year)
                put(COLUMN_COUNTER, 0)
            }
        }
        writableDatabase.insert(TABLE_NAME_DAY_ENTRY, null, values)
    }

    fun updateCountForEntryWithId(count: Int, id: Int, timeList: ArrayList<LocalTime>) {
        val values = ContentValues()
        values.put(COLUMN_COUNTER, count)
        writableDatabase.update(
            TABLE_NAME_DAY_ENTRY,
            values,
            "$COLUMN_ID_DAY_ENTRY = ?",
            arrayOf(id.toString())
        )
        updateTimeListForDayEntryId(id, timeList)
    }

    private fun getTimeListForDayEntryID(id: Int): ArrayList<LocalTime> {
        val cursor = readableDatabase.query(
            TABLE_NAME_TIME,
            arrayOf(COLUMN_HOUR, COLUMN_MINUTE, COLUMN_SECOND),
            "$COLUMN_ID_DAY_ENTRY = ?",
            arrayOf(id.toString()),
            null,
            null,
            "$COLUMN_ID_TIME DESC"
        )
        return arrayListOf<LocalTime>().apply {
            with(cursor) {
                while (moveToNext()) {
                    val hour: Int = getInt(getColumnIndexOrThrow(COLUMN_HOUR))
                    val minute: Int = getInt(getColumnIndexOrThrow(COLUMN_MINUTE))
                    val second: Int = getInt(getColumnIndexOrThrow(COLUMN_SECOND))
                    add(LocalTime.of(hour, minute, second))
                }
            }
        }
    }

    private fun updateTimeListForDayEntryId(id: Int, timeList: ArrayList<LocalTime>) {
        writableDatabase.execSQL("DELETE FROM $TABLE_NAME_TIME WHERE $COLUMN_ID_DAY_ENTRY = $id")
        for (time in timeList) {
            val values = ContentValues().apply {
                put(COLUMN_ID_DAY_ENTRY, id)
                put(COLUMN_HOUR, time.hour)
                put(COLUMN_MINUTE, time.minute)
                put(COLUMN_SECOND, time.second)
            }
            writableDatabase.insert(TABLE_NAME_TIME, null, values)
        }
    }

    fun getLatestEntry(): DayEntry {
        return try {
            val cursor = readableDatabase.query(
                TABLE_NAME_DAY_ENTRY,
                null,
                null,
                null,
                null,
                null,
                "$COLUMN_ID_DAY_ENTRY DESC",
                "1"
            )
            val lastEntry = getLastEntryFromCursor(cursor)
            lastEntry.timeList = getTimeListForDayEntryID(lastEntry.id!!)
            return lastEntry
        } catch (e: RuntimeException) {
            Log.e(TAG, "could not execute query in DB: $e")
            DayEntry(
                LocalDate.of(0, Month.AUGUST, 1),
                0,
                arrayListOf<LocalTime>()
            )
        }
    }

    fun getAllEntriesFromDb(): ArrayList<DayEntry> {
        val cursor = readableDatabase.query(
            TABLE_NAME_DAY_ENTRY,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_ID_DAY_ENTRY DESC",
            null
        )
        val entryList = getAllEntriesFromCursor(cursor)
        for (entry in entryList) {
            entry.timeList = getTimeListForDayEntryID(entry.id!!)
        }
        return entryList
    }

    fun deleteLatestEntry() {
        writableDatabase.execSQL("DELETE FROM $TABLE_NAME_DAY_ENTRY WHERE $COLUMN_ID_DAY_ENTRY = (SELECT MAX($COLUMN_ID_DAY_ENTRY) FROM $TABLE_NAME_DAY_ENTRY)")
        Log.i(TAG, "Latest entry deleted ")
    }

    fun dbHasValues(): Boolean {
        val cursor = readableDatabase.query(
            TABLE_NAME_DAY_ENTRY,
            null,
            null,
            null,
            null,
            null,
            null,
        )
        return cursor.moveToFirst()
    }

    fun getTotalJCountFromDB(): Int {
        try {
            val cursor = readableDatabase.query(
                TABLE_NAME_DAY_ENTRY,
                arrayOf("SUM($COLUMN_COUNTER) AS $COLUMN_TOTALCOUNT"),
                null,
                null,
                null,
                null,
                null,
                null,
            )
            with(cursor) {
                moveToFirst()
                return getInt(getColumnIndexOrThrow(COLUMN_TOTALCOUNT))
            }
        } catch (e: RuntimeException) {
            Log.e(TAG, "could not execute query in DB: $e")
        }
        return 0
    }


    private fun getAllEntriesFromCursor(cursor: Cursor?): ArrayList<DayEntry> {
        val list = arrayListOf<DayEntry>()
        with(cursor!!) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_ID_DAY_ENTRY))
                val dayOfMonth: Int = getInt(getColumnIndexOrThrow(COLUMN_DAYOFMONTH))
                val monthInt: Int = getInt(getColumnIndexOrThrow(COLUMN_MONTH))
                val year: Int = getInt(getColumnIndexOrThrow(COLUMN_YEAR))
                val counter: Int = getInt(getColumnIndexOrThrow(COLUMN_COUNTER))

                val month = Month.of(monthInt)
                val date = LocalDate.of(year, month, dayOfMonth)
                list.add(DayEntry(id, date, counter, arrayListOf<LocalTime>()))
            }
        }
        return list
    }

    private fun getLastEntryFromCursor(cursor: Cursor?): DayEntry {
        var entry: DayEntry
        if (cursor!!.moveToLast()) {
            with(cursor) {
                moveToFirst()
                val id = getInt(getColumnIndexOrThrow(COLUMN_ID_DAY_ENTRY))
                val dayOfMonth: Int = getInt(getColumnIndexOrThrow(COLUMN_DAYOFMONTH))
                val monthInt: Int = getInt(getColumnIndexOrThrow(COLUMN_MONTH))
                val year: Int = getInt(getColumnIndexOrThrow(COLUMN_YEAR))
                val counter: Int = getInt(getColumnIndexOrThrow(COLUMN_COUNTER))

                val month = Month.of(monthInt)
                val date = LocalDate.of(year, month, dayOfMonth)
                entry = DayEntry(id, date, counter, arrayListOf<LocalTime>())
            }
            return entry
        } else {
            Log.i(TAG, "Could not find entries in the database")
            return DayEntry(LocalDate.now(), 0, arrayListOf<LocalTime>())
        }
    }

    fun closeDb() {
        readableDatabase.close()
        writableDatabase.close()
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_DAY_ENTRY)
        db.execSQL(SQL_CREATE_TABLE_TIME)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_TABLE_DAY_ENTRY)
        db.execSQL(SQL_DELETE_TABLE_TIME)
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
        const val TABLE_NAME_DAY_ENTRY = "DAY_ENTRY"
        const val TABLE_NAME_TIME = "TIME"
        const val COLUMN_ID_DAY_ENTRY = "_id_day_entry"
        const val COLUMN_DAYOFMONTH = "day_of_month"
        const val COLUMN_MONTH = "month"
        const val COLUMN_YEAR = "year"
        const val COLUMN_ID_TIME = "_id_time"
        const val COLUMN_HOUR = "hour"
        const val COLUMN_MINUTE = "minute"
        const val COLUMN_SECOND = "second"
        const val COLUMN_COUNTER = "counter"
        const val COLUMN_TOTALCOUNT = "totalCount"
        const val SQL_DELETE_TABLE_DAY_ENTRY = "DROP TABLE IF EXISTS $TABLE_NAME_DAY_ENTRY"
        const val SQL_DELETE_TABLE_TIME = "DROP TABLE IF EXISTS $TABLE_NAME_TIME"
        const val SQL_CREATE_TABLE_DAY_ENTRY = "CREATE TABLE $TABLE_NAME_DAY_ENTRY (" +
                "$COLUMN_ID_DAY_ENTRY INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_DAYOFMONTH INTEGER," +
                "$COLUMN_MONTH INTEGER," +
                "$COLUMN_YEAR INTEGER," +
                "$COLUMN_COUNTER INTEGER" +
                ")"
        const val SQL_CREATE_TABLE_TIME = "CREATE TABLE $TABLE_NAME_TIME (" +
                "$COLUMN_ID_TIME INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_ID_DAY_ENTRY INTEGER," +
                "$COLUMN_HOUR INTEGER," +
                "$COLUMN_MINUTE INTEGER," +
                "$COLUMN_SECOND INTEGER," +
                "FOREIGN KEY ($COLUMN_ID_DAY_ENTRY) REFERENCES $TABLE_NAME_DAY_ENTRY ($COLUMN_ID_DAY_ENTRY)" +
                ")"
    }


}

