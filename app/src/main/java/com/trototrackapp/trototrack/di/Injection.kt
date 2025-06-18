package com.trototrackapp.trototrack.di

import android.content.Context
import com.trototrackapp.trototrack.data.local.UserPreference
import com.trototrackapp.trototrack.data.remote.retrofit.ApiConfig
import com.trototrackapp.trototrack.data.repository.ArticleRepository
import com.trototrackapp.trototrack.data.repository.AuthRepository
import com.trototrackapp.trototrack.data.repository.ForgetPasswordRepository
import com.trototrackapp.trototrack.data.repository.ProfileRepository
import com.trototrackapp.trototrack.data.repository.ReportRepository
import com.trototrackapp.trototrack.data.repository.ScanRepository

object Injection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val userPreference = UserPreference.getInstance(context)
        val tokenFlow = userPreference.tokenFlow
        val apiService = ApiConfig.getApiService(tokenFlow)
        return AuthRepository.getInstance(apiService)
    }

    fun provideReportRepository(context: Context): ReportRepository {
        val userPreference = UserPreference.getInstance(context)
        val tokenFlow = userPreference.tokenFlow
        val apiService = ApiConfig.getApiService(tokenFlow)
        return ReportRepository.getInstance(apiService)
    }

    fun provideScanRepository(context: Context): ScanRepository {
        val userPreference = UserPreference.getInstance(context)
        val tokenFlow = userPreference.tokenFlow
        val apiService = ApiConfig.getApiService(tokenFlow)
        return ScanRepository.getInstance(apiService)
    }

    fun provideProfileRepository(context: Context): ProfileRepository {
        val userPreference = UserPreference.getInstance(context)
        val tokenFlow = userPreference.tokenFlow
        val apiService = ApiConfig.getApiService(tokenFlow)
        return ProfileRepository.getInstance(apiService)
    }

    fun provideArticleRepository(context: Context): ArticleRepository {
        val userPreference = UserPreference.getInstance(context)
        val tokenFlow = userPreference.tokenFlow
        val apiService = ApiConfig.getApiService(tokenFlow)
        return ArticleRepository.getInstance(apiService)
    }

    fun provideForgetPasswordRepository(context: Context): ForgetPasswordRepository {
        val userPreference = UserPreference.getInstance(context)
        val tokenFlow = userPreference.tokenFlow
        val apiService = ApiConfig.getApiService(tokenFlow)
        return ForgetPasswordRepository.getInstance(apiService)
    }
}