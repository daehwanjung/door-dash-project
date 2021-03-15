package com.example.doordashproject

import com.example.core.RestaurantMetadataProvider
import com.example.core.RestaurantsPresenter
import com.example.core.RestaurantsService
import com.example.core.RestaurantsView
import com.example.core.common.Constants
import com.example.core.common.Logger
import com.example.core.common.ThreadManager
import com.example.core.list.ItemClickListener
import com.example.core.list.RestaurantDataView
import com.example.core.model.Restaurant
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class RestaurantsPresenterTest {
    private lateinit var presenter: RestaurantsPresenter
    private lateinit var provider: RestaurantMetadataProvider
    private lateinit var service: RestaurantsService
    private lateinit var threadManager: ThreadManager
    private lateinit var logger: Logger
    private lateinit var coroutineDispatcher: TestCoroutineDispatcher
    private lateinit var view: RestaurantsView

    private val latitude = Constants.LATITUDE
    private val longitude = Constants.LONGITUDE
    private val limit = Constants.LIMIT

    private val restaurantA = Restaurant(1,"A", "a", null, Restaurant.Status.Available(5, 10))
    private val restaurantB = Restaurant(2,"B", "b", null, Restaurant.Status.Unavailable())
    private val restaurants = listOf(restaurantA, restaurantB)

    private class TestThreadManager : ThreadManager {
        override fun runOnMainThread(task: () -> Unit) {
            task()
        }
    }

    @Before
    fun setup() {
        coroutineDispatcher = TestCoroutineDispatcher()
        logger = Mockito.mock(Logger::class.java)
        threadManager = TestThreadManager()
        service = Mockito.mock(RestaurantsService::class.java)
        provider = Mockito.mock(RestaurantMetadataProvider::class.java)
        presenter = RestaurantsPresenter(
            provider,
            service,
            threadManager,
            logger,
            coroutineDispatcher
        )
        view = Mockito.mock(RestaurantsView::class.java)
        presenter.attachView(view)
    }

    @After
    fun clean() {
        presenter.detachView()
    }

    @Test
    fun testLoadResults() {
        Mockito.`when`(service.restaurants(latitude, longitude, 0, limit))
            .thenReturn(restaurants)

        presenter.loadResults()
        coroutineDispatcher.advanceUntilIdle()

        verify(service, times(1)).restaurants(latitude, longitude, 0, limit)
        verify(view, times(0)).handleResultsEmpty()
        verify(view, times(0)).handleError()
        verify(view, times(1)).updateResults()
    }

    @Test
    fun testLoadResultsEmpty() {
        Mockito.`when`(service.restaurants(latitude, longitude, 0, limit))
            .thenReturn(listOf())

        presenter.loadResults()
        coroutineDispatcher.advanceUntilIdle()

        verify(service, times(1)).restaurants(latitude, longitude, 0, limit)
        verify(view, times(1)).handleResultsEmpty()
        verify(view, times(0)).handleError()
        verify(view, times(0)).updateResults()
    }

    @Test
    fun testLoadResultsError() {
        Mockito.`when`(service.restaurants(latitude, longitude, 0, limit))
            .thenAnswer { throw RestaurantsService.NetworkException() }

        presenter.loadResults()
        coroutineDispatcher.advanceUntilIdle()

        verify(service, times(1)).restaurants(latitude, longitude, 0, limit)
        verify(view, times(0)).handleResultsEmpty()
        verify(view, times(1)).handleError()
        verify(view, times(0)).updateResults()
    }

    @Test
    fun testLoadMoreResults() {
        Mockito.`when`(service.restaurants(latitude, longitude, 0, limit))
            .thenReturn(listOf(restaurantA))
        Mockito.`when`(service.restaurants(latitude, longitude, 1, limit))
            .thenReturn(listOf(restaurantB))

        presenter.loadResults()
        coroutineDispatcher.advanceUntilIdle()
        presenter.loadMoreResults()
        coroutineDispatcher.advanceUntilIdle()

        verify(service, times(1)).restaurants(latitude, longitude, 0, limit)
        verify(service, times(1)).restaurants(latitude, longitude, 1, limit)
        verify(view, times(0)).handleMoreResultsEmpty()
        verify(view, times(0)).handleError()
        verify(view, times(1)).updateMoreResults()
    }

    @Test
    fun testLoadMoreResultsEmpty() {
        Mockito.`when`(service.restaurants(latitude, longitude, 0, limit))
            .thenReturn(listOf(restaurantA))
        Mockito.`when`(service.restaurants(latitude, longitude, 1, limit))
            .thenReturn(listOf())

        presenter.loadResults()
        coroutineDispatcher.advanceUntilIdle()
        presenter.loadMoreResults()
        coroutineDispatcher.advanceUntilIdle()

        verify(service, times(1)).restaurants(latitude, longitude, 0, limit)
        verify(service, times(1)).restaurants(latitude, longitude, 1, limit)
        verify(view, times(1)).handleMoreResultsEmpty()
        verify(view, times(0)).handleError()
        verify(view, times(0)).updateMoreResults()
    }

    @Test
    fun testLoadMoreResultsError() {
        Mockito.`when`(service.restaurants(latitude, longitude, 0, limit))
            .thenReturn(listOf(restaurantA))
        Mockito.`when`(service.restaurants(latitude, longitude, 1, limit))
            .thenAnswer { throw RestaurantsService.NetworkException() }

        presenter.loadResults()
        coroutineDispatcher.advanceUntilIdle()
        presenter.loadMoreResults()
        coroutineDispatcher.advanceUntilIdle()

        verify(service, times(1)).restaurants(latitude, longitude, 0, limit)
        verify(service, times(1)).restaurants(latitude, longitude, 1, limit)
        verify(view, times(0)).handleMoreResultsEmpty()
        verify(view, times(1)).handleError()
        verify(view, times(0)).updateMoreResults()
    }

    @Test
    fun testLoadResultsBindData() {
        Mockito.`when`(service.restaurants(latitude, longitude, 0, limit))
            .thenReturn(listOf(restaurantA))
        Mockito.`when`(service.restaurants(latitude, longitude, 1, limit))
            .thenReturn(listOf(restaurantB))

        presenter.loadResults()
        coroutineDispatcher.advanceUntilIdle()
        presenter.loadMoreResults()
        coroutineDispatcher.advanceUntilIdle()

        val testDataView = object : RestaurantDataView {
            var actual: Restaurant? = null

            override fun bindData(restaurant: Restaurant, listener: ItemClickListener<Restaurant>) {
                actual = restaurant
                listener.onClick(restaurant)
            }
        }

        presenter.bindData(testDataView, 0)
        assertEquals(restaurantA, testDataView.actual)
        verify(provider, times(1)).fetchMetadata(restaurantA.id)
        verify(view, times(1)).showDetails(restaurantA.id)

        presenter.bindData(testDataView, 1)
        assertEquals(restaurantB, testDataView.actual)
        verify(provider, times(1)).fetchMetadata(restaurantB.id)
        verify(view, times(1)).showDetails(restaurantB.id)
    }
}