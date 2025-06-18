package com.trototrackapp.trototrack.ui.result

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.trototrackapp.trototrack.R
import com.trototrackapp.trototrack.databinding.FragmentResultConfirmationBinding

class ResultConfirmationFragment : Fragment() {

    private var _binding: FragmentResultConfirmationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val label = arguments?.getString("label") ?: "No label"
        binding.scanResult.text = label

        when (label) {
            "Light Damaged" -> binding.scanResult.setTextColor(Color.YELLOW)
            "Heavy Damaged" -> binding.scanResult.setTextColor(Color.RED)
            "Good" -> binding.scanResult.setTextColor(Color.GREEN)
            else -> binding.scanResult.setTextColor(Color.BLACK) // Warna default jika label tidak cocok
        }

        binding.confirmButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(0, R.anim.slide_down)
                .remove(this)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val ARG_LABEL = "label"

        fun newInstance(label: String): ResultConfirmationFragment {
            val fragment = ResultConfirmationFragment()
            val args = Bundle()
            args.putString(ARG_LABEL, label)
            fragment.arguments = args
            return fragment
        }
    }
}