package com.example.data.model

import com.example.core.model.RestaurantMetadata
import com.example.core.model.Transformable
import com.squareup.moshi.Json

data class DoorDashRestaurantMetadata(
    @field:Json(name = "name") val name: String,
    @field:Json(name = "phone_number") val phone_number: String,
    @field:Json(name = "address") val address: DoorDashAddress,
    @field:Json(name = "average_rating") val average_rating: Double
) : Transformable<RestaurantMetadata> {
    data class DoorDashAddress(
        @field:Json(name = "shortname") val shortname: String
    ) : Transformable<String> {
        override fun transform(): String {
            return shortname
        }
    }

    override fun transform(): RestaurantMetadata {
        return RestaurantMetadata(
            name,
            phone_number,
            address.transform(),
            average_rating
        )
    }
}