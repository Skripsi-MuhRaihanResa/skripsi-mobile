package com.trototrackapp.trototrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.trototrackapp.trototrack.data.repository.ProfileRepository

class ProfileViewModel(private val profileRepository: ProfileRepository) : ViewModel() {

    fun updateProfile(name: String, username: String) =
        profileRepository.updateProfile(name, username)

    fun getUserProfile() =
        profileRepository.getUserProfile()
}