package com.example.data

import com.example.core.RestaurantsService
import com.example.core.model.Restaurant
import com.example.core.model.RestaurantMetadata
import com.example.data.model.DoorDashRestaurantMetadata
import com.example.data.model.DoorDashRestaurantsList
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.TimeUnit

class DoorDashRestaurantsService : RestaurantsService {
    interface Interface {
        @GET("v1/store_feed")
        fun restaurants(
            @Query("lat") latitude: Double,
            @Query("lng") longitude: Double,
            @Query("offset") offset: Int,
            @Query("limit") limit: Int
        ): Call<DoorDashRestaurantsList>

        @GET("v2/restaurant/{id}")
        fun metadata(
            @Path("id") id: Int
        ): Call<DoorDashRestaurantMetadata>
    }

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val endpoint = "https://api.doordash.com/"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(endpoint)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val service = retrofit.create(Interface::class.java)

    private var currentRestaurantsCall: Call<DoorDashRestaurantsList>? = null
    private var currentMetadataCall: Call<DoorDashRestaurantMetadata>? = null

    override fun restaurants(
        latitude: Double,
        longitude: Double,
        offset: Int,
        limit: Int
    ): List<Restaurant> {
        return try {
            currentRestaurantsCall?.cancel()
            currentMetadataCall?.cancel()
            val call = service.restaurants(latitude, longitude, offset, limit)
            currentRestaurantsCall = call
            call.execute().body()!!.transform()
        } catch (e: IOException) {
            throw RestaurantsService.CancellationException()
        } catch (e: Exception) {
            throw RestaurantsService.NetworkException()
        }
    }

    override fun metadata(id: Int): RestaurantMetadata {
        return try {
            currentRestaurantsCall?.cancel()
            currentMetadataCall?.cancel()
            val call = service.metadata(id)
            currentMetadataCall = call
            call.execute().body()!!.transform()
        } catch (e: IOException) {
            throw RestaurantsService.CancellationException()
        } catch (e: Exception) {
            throw RestaurantsService.NetworkException()
        }
    }
}