package com.trototrackapp.trototrack.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetReportsUserResponse(

	@field:SerializedName("pagination")
	val pagination: PaginationReportUser? = null,

	@field:SerializedName("data")
	val data: List<DataReportUser?>? = null,

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class DataReportUser(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("like")
	val like: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("location")
	val location: String? = null,

	@field:SerializedName("status_damage")
	val statusDamage: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class PaginationReportUser(

	@field:SerializedName("last_page")
	val lastPage: Int? = null,

	@field:SerializedName("limit")
	val limit: Int? = null,

	@field:SerializedName("current_page")
	val currentPage: Int? = null
)
