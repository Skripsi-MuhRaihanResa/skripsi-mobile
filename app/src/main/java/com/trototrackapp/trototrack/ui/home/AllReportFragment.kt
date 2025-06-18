package com.trototrackapp.trototrack.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.trototrackapp.trototrack.data.ResultState
import com.trototrackapp.trototrack.databinding.FragmentAllReportBinding
import com.trototrackapp.trototrack.ui.adapter.AllReportsAdapter
import com.trototrackapp.trototrack.ui.viewmodel.ReportsViewModel
import com.trototrackapp.trototrack.ui.viewmodel.ViewModelFactory
import org.json.JSONException
import org.json.JSONObject

class AllReportFragment : Fragment() {

    private lateinit var allReportsAdapter: AllReportsAdapter
    private var _binding: FragmentAllReportBinding? = null
    private val binding get() = _binding!!
    private val reportsViewModel: ReportsViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllReportBinding.inflate(inflater, container, false)
        val view = binding.root

        setupRecyclerView()
        setupObserver()
        setupSearchView()

        return view
    }

    private fun setupRecyclerView() {
        allReportsAdapter = AllReportsAdapter()
        binding.recycleViewReports.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = allReportsAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                reportsViewModel.getAllReports(query).observe(viewLifecycleOwner, Observer { result ->
                    when (result) {
                        is ResultState.Loading -> {
                            binding.progressIndicator.visibility = View.VISIBLE
                            binding.emptyView.visibility = View.GONE
                            binding.recycleViewReports.visibility = View.GONE
                        }
                        is ResultState.Success -> {
                            binding.progressIndicator.visibility = View.GONE
                            if (result.data.data.isNullOrEmpty()) {
                                binding.emptyView.visibility = View.VISIBLE
                                binding.recycleViewReports.visibility = View.GONE
                            } else {
                                binding.emptyView.visibility = View.GONE
                                binding.recycleViewReports.visibility = View.VISIBLE
                                allReportsAdapter.submitList(result.data.data)
                            }
                        }
                        is ResultState.Error -> {
                            binding.progressIndicator.visibility = View.GONE
                            binding.emptyView.visibility = View.VISIBLE
                            binding.recycleViewReports.visibility = View.GONE
                            val errorMessage = result.message.let {
                                try {
                                    val json = JSONObject(it)
                                    json.getString("message")
                                } catch (e: JSONException) {
                                    it
                                }
                            } ?: "An error occurred"
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                return true
            }
        })
    }

    private fun setupObserver() {
        reportsViewModel.getAllReports(null).observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is ResultState.Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                    binding.emptyView.visibility = View.GONE
                    binding.recycleViewReports.visibility = View.GONE
                }
                is ResultState.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    if (result.data.data.isNullOrEmpty()) {
                        binding.emptyView.visibility = View.VISIBLE
                        binding.recycleViewReports.visibility = View.GONE
                    } else {
                        binding.emptyView.visibility = View.GONE
                        binding.recycleViewReports.visibility = View.VISIBLE
                        allReportsAdapter.submitList(result.data.data)
                    }
                }
                is ResultState.Error -> {
                    binding.progressIndicator.visibility = View.GONE
                    binding.emptyView.visibility = View.VISIBLE
                    binding.recycleViewReports.visibility = View.GONE
                    val errorMessage = result.message.let {
                        try {
                            val json = JSONObject(it)
                            json.getString("message")
                        } catch (e: JSONException) {
                            it
                        }
                    } ?: "An error occurred"
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        setupObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
