package com.trototrackapp.trototrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.trototrackapp.trototrack.data.repository.ReportRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ReportsViewModel(private val reportRepository: ReportRepository) : ViewModel() {

    fun getAllReports(searchQuery: String?) =
        reportRepository.getAllReports(searchQuery)

    fun getReportsUser() =
        reportRepository.getReportsUser()

    fun addReport(locationName: RequestBody, reference_location: RequestBody, latitude: RequestBody, longitude: RequestBody, image: MultipartBody.Part, status_damage: RequestBody, description: RequestBody) =
        reportRepository.addReport(locationName, reference_location, latitude, longitude, image, status_damage, description)

    fun getReportDetail(reportId: String) =
        reportRepository.getReportDetail(reportId)

    fun voteReport(id: String) =
        reportRepository.voteReport(id)
}