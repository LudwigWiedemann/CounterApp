package com.googletutorial.jcounter.overview

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
import com.googletutorial.jcounter.common.ItemAdapter
import com.googletutorial.jcounter.databinding.OverviewFragmentBinding

class OverviewFragment : Fragment() {
    private lateinit var binding: OverviewFragmentBinding
    private lateinit var viewModel: OverviewViewModel
    lateinit var dbHelper: DatabaseHelper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.overview_fragment,
            container,
            false
        )
        dbHelper = DatabaseHelper(requireContext())
        val viewModelFactory = OverviewViewModelFactory(dbHelper)
        viewModel = ViewModelProvider(this, viewModelFactory).get(OverviewViewModel::class.java)
        val recyclerView = binding.recyclerView
        val dataset = viewModel.getDatasetForAdapter()
        recyclerView.adapter =
            ItemAdapter(dataset, requireContext(), recyclerView, NavHostFragment.findNavController(this))
        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true)

        return binding.root
    }
}
