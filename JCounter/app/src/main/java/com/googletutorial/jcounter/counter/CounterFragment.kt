package com.googletutorial.jcounter.counter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.googletutorial.jcounter.R
import com.googletutorial.jcounter.common.DatabaseHelper
import com.googletutorial.jcounter.common.DayEntry
import com.googletutorial.jcounter.databinding.CounterFragmentBinding
import java.time.LocalDateTime

class CounterFragment : Fragment() {
    private lateinit var binding: CounterFragmentBinding
    private lateinit var viewModel: CounterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.counter_fragment,
            container,
            false
        )
        var dayEntry: DayEntry? = null
        try {
            dayEntry = requireArguments().getParcelable("dayEntry")
        } catch (e: RuntimeException) {
            Log.d(TAG, "no Entry passed to the Fragment: $e")
        }
        val dbHelper = DatabaseHelper(requireContext())
        val viewModelFactory = CounterViewModelFactory(dbHelper, dayEntry)
        viewModel = ViewModelProvider(this, viewModelFactory)[CounterViewModel::class.java]

        binding.btnPlus.setOnClickListener {
            viewModel.increaseCount()
        }
        binding.btnMinus.setOnClickListener {
            viewModel.decreaseCount()
        }

        binding.btnOverview.setOnClickListener {
            val navController = NavHostFragment.findNavController(this)
            navController.navigate(R.id.action_counterFragment_to_overviewFragment)
        }

        viewModel.dayEntry.observe(viewLifecycleOwner, { dE ->
            binding.tvTotalCount.text = dE.totalCount.toString()
            binding.tvTodayCount.text = dE.count.toString()
            binding.tvDate.text = getDateStringFromDate(dE.dateTime)
            viewModel.updateDatabase()
        })
        return binding.root
    }

    private fun getDateStringFromDate(d: LocalDateTime) =
        "${d.dayOfWeek}, ${d.dayOfMonth}. ${d.month} ${d.year}"

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        with(viewModel) {
            bringDbUpToDate()
            refreshDayEntry()
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.updateDatabase()
    }

    companion object {
        fun newInstance() = CounterFragment()
        const val TAG = "CounterFragment"
    }
}
