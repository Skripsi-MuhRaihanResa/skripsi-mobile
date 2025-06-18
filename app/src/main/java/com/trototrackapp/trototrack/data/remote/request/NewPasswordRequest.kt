package com.trototrackapp.trototrack.data.remote.request

data class NewPasswordRequest (
    val password: String,
    val confirm_password: String
)