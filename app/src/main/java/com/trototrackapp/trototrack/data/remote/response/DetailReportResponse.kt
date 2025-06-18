package com.trototrackapp.trototrack.data.remote.response

import com.google.gson.annotations.SerializedName

data class DetailReportResponse(

	@field:SerializedName("data")
	val data: DataDetail? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class DataDetail(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("reason")
	val reason: Any? = null,

	@field:SerializedName("like")
	val like: Int? = null,

	@field:SerializedName("latitude")
	val latitude: Any? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("location")
	val location: String? = null,

	@field:SerializedName("status_damage")
	val statusDamage: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("reference_location")
	val referenceLocation: String? = null,

	@field:SerializedName("longitude")
	val longitude: Any? = null,

	@field:SerializedName("status")
	val status: String? = null
)
