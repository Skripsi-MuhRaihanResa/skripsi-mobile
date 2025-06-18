package com.trototrackapp.trototrack.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.trototrackapp.trototrack.data.ResultState
import com.trototrackapp.trototrack.data.remote.response.ScanResponse
import com.trototrackapp.trototrack.data.remote.retrofit.ApiService
import okhttp3.MultipartBody

class ScanRepository(private val apiService: ApiService) {

    fun scan(image: MultipartBody.Part): LiveData<ResultState<ScanResponse>> =
        liveData {
            emit(ResultState.Loading)
            try {
                val response = apiService.scan(image)
                if (response.isSuccessful) {
                    emit(ResultState.Success(response.body()!!))
                } else {
                    emit(ResultState.Error(response.errorBody()?.string() ?: "An error occurred"))
                }
            } catch (e: Exception) {
                emit(ResultState.Error(e.message ?: "An error occurred"))
            }
        }

    companion object {
        @Volatile
        private var instance: ScanRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): ScanRepository =
            instance ?: synchronized(this) {
                instance ?: ScanRepository(apiService)
            }.also { instance = it }
    }
}