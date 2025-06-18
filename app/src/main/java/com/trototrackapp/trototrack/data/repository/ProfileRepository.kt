package com.trototrackapp.trototrack.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.trototrackapp.trototrack.data.ResultState
import com.trototrackapp.trototrack.data.remote.response.UpdateProfileResponse
import com.trototrackapp.trototrack.data.remote.request.UpdateProfileRequest
import com.trototrackapp.trototrack.data.remote.response.GetProfileResponse
import com.trototrackapp.trototrack.data.remote.retrofit.ApiService

class ProfileRepository(private val apiService: ApiService) {

    fun updateProfile(name: String, username: String): LiveData<ResultState<UpdateProfileResponse>> =
        liveData {
            emit(ResultState.Loading)
            try {
                val request = UpdateProfileRequest(name, username)
                val response = apiService.updateProfile(request)
                if (response.isSuccessful) {
                    emit(ResultState.Success(response.body()!!))
                } else {
                    emit(ResultState.Error(response.errorBody()?.string() ?: "An error occurred"))
                }
            } catch (e: Exception) {
                emit(ResultState.Error(e.message ?: "An error occurred"))
            }
        }

    fun getUserProfile(): LiveData<ResultState<GetProfileResponse>> =
        liveData {
            emit(ResultState.Loading)
            try {
                val response = apiService.getUserProfile()
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
        private var instance: ProfileRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): ProfileRepository =
            instance ?: synchronized(this) {
                instance ?: ProfileRepository(apiService)
            }.also { instance = it }
    }
}