package com.trototrackapp.trototrack.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.trototrackapp.trototrack.R
import com.trototrackapp.trototrack.data.ResultState
import com.trototrackapp.trototrack.data.model.SliderData
import com.trototrackapp.trototrack.databinding.FragmentHomeBinding
import com.trototrackapp.trototrack.ui.adapter.ArticlesAdapter
import com.trototrackapp.trototrack.ui.adapter.SliderAdapter
import com.trototrackapp.trototrack.ui.detail.DetailAccountActivity
import com.trototrackapp.trototrack.ui.viewmodel.ArticlesViewModel
import com.trototrackapp.trototrack.ui.viewmodel.ViewModelFactory
import org.json.JSONException
import org.json.JSONObject

class HomeFragment : Fragment() {

    private lateinit var articlesAdapter: ArticlesAdapter
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val articlesViewModel: ArticlesViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var slideAdapter: SliderAdapter
    private val list = ArrayList<SliderData>()
    private lateinit var dots: ArrayList<TextView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObserver()

        list.add(SliderData(R.drawable.home1))
        list.add(SliderData(R.drawable.home2))
        list.add(SliderData(R.drawable.home3))

        slideAdapter = SliderAdapter(list)
        binding.viewPager.adapter = slideAdapter
        dots = ArrayList()
        setIndicator()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                selectedDot(position)
                super.onPageSelected(position)
            }

        })

        binding.detailAccountButton.setOnClickListener {
            val intent = Intent(activity, DetailAccountActivity::class.java)
            startActivity(intent)
        }
    }

    private fun selectedDot(position: Int) {
        for (i in 0 until list.size) {
            if (i == position)
                dots[i].setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow))
            else
                dots[i].setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
        }
    }

    private fun setIndicator() {
        val dotSize = 24
        val dotMargin = 8

        for (i in 0 until list.size) {
            dots.add(TextView(requireContext()).apply {
                text = Html.fromHtml("&#9679;", Html.FROM_HTML_MODE_LEGACY).toString()
                textSize = dotSize.toFloat()
                setTextColor(ContextCompat.getColor(requireContext(), R.color.light_yellow))
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(dotMargin, 0, dotMargin, 0)
                layoutParams = params
            })
            binding.dotsIndicator.addView(dots[i])
        }
    }

    private fun setupRecyclerView() {
        articlesAdapter = ArticlesAdapter()
        binding.recycleViewArticle.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = articlesAdapter
        }
    }

    private fun setupObserver() {
        articlesViewModel.getArticle().observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is ResultState.Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                }
                is ResultState.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    articlesAdapter.submitList(result.data.data)
                }
                is ResultState.Error -> {
                    binding.progressIndicator.visibility = View.GONE
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
