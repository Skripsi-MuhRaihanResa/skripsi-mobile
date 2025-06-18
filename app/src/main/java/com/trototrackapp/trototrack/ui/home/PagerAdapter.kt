package com.trototrackapp.trototrack.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

import androidx.fragment.app.FragmentActivity

class PagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AllReportFragment()
            1 -> MyReportFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}