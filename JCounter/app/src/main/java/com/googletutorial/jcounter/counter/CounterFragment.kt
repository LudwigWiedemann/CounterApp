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
import com.googletutorial.jcounter.databinding.CounterFragmentBinding

class CounterFragment : Fragment() {
    private lateinit var binding: CounterFragmentBinding
    private lateinit var viewModel: CounterViewModel
    private var totalCount = 0

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
        var iDateString = ""
        var iDayEntryId = -1
        var iCount = -1
        try {
            with(requireArguments()) {
                iDayEntryId = getInt("id")
                iDateString = getString("dateString")!!
                iCount = getInt("count")
                Log.i(TAG, "id: $iDayEntryId, dateString: $iDateString, count: $iCount" )

            }
        }catch (e:IllegalStateException) {
            Log.i(TAG, "The Fragment has been started without a bundle $e")
        }catch (e: RuntimeException) {
            Log.d(TAG, "in CounterFragment.onCreate: $e")
        }
        val dbHelper = DatabaseHelper(requireContext())
        val viewModelFactory = CounterViewModelFactory(dbHelper, iDayEntryId, iDateString, iCount)

        viewModel = ViewModelProvider(this, viewModelFactory)[CounterViewModel::class.java]

        totalCount = viewModel.getTotalJCount()
        binding.tvTotalCount.text = totalCount.toString()
        binding.tvDate.text = viewModel.dateString


        binding.btnPlus.setOnClickListener {
            viewModel.increaseCount()
            totalCount ++
            binding.tvTotalCount.text = totalCount.toString()
        }
        binding.btnMinus.setOnClickListener {
            viewModel.decreaseCount()
            totalCount --
            binding.tvTotalCount.text = totalCount.toString()
        }

        binding.btnOverview.setOnClickListener {
            val navController = NavHostFragment.findNavController(this)
            navController.navigate(R.id.action_counterFragment_to_overviewFragment)
        }

        viewModel.count.observe(viewLifecycleOwner, { count ->
            binding.tvCount.text = count.toString()
            viewModel.updateDatabase()
        })
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        with(viewModel) {
            fillDbUntilNow()
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.updateDatabase()
    }

    companion object {
        const val TAG = "CounterFragment"
    }
}
