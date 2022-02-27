package com.googletutorial.jcounter.overview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.googletutorial.jcounter.R
import com.googletutorial.jcounter.common.DatabaseHelper
import com.googletutorial.jcounter.common.DayEntryItemAdapter
import com.googletutorial.jcounter.databinding.OverviewFragmentBinding
import java.time.LocalDate
import java.util.*

class OverviewFragment : Fragment() {
    private lateinit var binding: OverviewFragmentBinding
    private lateinit var viewModel: OverviewViewModel
    lateinit var dbHelper: DatabaseHelper

    lateinit var dateFrom: LocalDate
    lateinit var dateUntil: LocalDate


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.overview_fragment,
            container,
            false
        )
        dbHelper = DatabaseHelper(requireContext())
        dateFrom = dbHelper.getOldestEntry().date
        dateUntil = dbHelper.getLatestEntry().date
        val viewModelFactory = OverviewViewModelFactory(dbHelper)
        viewModel = ViewModelProvider(this, viewModelFactory).get(OverviewViewModel::class.java)
        val recyclerView = binding.recyclerView
        val dataset = viewModel.getDatasetForAdapter()
        recyclerView.adapter =
            DayEntryItemAdapter(dataset, requireContext(), recyclerView, NavHostFragment.findNavController(this))
        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true)
        binding.calendarViewUntil.visibility = View.INVISIBLE
        binding.calendarViewFrom.visibility = View.INVISIBLE
        binding.calendarViewFrom.setDate(GregorianCalendar(2022, 0, 1).timeInMillis, true, true)
        binding.tvAverage.text = String.format(
            requireContext().getString(R.string.avg_format),
            dateFrom,
            dateUntil,
            dbHelper.getAverageBetweenDates(dateFrom, dateUntil).toString()
        )
        binding.tvAverage.setOnClickListener {
            toggleUiVisibility()
        }

        binding.calendarViewUntil.setOnDateChangeListener(UntilDateChangeListener())
        binding.calendarViewFrom.setOnDateChangeListener(FromDateChangeListener())
        return binding.root
    }

    private fun toggleUiVisibility() {
        if (binding.recyclerView.visibility == View.INVISIBLE) {
            binding.calendarViewUntil.visibility = View.INVISIBLE
            binding.calendarViewFrom.visibility = View.INVISIBLE
            binding.recyclerView.visibility = View.VISIBLE
        } else {
            binding.calendarViewUntil.visibility = View.VISIBLE
            binding.calendarViewFrom.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.INVISIBLE
        }
    }

    inner class FromDateChangeListener: CalendarView.OnDateChangeListener {
        override fun onSelectedDayChange(p0: CalendarView, p1: Int, p2: Int, p3: Int) {
            dateFrom = LocalDate.of(p1, p2+1, p3)
            binding.tvAverage.text = String.format(
                requireContext().getString(R.string.avg_format),
                dateFrom,
                dateUntil,
                dbHelper.getAverageBetweenDates(dateFrom, dateUntil).toString()
            )
            Log.i("bla", "from: $dateFrom" )

        }
    }

    inner class UntilDateChangeListener: CalendarView.OnDateChangeListener {
        override fun onSelectedDayChange(p0: CalendarView, p1: Int, p2: Int, p3: Int) {
            dateUntil = LocalDate.of(p1, p2+1, p3)
            Log.i("bla", "until: $dateUntil" )
            binding.tvAverage.text = String.format(
                requireContext().getString(R.string.avg_format),
                dateFrom,
                dateUntil,
                dbHelper.getAverageBetweenDates(dateFrom, dateUntil).toString()
            )
        }
    }

}
