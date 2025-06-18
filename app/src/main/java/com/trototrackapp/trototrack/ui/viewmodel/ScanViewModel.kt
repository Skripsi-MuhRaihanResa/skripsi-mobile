package com.trototrackapp.trototrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.trototrackapp.trototrack.data.repository.ScanRepository
import okhttp3.MultipartBody

class ScanViewModel(private val scanRepository: ScanRepository) : ViewModel()  {

    fun scan(image: MultipartBody.Part) = scanRepository.scan(image)
}