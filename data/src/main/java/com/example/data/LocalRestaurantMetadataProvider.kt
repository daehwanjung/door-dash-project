package com.example.data

import com.example.core.RestaurantMetadataProvider
import com.example.core.RestaurantsService
import com.example.core.model.RestaurantMetadata
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class LocalRestaurantMetadataProvider(
    private val service: RestaurantsService,
    override val coroutineContext: CoroutineContext = CoroutineName("metadata")
) : RestaurantMetadataProvider, CoroutineScope {
    class MissingMetadataException : Exception()

    private val cache = mutableMapOf<Int, Deferred<RestaurantMetadata?>>()

    override fun fetchMetadata(id: Int) {
        if (cache[id] == null) {
            cache[id] = async {
                try {
                    service.metadata(id)
                } catch (e: RestaurantsService.CancellationException) {
                    null
                } catch (e: RestaurantsService.NetworkException) {
                    null
                }
            }
        }
    }

    override suspend fun metadata(id: Int): RestaurantMetadata {
        if (cache[id] == null) {
            fetchMetadata(id)
        }
        return cache[id]?.await() ?: throw MissingMetadataException()
    }
}