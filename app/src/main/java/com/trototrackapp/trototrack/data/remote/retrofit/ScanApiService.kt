package com.trototrackapp.trototrack.data.remote.retrofit

import com.trototrackapp.trototrack.data.remote.response.ScanResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ScanApiService {
    @Multipart
    @POST("predict")
    suspend fun scan(
        @Part image: MultipartBody.Part,
    ): Response<ScanResponse>
}
