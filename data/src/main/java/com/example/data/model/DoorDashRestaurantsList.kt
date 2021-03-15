package com.example.data.model

import com.example.core.model.Restaurant
import com.example.core.model.Transformable
import com.squareup.moshi.Json

data class DoorDashRestaurantsList(
    @field:Json(name = "stores") val stores: List<DoorDashRestaurant>
) : Transformable<List<Restaurant>> {
    data class DoorDashRestaurant(
        @field:Json(name = "id") val id: Int,
        @field:Json(name = "name") val name: String,
        @field:Json(name = "description") val description: String,
        @field:Json(name = "cover_img_url") val cover_img_url: String?,
        @field:Json(name = "status") val status: DoorDashStatus
    ) : Transformable<Restaurant> {
        data class DoorDashStatus(
            @field:Json(name = "asap_available") val asap_available: Boolean,
            @field:Json(name = "asap_minutes_range") val asap_minutes_range: List<Int>
        ) : Transformable<Restaurant.Status> {
            override fun transform(): Restaurant.Status {
                return if (asap_available) {
                    Restaurant.Status.Available(asap_minutes_range[0], asap_minutes_range[1])
                } else {
                    Restaurant.Status.Unavailable()
                }
            }
        }

        override fun transform(): Restaurant {
            return Restaurant(id, name, description, cover_img_url, status.transform())
        }
    }

    override fun transform(): List<Restaurant> {
        return stores.map { it.transform() }
    }
}