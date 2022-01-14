package com.googletutorial.jcounter.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.googletutorial.jcounter.R

import android.content.Context

class ItemAdapter( private val dataset: ArrayList<DayEntry>, private val context: Context):
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>()  {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCounter: TextView = view.findViewById(R.id.tv_counter)
        val tvEntryDate: TextView = view.findViewById(R.id.tv_entry_date)
        val tvDayOfWeek: TextView = view.findViewById(R.id.tv_day_of_week)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_day_entry, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount() = dataset.size


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        with(dataset[position]) {
            holder.tvCounter.text = counter.toString()
            holder.tvDayOfWeek.text = date.dayOfWeek.toString()
            val tvEntryDateText = String.format(
                context.getString(R.string.date_format),
                addLeadingZero(date.dayOfMonth.toString()),
                addLeadingZero(date.month.value.toString()),
                date.year)
            holder.tvEntryDate.text = tvEntryDateText
        }
    }

    private fun addLeadingZero(text: String): String {
        return if (text.length <= 1) {
            "0$text"
        } else {
            text
        }
    }
}