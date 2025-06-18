package com.trototrackapp.trototrack.ui.detail

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.trototrackapp.trototrack.R
import com.trototrackapp.trototrack.data.ResultState
import com.trototrackapp.trototrack.databinding.ActivityDetailReportBinding
import com.trototrackapp.trototrack.ui.viewmodel.ReportsViewModel
import com.trototrackapp.trototrack.ui.viewmodel.ViewModelFactory
import com.trototrackapp.trototrack.util.convertIso8601ToDate
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class DetailReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailReportBinding
    private val reportViewModel: ReportsViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val reportId = intent.getStringExtra(REPORT_ID)

        binding.voteButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Vote Confirmation")
                .setMessage("Are you sure you want to vote on this report?")
                .setPositiveButton("Yes") { dialog, _ ->
                    lifecycleScope.launch {
                        if (reportId != null) {
                            reportViewModel.voteReport(reportId).observe(this@DetailReportActivity) { result ->
                                when (result) {
                                    is ResultState.Loading -> {
                                        binding.progressIndicator.visibility = View.VISIBLE
                                    }

                                    is ResultState.Success -> {
                                        binding.progressIndicator.visibility = View.GONE
                                        Toast.makeText(this@DetailReportActivity, "Vote success", Toast.LENGTH_SHORT).show()

                                        reportViewModel.getReportDetail(reportId).observe(this@DetailReportActivity) { reportResult ->
                                            if (reportResult is ResultState.Success) {
                                                val updatedReportDetail = reportResult.data.data
                                                if (updatedReportDetail != null) {
                                                    binding.reportVote.text = updatedReportDetail.like.toString()
                                                }
                                            }
                                        }
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
                                        Toast.makeText(this@DetailReportActivity, errorMessage, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        reportId?.let { nonNullReportId ->
            setupObserver(nonNullReportId)
        } ?: run {
            Toast.makeText(this, "Report ID is missing", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupObserver(reportId: String) {
        reportViewModel.getReportDetail(reportId).observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                }
                is ResultState.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    val reportDetail = result.data.data
                    if (reportDetail != null) {
                        Glide.with(binding.root)
                            .load(reportDetail.image)
                            .into(binding.reportImage)
                        binding.reportStatus.text = reportDetail.status
                        binding.reportDate.text = convertIso8601ToDate(reportDetail.createdAt ?: "")
                        binding.reportVote.text = reportDetail.like.toString()
                        binding.reportLocation.text = reportDetail.location
                        binding.reportDescription.text = reportDetail.description
                        binding.reportStatusDamage.text = reportDetail.statusDamage

                        val backgroundColorStatusDamage = when (reportDetail.statusDamage) {
                            "Light Damaged" -> ContextCompat.getColor(this, R.color.yellow)
                            "Heavy Damaged" -> ContextCompat.getColor(this, R.color.red)
                            "Good" -> ContextCompat.getColor(this, R.color.green)
                            else -> Color.TRANSPARENT
                        }

                        val backgroundColorStatus = when (reportDetail.status) {
                            "Pending" -> ContextCompat.getColor(this, R.color.yellow)
                            "Rejected" -> ContextCompat.getColor(this, R.color.red)
                            "Approved" -> ContextCompat.getColor(this, R.color.green)
                            else -> Color.TRANSPARENT
                        }

                        if (reportDetail.reason != null) {
                            binding.View3.visibility = View.VISIBLE
                            binding.reason.visibility = View.VISIBLE
                            binding.reportReason.visibility = View.VISIBLE
                        } else {
                            binding.View3.visibility = View.INVISIBLE
                            binding.reason.visibility = View.INVISIBLE
                            binding.reportReason.visibility = View.INVISIBLE
                        }

                        binding.reportStatusDamage.backgroundTintList = ColorStateList.valueOf(backgroundColorStatusDamage)
                        binding.reportStatus.backgroundTintList = ColorStateList.valueOf(backgroundColorStatus)

                        binding.buttonCheckLocation.setOnClickListener {
                            val latitude = reportDetail.latitude
                            val longitude = reportDetail.longitude

                            if (latitude != null && longitude != null) {
                                val gmmIntentUri = Uri.parse("geo:0,0?q=$latitude,$longitude")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps")

                                if (mapIntent.resolveActivity(packageManager) != null) {
                                    startActivity(mapIntent)
                                } else {
                                    Toast.makeText(this, "Google Maps app not found", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this, "Latitude or longitude is missing", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                is ResultState.Error -> {
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val REPORT_ID = "report_id"
    }
}
