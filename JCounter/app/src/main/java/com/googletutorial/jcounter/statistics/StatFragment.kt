package com.googletutorial.jcounter.statistics

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.googletutorial.jcounter.R
import com.googletutorial.jcounter.common.DatabaseHelper
import com.googletutorial.jcounter.databinding.StatFragmentBinding
import java.time.LocalDate
import java.util.*

class StatFragment : Fragment() {
    private lateinit var binding: StatFragmentBinding
    private lateinit var viewModel: StatViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.stat_fragment,
            container,
            false
        )
        val dbHelper = DatabaseHelper(requireContext())
        val viewModelFactory = StatViewModelFactory(dbHelper)
        viewModel = ViewModelProvider(this, viewModelFactory)[StatViewModel::class.java]
        refreshDatesAndAvg()
        hideCalendar()


        binding.btnStartDate.setOnClickListener {
            binding.calendarView.setOnDateChangeListener(StartDateChangeListener())
            showCalendar(viewModel.startDate)
            Log.i("Statistics", "button 1")

        }
        binding.btnEndDate.setOnClickListener {
            binding.calendarView.setOnDateChangeListener(EndDateChangeListener())
            showCalendar(viewModel.endDate)
        }

        binding.btnHide.setOnClickListener {
            hideCalendar()
        }

        return binding.root
    }

    private fun showCalendar(date: LocalDate) {
        binding.calendarView.setDate(GregorianCalendar(date.year, date.monthValue -1, date.dayOfMonth).timeInMillis, true, true)
        binding.calendarView.visibility = View.VISIBLE
        binding.btnHide.visibility = View.VISIBLE
    }

    private fun hideCalendar() {
        binding.calendarView.visibility = View.GONE
        binding.btnHide.visibility = View.GONE
    }

    private fun refreshDatesAndAvg() {
        Log.i("Statistics", "refresh")
        binding.tvAverage.text = String.format(
            requireContext().getString(R.string.avg_format),
            viewModel.startDate,
            viewModel.endDate,
            viewModel.getAverage().toString().substring(0,3)
        )
    }

    inner class StartDateChangeListener: CalendarView.OnDateChangeListener {
        override fun onSelectedDayChange(p0: CalendarView, p1: Int, p2: Int, p3: Int) {
            viewModel.startDate = LocalDate.of(p1, p2+1, p3)
            refreshDatesAndAvg()
            Log.i("bla", "from: ${viewModel.startDate} " )
        }
    }

    inner class EndDateChangeListener: CalendarView.OnDateChangeListener {
        override fun onSelectedDayChange(p0: CalendarView, p1: Int, p2: Int, p3: Int) {
            viewModel.endDate = LocalDate.of(p1, p2 + 1, p3)
            refreshDatesAndAvg()
            Log.i("bla", "until: ${viewModel.endDate}")
        }
    }

}