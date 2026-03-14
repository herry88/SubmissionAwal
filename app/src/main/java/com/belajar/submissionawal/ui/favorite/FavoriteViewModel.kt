package com.belajar.submissionawal.ui.favorite

import androidx.lifecycle.ViewModel
import com.belajar.submissionawal.data.EventRepository

class FavoriteViewModel(private val eventRepository: EventRepository) : ViewModel() {
    fun getFavoriteEvents() = eventRepository.getAllFavoriteEvents()
}
