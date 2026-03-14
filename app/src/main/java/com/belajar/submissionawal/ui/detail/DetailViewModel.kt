package com.belajar.submissionawal.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belajar.submissionawal.data.EventRepository
import com.belajar.submissionawal.data.local.entity.FavoriteEvent
import com.belajar.submissionawal.data.response.ListEventsItem
import kotlinx.coroutines.launch
 
class DetailViewModel(private val repository: EventRepository) : ViewModel() {
 
    private val _eventDetail = MutableLiveData<ListEventsItem?>()
    val eventDetail: LiveData<ListEventsItem?> = _eventDetail
 
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
 
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
 
    fun getDetailEvent(id: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getDetailEvent(id)
                _eventDetail.value = response.event
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getFavoriteEventById(id: Int): LiveData<FavoriteEvent?> {
        return repository.getFavoriteEventById(id)
    }

    fun insertFavorite(event: FavoriteEvent) {
        viewModelScope.launch {
            repository.insertFavorite(event)
        }
    }

    fun deleteFavorite(event: FavoriteEvent) {
        viewModelScope.launch {
            repository.deleteFavorite(event)
        }
    }
}
