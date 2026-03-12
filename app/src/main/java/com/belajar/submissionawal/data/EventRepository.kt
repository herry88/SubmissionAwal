package com.belajar.submissionawal.data
import androidx.lifecycle.LiveData
import com.belajar.submissionawal.data.local.entity.FavoriteEvent
import com.belajar.submissionawal.data.local.room.FavoriteEventDao
import com.belajar.submissionawal.data.response.DetailResponse
import com.belajar.submissionawal.data.response.EventResponse
import com.belajar.submissionawal.data.retrofit.ApiService
 
class EventRepository private constructor(
    private val apiService: ApiService,
    private val favoriteEventDao: FavoriteEventDao
) {
    suspend fun getEvents(active: Int): EventResponse {
        return apiService.getEvents(active)
    }
 
    suspend fun searchEvents(query: String): EventResponse {
        return apiService.searchEvents(query = query)
    }
 
    suspend fun getDetailEvent(id: String): DetailResponse {
        return apiService.getDetailEvent(id)
    }
 
    suspend fun getNearestEvent(): EventResponse {
        return apiService.getNearestEvent()
    }
 
    fun getAllFavoriteEvents(): LiveData<List<FavoriteEvent>> {
        return favoriteEventDao.getAllFavoriteEvent()
    }
 
    fun getFavoriteEventById(id: Int): LiveData<FavoriteEvent?> {
        return favoriteEventDao.getFavoriteEventById(id)
    }
 
    suspend fun insertFavorite(event: FavoriteEvent) {
        favoriteEventDao.insert(event)
    }
 
    suspend fun deleteFavorite(event: FavoriteEvent) {
        favoriteEventDao.delete(event)
    }
 
    companion object {
        @Volatile
        private var INSTANCE: EventRepository? = null
        fun getInstance(
            apiService: ApiService,
            favoriteEventDao: FavoriteEventDao
        ): EventRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: EventRepository(apiService, favoriteEventDao)
            }.also { INSTANCE = it }
 
        fun resetInstance() {
            INSTANCE = null
        }
    }
}
