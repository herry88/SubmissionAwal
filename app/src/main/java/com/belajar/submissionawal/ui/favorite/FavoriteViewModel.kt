package com.belajar.submissionawal.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.belajar.submissionawal.data.EventRepository
import com.belajar.submissionawal.data.local.entity.FavoriteEvent

class FavoriteViewModel(private val repository: EventRepository) : ViewModel() {
    fun getFavoriteEvents(): LiveData<List<FavoriteEvent>> {
        return repository.getAllFavoriteEvents()
    }
}
