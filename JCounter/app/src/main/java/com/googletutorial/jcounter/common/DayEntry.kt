package com.googletutorial.jcounter.common

import android.os.Parcel
import android.os.Parcelable
import java.time.LocalDate
import java.time.LocalDateTime

data class DayEntry(
    val id: Int?,
    val dateTime: LocalDateTime,
    var count: Int,
    var totalCount: Int,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        TODO("dateTime"),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    constructor(
        date: LocalDateTime,
        count: Int,
        totalCount: Int) : this(null, date, count, totalCount)

    fun isFromToday(): Boolean {
        return getDateFromLocalDateTime(dateTime) == LocalDate.now()
    }

    private fun getDateFromLocalDateTime(dateTime: LocalDateTime): LocalDate {
        return with(dateTime) {
            LocalDate.of(
                year,
                month,
                dayOfMonth
            )
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeInt(count)
        parcel.writeInt(totalCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DayEntry> {
        override fun createFromParcel(parcel: Parcel): DayEntry {
            return DayEntry(parcel)
        }

        override fun newArray(size: Int): Array<DayEntry?> {
            return arrayOfNulls(size)
        }
    }
}