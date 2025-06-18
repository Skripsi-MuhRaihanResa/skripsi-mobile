package com.trototrackapp.trototrack.ui.home

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.trototrackapp.trototrack.data.ResultState
import com.trototrackapp.trototrack.databinding.FragmentToolsBinding
import com.trototrackapp.trototrack.ui.result.ResultActivity
import com.trototrackapp.trototrack.ui.viewmodel.ScanViewModel
import com.trototrackapp.trototrack.ui.viewmodel.ViewModelFactory
import com.trototrackapp.trototrack.util.getImageUri
import com.trototrackapp.trototrack.util.uriToFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONException
import org.json.JSONObject

class ToolsFragment : Fragment() {

    private var _binding: FragmentToolsBinding? = null
    private val binding get() = _binding!!
    private var currentImageUri: Uri? = null
    private val scanViewModel: ScanViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val resultUri = result.uriContent
            currentImageUri = resultUri
            showImage()
        } else {
            val error = result.error
            Log.e("Crop Error", error?.message ?: "Unknown error")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentToolsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cameraButton.setOnClickListener {
            startCamera()
        }

        binding.galleryButton.setOnClickListener {
            startGallery()
        }

        binding.scanButton.setOnClickListener {
            if (currentImageUri == null) {
                Toast.makeText(requireContext(), "Please capture an image first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val file = uriToFile(currentImageUri!!, requireContext())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "image", file.name, requestImageFile
            )

            binding.progressIndicator.visibility = View.VISIBLE
            scanViewModel.scan(imageMultipart).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is ResultState.Loading -> {
                        binding.progressIndicator.visibility = View.VISIBLE
                    }
                    is ResultState.Success -> {
                        binding.progressIndicator.visibility = View.GONE
                        Toast.makeText(requireContext(), "Scan successful", Toast.LENGTH_SHORT).show()

                        val label = result.data.data?.label
                        val description = result.data.data?.description

                        val intent = Intent(requireContext(), ResultActivity::class.java)
                        intent.putExtra("label", label)
                        intent.putExtra("description", description)
                        intent.putExtra("imageUri", currentImageUri.toString())
                        startActivity(intent)
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
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.imageHolder.setImageURI(it)
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            startCrop(currentImageUri)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(requireContext())
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            startCrop(currentImageUri)
        }
    }

    private fun startCrop(uri: Uri?) {
        uri?.let {
            showPreCropDialog(it)
        }
    }

    private fun showPreCropDialog(uri: Uri) {
        AlertDialog.Builder(requireContext())
            .setTitle("Highlight Guidance")
            .setMessage("Please highlight the section of sidewalk in your photo!")
            .setPositiveButton("OK") { _, _ ->
                cropImage.launch(
                    CropImageContractOptions(
                        uri = uri,
                        cropImageOptions = CropImageOptions(
                            guidelines = CropImageView.Guidelines.ON,
                            fixAspectRatio = false,
                            outputCompressFormat = Bitmap.CompressFormat.JPEG
                        )
                    )
                )
            }
            .show()
    }
}