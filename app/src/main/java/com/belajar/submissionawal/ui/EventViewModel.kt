package com.belajar.submissionawal.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.belajar.submissionawal.data.response.EventResponse
import com.belajar.submissionawal.data.response.ListEventsItem
import com.belajar.submissionawal.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventViewModel : ViewModel() {

    private val _upcomingEvents = MutableLiveData<List<ListEventsItem>>()
    val upcomingEvents: LiveData<List<ListEventsItem>> = _upcomingEvents

    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvents

    private val _searchResults = MutableLiveData<List<ListEventsItem>>()
    val searchResults: LiveData<List<ListEventsItem>> = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getUpcomingEvents() {
        if (_upcomingEvents.value != null) return
        fetchEvents(1, _upcomingEvents)
    }

    fun getFinishedEvents() {
        if (_finishedEvents.value != null) return
        fetchEvents(0, _finishedEvents)
    }

    fun getHomeUpcomingEvents() {
        if (_upcomingEvents.value != null) return
        fetchEvents(1, _upcomingEvents)
    }

    fun getHomeFinishedEvents() {
        if (_finishedEvents.value != null) return
        fetchEvents(0, _finishedEvents)
    }

    fun searchEvents(query: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().searchEvents(-1, query)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _searchResults.value = response.body()?.listEvents
                } else {
                    _errorMessage.value = "Error: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Failure: ${t.message}"
            }
        })
    }

    private fun fetchEvents(active: Int, liveData: MutableLiveData<List<ListEventsItem>>) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getEvents(active)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    liveData.value = response.body()?.listEvents
                } else {
                    _errorMessage.value = "Error: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Failure: ${t.message}"
            }
        })
    }
}
