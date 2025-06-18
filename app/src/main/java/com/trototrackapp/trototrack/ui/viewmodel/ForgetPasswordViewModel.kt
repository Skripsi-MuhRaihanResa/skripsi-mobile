package com.trototrackapp.trototrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.trototrackapp.trototrack.data.repository.ForgetPasswordRepository

class ForgetPasswordViewModel(private val forgetPasswordRepository: ForgetPasswordRepository) : ViewModel() {

    fun sendOtp(email: String) = forgetPasswordRepository.sendOtp(email)

    fun verifyOtp(email: String, otp: String) = forgetPasswordRepository.verifyOtp(email, otp)

    fun newPassword(password: String, passwordConfirmation: String) = forgetPasswordRepository.newPassword(password, passwordConfirmation)

}