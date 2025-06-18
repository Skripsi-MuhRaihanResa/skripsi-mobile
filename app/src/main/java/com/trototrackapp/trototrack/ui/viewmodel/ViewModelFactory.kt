// ViewModelFactory.kt
package com.trototrackapp.trototrack.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.trototrackapp.trototrack.data.repository.ArticleRepository
import com.trototrackapp.trototrack.data.repository.AuthRepository
import com.trototrackapp.trototrack.data.repository.ForgetPasswordRepository
import com.trototrackapp.trototrack.data.repository.ProfileRepository
import com.trototrackapp.trototrack.data.repository.ReportRepository
import com.trototrackapp.trototrack.data.repository.ScanRepository
import com.trototrackapp.trototrack.di.Injection


class ViewModelFactory private constructor(
    private val authRepository: AuthRepository,
    private val reportRepository: ReportRepository,
    private val scanRepository: ScanRepository,
    private val profileRepository: ProfileRepository,
    private val articleRepository: ArticleRepository,
    private val forgetPasswordRepository: ForgetPasswordRepository,
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(ScanViewModel::class.java) -> {
                ScanViewModel(scanRepository) as T
            }
            modelClass.isAssignableFrom(ReportsViewModel::class.java) -> {
                ReportsViewModel(reportRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(profileRepository) as T
            }
            modelClass.isAssignableFrom(ArticlesViewModel::class.java) -> {
                ArticlesViewModel(articleRepository) as T
            }
            modelClass.isAssignableFrom(ForgetPasswordViewModel::class.java) -> {
                ForgetPasswordViewModel(forgetPasswordRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            val authRepository = Injection.provideAuthRepository(context)
            val reportRepository = Injection.provideReportRepository(context)
            val scanRepository = Injection.provideScanRepository(context)
            val profileRepository = Injection.provideProfileRepository(context)
            val articleRepository = Injection.provideArticleRepository(context)
            val forgetPasswordRepository = Injection.provideForgetPasswordRepository(context)
            return INSTANCE ?: synchronized(ViewModelFactory::class.java) {
                INSTANCE ?: ViewModelFactory(authRepository, reportRepository, scanRepository, profileRepository, articleRepository, forgetPasswordRepository)
                    .also { INSTANCE = it }
            }
        }
    }
}
