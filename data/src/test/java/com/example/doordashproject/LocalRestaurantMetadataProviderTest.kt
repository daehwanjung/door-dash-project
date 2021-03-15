package com.example.doordashproject

import com.example.core.RestaurantMetadataProvider
import com.example.core.RestaurantsService
import com.example.core.model.RestaurantMetadata
import com.example.data.LocalRestaurantMetadataProvider
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class LocalRestaurantMetadataProviderTest {
    private lateinit var provider: RestaurantMetadataProvider
    private lateinit var service: RestaurantsService
    private lateinit var coroutineDispatcher: TestCoroutineDispatcher

    private val id = 1
    private val metadata = RestaurantMetadata(
        "name",
        "number",
        "address",
        2.5
    )

    @Before
    fun setup() {
        coroutineDispatcher = TestCoroutineDispatcher()
        service = Mockito.mock(RestaurantsService::class.java)
        provider = LocalRestaurantMetadataProvider(service, coroutineDispatcher)
    }

    @Test
    fun testCaching() = runBlockingTest {
        Mockito.`when`(service.metadata(id)).thenReturn(metadata)

        provider.fetchMetadata(id)
        coroutineDispatcher.advanceUntilIdle()
        provider.fetchMetadata(id)
        coroutineDispatcher.advanceUntilIdle()

        verify(service, times(1)).metadata(id)

        assertEquals(metadata, provider.metadata(id))
    }
}