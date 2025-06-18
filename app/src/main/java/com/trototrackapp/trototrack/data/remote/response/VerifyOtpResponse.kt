package com.trototrackapp.trototrack.data.remote.response

import com.google.gson.annotations.SerializedName

data class VerifyOtpResponse(

	@field:SerializedName("data")
	val data: DataVerify? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class DataVerify(

	@field:SerializedName("token")
	val token: String? = null
)
