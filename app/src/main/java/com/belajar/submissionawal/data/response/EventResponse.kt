package com.belajar.submissionawal.data.response

import com.google.gson.annotations.SerializedName

data class EventResponse(

	@field:SerializedName("listEvents")
	val listEvents: List<ListEventsItem>? = emptyList(),

	@field:SerializedName("error")
	val error: Boolean = false,

	@field:SerializedName("message")
	val message: String? = null
)

data class ListEventsItem(

	@field:SerializedName("quota")
	val quota: Int = 0,

	@field:SerializedName("registrants")

	val registrants: Int = 0,

	val registrants: Int,


	@field:SerializedName("beginTime")
	val beginTime: String? = null,

	@field:SerializedName("endTime")
	val endTime: String? = null,

	@field:SerializedName("link")
	val link: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("id")
	val id: Int = 0,

	@field:SerializedName("imageLogo")
	val imageLogo: String? = null,

	@field:SerializedName("mediaCover")
	val mediaCover: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("ownerName")
	val ownerName: String? = null,

	@field:SerializedName("summary")
	val summary: String? = null,

	@field:SerializedName("cityName")
	val cityName: String? = null,

	@field:SerializedName("category")
	val category: String? = null
)
