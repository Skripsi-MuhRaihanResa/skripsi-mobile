package com.trototrackapp.trototrack.ui.add

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.trototrackapp.trototrack.data.ResultState
import com.trototrackapp.trototrack.databinding.ActivityAddReportBinding
import com.trototrackapp.trototrack.ui.home.MainActivity
import com.trototrackapp.trototrack.ui.viewmodel.ReportsViewModel
import com.trototrackapp.trototrack.ui.viewmodel.ViewModelFactory
import com.trototrackapp.trototrack.util.uriToFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

class AddReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddReportBinding
    private val reportViewModel: ReportsViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private var lat: Double? = null
    private var long: Double? = null

    companion object {
        const val REQUEST_MAP = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val label = intent.getStringExtra("label")
        val imageUriString = intent.getStringExtra("imageUri")

        Glide.with(this)
            .load(imageUriString)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(binding.imageHolder)

        binding.mapButton.setOnClickListener {
            startActivityForResult(Intent(this, MapsActivity::class.java), REQUEST_MAP)
        }

        binding.submitReportButton.setOnClickListener {
            val location = binding.mapLocationEditText.text.toString()
            val referenceLocation = binding.addressEditText.text.toString()
            val statusDamage = label ?: "DefaultStatusDamage"
            val description = binding.descriptionEditTextLayout.text.toString()
            val latitude = lat
            val longitude = long

            val imageUri = Uri.parse(imageUriString)
            val file = uriToFile(imageUri, this)
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val image: MultipartBody.Part = MultipartBody.Part.createFormData("image", file.name, requestImageFile)

            val locationRequestBody = location.toRequestBody("text/plain".toMediaTypeOrNull())
            val referenceLocationRequestBody = referenceLocation.toRequestBody("text/plain".toMediaTypeOrNull())
            val statusDamageRequestBody = statusDamage.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionRequestBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val latitudeRequestBody = latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val longitudeRequestBody = longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            binding.progressIndicator.visibility = View.VISIBLE
            reportViewModel.addReport(
                locationRequestBody,
                referenceLocationRequestBody,
                latitudeRequestBody,
                longitudeRequestBody,
                image,
                statusDamageRequestBody,
                descriptionRequestBody
            ).observe(this) { result ->
                when (result) {
                    is ResultState.Loading -> {
                        binding.progressIndicator.visibility = View.VISIBLE
                    }
                    is ResultState.Success -> {
                        binding.progressIndicator.visibility = View.GONE
                        Toast.makeText(this, "Report added successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finishAffinity()
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
                        val dialog = AlertDialog.Builder(this)
                            .setMessage(errorMessage)
                            .setPositiveButton("OK", null)
                            .create()
                        dialog.show()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_MAP && resultCode == Activity.RESULT_OK) {
            val locationName = data?.getStringExtra("LOCATION_NAME")
            binding.mapLocationEditText.setText(locationName ?: "")
            lat = data?.getDoubleExtra("LATITUDE", 0.0)
            long = data?.getDoubleExtra("LONGITUDE", 0.0)
        }
    }
}

