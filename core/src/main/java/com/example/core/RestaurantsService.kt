package com.example.core

import com.example.core.model.Restaurant
import com.example.core.model.RestaurantMetadata
import java.lang.Exception

interface RestaurantsService {
    class CancellationException: Exception()
    class NetworkException: Exception()

    fun restaurants(
        latitude: Double,
        longitude: Double,
        offset: Int,
        limit: Int
    ): List<Restaurant>

    fun metadata(id: Int): RestaurantMetadata
}