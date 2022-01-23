package com.googletutorial.jcounter.counter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.googletutorial.jcounter.R
import com.googletutorial.jcounter.common.*
import com.googletutorial.jcounter.databinding.CounterFragmentBinding

class CounterFragment : Fragment(), RecyclerViewClickListener {
    private lateinit var binding: CounterFragmentBinding
    private lateinit var viewModel: CounterViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataset: ArrayList<TimeEntry>
    private lateinit var timeItemAdapter: TimeItemAdapter


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
        var tempDayEntry: DayEntry? = null
        try {
            with(requireArguments()) {
                val entryInList = getSerializable("entryInList") as ArrayList<DayEntry>
                tempDayEntry = entryInList[0]
//                Log.i(TAG, entryInList[0].toString())

            }
        }catch (e:IllegalStateException) {
            Log.i(TAG, "The Fragment has been started without a bundle $e")
        }catch (e: RuntimeException) {
            Log.d(TAG, "in CounterFragment.onCreate: $e")
        }
        val dbHelper = DatabaseHelper(requireContext())
        val viewModelFactory = CounterViewModelFactory(dbHelper, tempDayEntry)


        viewModel = ViewModelProvider(this, viewModelFactory)[CounterViewModel::class.java]

        recyclerView = binding.recyclerView
        dataset = viewModel.getDatasetForAdapter()
        timeItemAdapter = TimeItemAdapter(this, recyclerView, dataset)
        recyclerView.adapter = timeItemAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

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

        viewModel.count.observe(viewLifecycleOwner, { count ->
            if (count >= 0) {
                viewModel.updateDatabase()
                viewModel.reloadTimeList()
                updateUi(count)
            } else {
                showStupidMessage()
            }
        })

        return binding.root
    }

    private fun showStupidMessage() {
        Toast.makeText(requireContext(), "Bist du eigentlich komplett behindert?", Toast.LENGTH_SHORT).show()
    }

    private fun updateUi(count: Int) {
        with(viewModel.getDayEntry()) {
            binding.tvTotalCount.text =  viewModel.getTotalJCount().toString()
            binding.tvCount.text = count.toString()
            binding.tvDate.text = viewModel.getDateStringFromDate(date)
            timeItemAdapter.setDataset(viewModel.getDatasetForAdapter())
        }
        dataset = viewModel.getDatasetForAdapter()
        timeItemAdapter = TimeItemAdapter(this, recyclerView, dataset)
        recyclerView.adapter = timeItemAdapter
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

    companion object {
        const val TAG = "CounterFragment"
    }

    override fun recyclerViewListClicked(entry: TimeEntry) {
       viewModel.removeTimeEntry(entry)
    }
}
