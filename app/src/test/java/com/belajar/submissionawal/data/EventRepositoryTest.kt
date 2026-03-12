package com.belajar.submissionawal.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.belajar.submissionawal.MainDispatcherRule
import com.belajar.submissionawal.data.local.entity.FavoriteEvent
import com.belajar.submissionawal.data.local.room.FavoriteEventDao
import com.belajar.submissionawal.data.response.DetailResponse
import com.belajar.submissionawal.data.retrofit.ApiService
import com.belajar.submissionawal.data.response.EventResponse
import com.belajar.submissionawal.data.response.ListEventsItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class EventRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var favoriteEventDao: FavoriteEventDao

    private lateinit var repository: EventRepository

    @Before
    fun setUp() {
        EventRepository.resetInstance()
        repository = EventRepository.getInstance(apiService, favoriteEventDao)
    }

    @Test
    fun `getEvents should fetch data from ApiService`() = runTest {
        val dummyResponse = EventResponse(listEvents = emptyList(), error = false, message = "Success")
        `when`(apiService.getEvents(1)).thenReturn(dummyResponse)

        val actualResponse = repository.getEvents(1)

        verify(apiService).getEvents(1)
        assertNotNull(actualResponse)
        assertEquals(dummyResponse, actualResponse)
    }

    @Test
    fun `searchEvents should fetch data from ApiService`() = runTest {
        val query = "dicoding"
        val dummyResponse = EventResponse(listEvents = emptyList(), error = false, message = "Success")
        `when`(apiService.searchEvents(query = query)).thenReturn(dummyResponse)

        val actualResponse = repository.searchEvents(query)

        verify(apiService).searchEvents(query = query)
        assertEquals(dummyResponse, actualResponse)
    }

    @Test
    fun `getDetailEvent should fetch data from ApiService`() = runTest {
        val id = "123"
        val dummyEvent = ListEventsItem(
            id = 1, name = "Event 1", quota = 10, registrants = 5, beginTime = "", endTime = "", 
            link = "", description = "", imageLogo = "", mediaCover = "", ownerName = "", 
            summary = "", cityName = "", category = ""
        )
        val dummyResponse = DetailResponse(error = false, message = "Success", event = dummyEvent)
        `when`(apiService.getDetailEvent(id)).thenReturn(dummyResponse)

        val actualResponse = repository.getDetailEvent(id)

        verify(apiService).getDetailEvent(id)
        assertEquals(dummyResponse, actualResponse)
    }

    @Test
    fun `insertFavorite should call DAO insert`() = runTest {
        val dummyEvent = FavoriteEvent(id = "1", name = "Event 1", mediaCover = "url")
        
        repository.insertFavorite(dummyEvent)

        verify(favoriteEventDao).insert(dummyEvent)
    }

    @Test
    fun `deleteFavorite should call DAO delete`() = runTest {
        val dummyEvent = FavoriteEvent(id = "1", name = "Event 1", mediaCover = "url")

        repository.deleteFavorite(dummyEvent)

        verify(favoriteEventDao).delete(dummyEvent)
    }

    @Test
    fun `getAllFavoriteEvents should return LiveData from DAO`() {
        val dummyLiveData = MutableLiveData<List<FavoriteEvent>>()
        `when`(favoriteEventDao.getAllFavoriteEvent()).thenReturn(dummyLiveData)

        val actualLiveData = repository.getAllFavoriteEvents()

        verify(favoriteEventDao).getAllFavoriteEvent()
        assertEquals(dummyLiveData, actualLiveData)
    }
}
