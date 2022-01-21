package com.googletutorial.jcounter.common

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.googletutorial.jcounter.R
import java.time.LocalDateTime

class ItemAdapter(
    private val dataset: ArrayList<DayEntry>,
    private val context: Context,
    private val recyclerView: RecyclerView,
    private val navController: NavController
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val listItemLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_day_entry, parent, false)
        listItemLayout.setOnClickListener(ItemOnClickListener())

        return ItemViewHolder(listItemLayout)
    }

    override fun getItemCount() = dataset.size


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        with(dataset[position]) {
            holder.tvCounter.text = count.toString()
            holder.tvDayOfWeek.text = dateTime.dayOfWeek.toString()
            val tvEntryDateText = String.format(
                context.getString(R.string.date_format),
                addLeadingZero(dateTime.dayOfMonth.toString()),
                addLeadingZero(dateTime.month.value.toString()),
                dateTime.year
            )
            holder.tvEntryDate.text = tvEntryDateText
        }
    }


    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCounter: TextView = view.findViewById(R.id.tv_counter)
        val tvEntryDate: TextView = view.findViewById(R.id.tv_entry_date)
        val tvDayOfWeek: TextView = view.findViewById(R.id.tv_day_of_week)

    }

    inner class ItemOnClickListener : View.OnClickListener {

        override fun onClick(p0: View?) {
            val itemPosition: Int = recyclerView.getChildLayoutPosition(p0!!)
            val entry: DayEntry = dataset[itemPosition]
            val bundle = Bundle()
            bundle.putInt("id", entry.id!!)
            bundle.putString("dateString", getDateStringFromDate(entry.dateTime))
            bundle.putInt("count", entry.count)
            Log.i("OnItemClickListener", "id: ${entry.id}, dateString: ${getDateStringFromDate(entry.dateTime)}, count: ${entry.count}" )
            navController.navigate(R.id.action_overviewFragment_to_counterFragment, bundle)
        }
    }

    private fun getDateStringFromDate(d: LocalDateTime) =
        "${d.dayOfWeek}, ${d.dayOfMonth}. ${d.month} ${d.year}"

    private fun addLeadingZero(text: String): String {
        return if (text.length <= 1) {
            "0$text"
        } else {
            text
        }
    }


}

