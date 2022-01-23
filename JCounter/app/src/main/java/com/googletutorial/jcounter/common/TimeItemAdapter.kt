package com.googletutorial.jcounter.common

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.googletutorial.jcounter.R
import java.time.LocalTime

class TimeItemAdapter(private val dataset: ArrayList<LocalTime>): RecyclerView.Adapter<TimeItemAdapter.TimeItemViewHolder>() {

    init {
        Log.i("TimeItemAdapter", "dataset = $dataset")

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeItemAdapter.TimeItemViewHolder {
        val listItemLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_time, parent, false)
        Log.i("TimeItemAdapter", dataset.toString())


        return TimeItemViewHolder(listItemLayout)
    }

    override fun onBindViewHolder(holder: TimeItemAdapter.TimeItemViewHolder, position: Int) {
            holder.tvTime.text = dataset[position].toString()
            Log.i("TimeItemAdapter", dataset[position].toString())

    }

    override fun getItemCount(): Int {
        Log.i("TimeItemAdapter", "itemCount: ${dataset.size}")

        return dataset.size
    }

    inner class TimeItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTime: TextView = view.findViewById(R.id.tv_time)

    }
}