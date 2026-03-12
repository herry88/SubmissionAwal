package com.belajar.submissionawal.di
 
import android.content.Context
import com.belajar.submissionawal.data.EventRepository
import com.belajar.submissionawal.data.local.room.EventDatabase
import com.belajar.submissionawal.data.retrofit.ApiConfig
 
object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getDatabase(context)
        val dao = database.favoriteEventDao()
        return EventRepository.getInstance(apiService, dao)
    }
}
