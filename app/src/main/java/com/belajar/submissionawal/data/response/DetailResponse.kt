package com.belajar.submissionawal.data.response

import com.google.gson.annotations.SerializedName

data class DetailResponse(

	@field:SerializedName("error")
	val error: Boolean = false,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("event")
	val event: ListEventsItem? = null
)
