package com.belajar.submissionawal.ui.detail

import androidx.lifecycle.*
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

    fun getFavoriteEventById(id: String) = repository.getFavoriteEventById(id)

    fun saveFavorite(event: ListEventsItem) {
        viewModelScope.launch {
            val favoriteEvent = FavoriteEvent(
                id = event.id.toString(),
                name = event.name,
                mediaCover = event.mediaCover
            )
            repository.insertFavorite(favoriteEvent)
        }
    }

    fun deleteFavorite(id: String, name: String, mediaCover: String?) {
        viewModelScope.launch {
            val favoriteEvent = FavoriteEvent(id, name, mediaCover)
            repository.deleteFavorite(favoriteEvent)
        }
    }
}
