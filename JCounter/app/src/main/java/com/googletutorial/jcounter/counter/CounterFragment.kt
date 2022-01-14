package com.googletutorial.jcounter.counter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.googletutorial.jcounter.R
import com.googletutorial.jcounter.common.DatabaseHelper
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


        val dbHelper = DatabaseHelper(requireContext())
        val viewModelFactory = CounterViewModelFactory(dbHelper)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CounterViewModel::class.java)
        with(viewModel) {
//            fillWithEntriesUntilToday()
        }

        updateTvDateWithCurrentDate()
        binding.btnPlus.setOnClickListener {
            viewModel.updateTodaysCount(1)
        }
        binding.btnMinus.setOnClickListener {
            viewModel.updateTodaysCount(-1)
        }

        binding.btnOverview.setOnClickListener {

            val navController = NavHostFragment.findNavController(this)
            navController.navigate(R.id.action_counterFragment_to_overviewFragment)
        }

        viewModel.totalCount.observe(viewLifecycleOwner, { totalCount ->
            binding.tvTotalCount.text = totalCount.toString()
        })

        viewModel.todaysCount.observe(viewLifecycleOwner, { counterValue ->
            with(viewModel) {
                updateTodaysCountInDb(counterValue)
            }
            binding.tvTodayCount.text = counterValue.toString()
        })

        return binding.root
    }

    private fun updateTvDateWithCurrentDate() {
        val date = LocalDateTime.now()
        val dateText = "${date.dayOfWeek}, ${date.dayOfMonth}. ${date.month} ${date.year}"
        binding.tvDate.text = dateText
    }

    override fun onResume() {
        super.onResume()
        updateTvDateWithCurrentDate()
        with(viewModel) {
            setTotalCount(getTotalCount())
            setTodaysCount(getTodaysCount())

        }
    }


    companion object {
        fun newInstance() = CounterFragment()
    }
}
