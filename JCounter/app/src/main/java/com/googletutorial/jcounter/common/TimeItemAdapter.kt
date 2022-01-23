package com.googletutorial.jcounter.common

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.googletutorial.jcounter.R
import java.time.LocalTime

class TimeItemAdapter(private val itemClickListener: RecyclerViewClickListener, private val recyclerView: RecyclerView, private var dataset: ArrayList<TimeEntry>): RecyclerView.Adapter<TimeItemAdapter.TimeItemViewHolder>() {

    init {
        Log.i("TimeItemAdapter", "dataset = $dataset")

    }

    fun setDataset(newDataset: ArrayList<TimeEntry>) {
        dataset = newDataset
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeItemAdapter.TimeItemViewHolder {
        val listItemLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_time, parent, false)
        listItemLayout.setOnClickListener(ItemOnClickListener())
        Log.i("TimeItemAdapter", dataset.toString())


        return TimeItemViewHolder(listItemLayout)
    }

    override fun onBindViewHolder(holder: TimeItemAdapter.TimeItemViewHolder, position: Int) {
            holder.tvTime.text = dataset[position].getTimeString()
            Log.i("TimeItemAdapter", dataset[position].toString())

    }

    override fun getItemCount(): Int {
        Log.i("TimeItemAdapter", "itemCount: ${dataset.size}")

        return dataset.size
    }

    inner class TimeItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTime: TextView = view.findViewById(R.id.tv_time)

    }

    inner class ItemOnClickListener : View.OnClickListener {

        override fun onClick(p0: View?) {
            val itemPosition: Int = recyclerView.getChildLayoutPosition(p0!!)
            val entry: TimeEntry = dataset[itemPosition]
           itemClickListener.recyclerViewListClicked(entry)
        }
    }
}