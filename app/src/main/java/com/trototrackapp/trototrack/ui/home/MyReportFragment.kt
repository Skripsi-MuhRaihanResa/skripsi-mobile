    package com.trototrackapp.trototrack.ui.home

    import android.os.Bundle
    import androidx.fragment.app.Fragment
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Toast
    import androidx.fragment.app.viewModels
    import androidx.lifecycle.Observer
    import androidx.recyclerview.widget.LinearLayoutManager
    import com.trototrackapp.trototrack.data.ResultState
    import com.trototrackapp.trototrack.databinding.FragmentMyReportBinding
    import com.trototrackapp.trototrack.ui.adapter.UserReportsAdapter
    import com.trototrackapp.trototrack.ui.viewmodel.ReportsViewModel
    import com.trototrackapp.trototrack.ui.viewmodel.ViewModelFactory
    import org.json.JSONException
    import org.json.JSONObject

    class MyReportFragment : Fragment() {

        private lateinit var userReportsAdapter: UserReportsAdapter
        private var _binding: FragmentMyReportBinding? = null
        private val binding get() = _binding!!
        private val reportsViewModel: ReportsViewModel by viewModels {
            ViewModelFactory.getInstance(requireActivity())
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = FragmentMyReportBinding.inflate(inflater, container, false)
            val view = binding.root

            setupRecyclerView()
            setupObserver()

            return view
        }

        private fun setupRecyclerView() {
            userReportsAdapter = UserReportsAdapter()
            binding.recycleViewReports.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = userReportsAdapter
            }
        }

        private fun setupObserver() {
            reportsViewModel.getReportsUser().observe(viewLifecycleOwner, Observer { result ->
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
                            userReportsAdapter.submitList(result.data.data)
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
    }