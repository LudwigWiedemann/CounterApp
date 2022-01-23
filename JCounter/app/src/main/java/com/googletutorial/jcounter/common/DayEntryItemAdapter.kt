package com.googletutorial.jcounter.common

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.googletutorial.jcounter.R

class DayEntryItemAdapter(
    private val dataset: ArrayList<DayEntry>,
    private val context: Context,
    private val recyclerView: RecyclerView,
    private val navController: NavController
) : RecyclerView.Adapter<DayEntryItemAdapter.DayEntryItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayEntryItemViewHolder {
        // create a new view
        val listItemLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_day_entry, parent, false)
        listItemLayout.setOnClickListener(ItemOnClickListener())
        Log.i("dayEntryItemAdapter", "somethiiiiiing pls")

        return DayEntryItemViewHolder(listItemLayout)
    }

    override fun getItemCount() = dataset.size


    override fun onBindViewHolder(holder: DayEntryItemViewHolder, position: Int) {
        with(dataset[position]) {
            holder.tvCounter.text = getCount().toString()
            holder.tvDayOfWeek.text = date.dayOfWeek.toString()
            val tvEntryDateText = String.format(
                context.getString(R.string.date_format),
                addLeadingZero(date.dayOfMonth.toString()),
                addLeadingZero(date.month.value.toString()),
                date.year
            )
            holder.tvEntryDate.text = tvEntryDateText
        }
        Log.i("DayEntryItemAdapter", dataset[position].toString())

    }


    inner class DayEntryItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCounter: TextView = view.findViewById(R.id.tv_counter)
        val tvEntryDate: TextView = view.findViewById(R.id.tv_entry_date)
        val tvDayOfWeek: TextView = view.findViewById(R.id.tv_day_of_week)

    }

    inner class ItemOnClickListener : View.OnClickListener {

        override fun onClick(p0: View?) {
            val itemPosition: Int = recyclerView.getChildLayoutPosition(p0!!)
            val entry: DayEntry = dataset[itemPosition]
            val bundle = Bundle()
            bundle.putSerializable("entryInList", arrayListOf(entry))
            navController.navigate(R.id.action_overviewFragment_to_counterFragment, bundle)
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

