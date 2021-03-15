package com.example.core

import com.example.core.model.RestaurantMetadata

interface RestaurantMetadataProvider {
    fun fetchMetadata(id: Int)
    suspend fun metadata(id: Int): RestaurantMetadata
}