package com.trototrackapp.trototrack.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.trototrackapp.trototrack.data.ResultState
import com.trototrackapp.trototrack.data.remote.request.NewPasswordRequest
import com.trototrackapp.trototrack.data.remote.request.SendOtpRequest
import com.trototrackapp.trototrack.data.remote.request.VerifyOtpRequest
import com.trototrackapp.trototrack.data.remote.response.NewPasswordResponse
import com.trototrackapp.trototrack.data.remote.response.SendOtpResponse
import com.trototrackapp.trototrack.data.remote.response.VerifyOtpResponse
import com.trototrackapp.trototrack.data.remote.retrofit.ApiService

class ForgetPasswordRepository(private val apiService: ApiService) {

    fun sendOtp(email: String): LiveData<ResultState<SendOtpResponse>> =
        liveData {
            emit(ResultState.Loading)
            try {
                val request = SendOtpRequest(email)
                val response = apiService.sendOtp(request)
                if (response.isSuccessful) {
                    emit(ResultState.Success(response.body()!!))
                } else {
                    emit(ResultState.Error(response.errorBody()?.string() ?: "An error occurred"))
                }
            } catch (e: Exception) {
                emit(ResultState.Error(e.message ?: "An error occurred"))
            }
        }

    fun verifyOtp(email: String, otp: String): LiveData<ResultState<VerifyOtpResponse>> =
        liveData {
            emit(ResultState.Loading)
            try {
                val request = VerifyOtpRequest(email, otp)
                val response = apiService.verifyOtp(request)
                if (response.isSuccessful) {
                    emit(ResultState.Success(response.body()!!))
                } else {
                    emit(ResultState.Error(response.errorBody()?.string() ?: "An error occurred"))
                }
            } catch (e: Exception) {
                emit(ResultState.Error(e.message ?: "An error occurred"))
            }
        }

    fun newPassword(password: String, confirmPassword: String): LiveData<ResultState<NewPasswordResponse>> =
        liveData {
            emit(ResultState.Loading)
            try {
                val request = NewPasswordRequest(password, confirmPassword)
                val response = apiService.newPassword(request)
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
        private var instance: ForgetPasswordRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): ForgetPasswordRepository =
            instance ?: synchronized(this) {
                instance ?: ForgetPasswordRepository(apiService)
            }.also { instance = it }
    }
}