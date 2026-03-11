package com.belajar.submissionawal.data.response

import com.google.gson.annotations.SerializedName

data class DetailResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("event")
	val event: ListEventsItem
)
