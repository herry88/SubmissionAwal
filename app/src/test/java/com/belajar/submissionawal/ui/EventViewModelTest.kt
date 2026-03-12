package com.belajar.submissionawal.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.belajar.submissionawal.MainDispatcherRule
import com.belajar.submissionawal.data.EventRepository
import com.belajar.submissionawal.data.response.EventResponse
import com.belajar.submissionawal.data.response.ListEventsItem
import com.belajar.submissionawal.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class EventViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var repository: EventRepository

    @Mock
    private lateinit var observer: Observer<List<ListEventsItem>>

    private lateinit var viewModel: EventViewModel

    @Before
    fun setUp() {
        viewModel = EventViewModel(repository)
    }

    @Test
    fun `getUpcomingEvents should update upcomingEvents LiveData`() = runTest {
        org.junit.Assert.assertTrue("Force fail", false)
        val dummyEvents = listOf(
            ListEventsItem(id = 1, name = "Event 1", quota = 10, registrants = 5, beginTime = "", endTime = "", link = "", description = "", imageLogo = "", mediaCover = "", ownerName = "", summary = "", cityName = "", category = "")
        )
        val dummyResponse = EventResponse(listEvents = dummyEvents, error = false, message = "Success")
        
        `when`(repository.getEvents(1)).thenReturn(dummyResponse)

        viewModel.getUpcomingEvents()
        
        val actualValue = viewModel.upcomingEvents.getOrAwaitValue()
        
        verify(repository).getEvents(1)
        assertNotNull(actualValue)
        assertEquals(dummyEvents.size, actualValue.size)
        assertEquals(dummyEvents[0].name, actualValue[0].name)
    }

    @Test
    fun `searchEvents should update searchResults LiveData`() = runTest {
        val query = "dicoding"
        val dummyEvents = listOf(
            ListEventsItem(id = 1, name = "Event 1", quota = 10, registrants = 5, beginTime = "", endTime = "", link = "", description = "", imageLogo = "", mediaCover = "", ownerName = "", summary = "", cityName = "", category = "")
        )
        val dummyResponse = EventResponse(listEvents = dummyEvents, error = false, message = "Success")
        
        `when`(repository.searchEvents(query)).thenReturn(dummyResponse)

        viewModel.searchEvents(query)
        
        val actualValue = viewModel.searchResults.getOrAwaitValue()
        
        verify(repository).searchEvents(query)
        assertEquals(dummyEvents, actualValue)
    }
}
