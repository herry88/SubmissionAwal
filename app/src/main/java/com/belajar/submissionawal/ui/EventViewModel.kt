package com.belajar.submissionawal.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belajar.submissionawal.data.EventRepository
import com.belajar.submissionawal.data.response.ListEventsItem
import kotlinx.coroutines.launch

class EventViewModel(private val repository: EventRepository) : ViewModel() {

    private val _upcomingEvents = MutableLiveData<List<ListEventsItem>>()
    val upcomingEvents: LiveData<List<ListEventsItem>> = _upcomingEvents

    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvents

    private val _searchResults = MutableLiveData<List<ListEventsItem>>()
    val searchResults: LiveData<List<ListEventsItem>> = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

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
        viewModelScope.launch {
            try {
                val response = repository.searchEvents(query)
                _searchResults.value = response.listEvents ?: emptyList()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun fetchEvents(active: Int, liveData: MutableLiveData<List<ListEventsItem>>) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getEvents(active)
                liveData.value = response.listEvents ?: emptyList()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
