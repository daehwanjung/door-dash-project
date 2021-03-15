package com.example.doordashproject.di

import android.content.Context
import android.widget.ImageView
import com.example.core.RestaurantMetadataProvider
import com.example.core.RestaurantsPresenter
import com.example.core.RestaurantsService
import com.example.core.common.Logger
import com.example.core.common.ThreadManager
import com.example.core.image.ImageLoader
import com.example.data.DoorDashRestaurantsService
import com.example.data.LocalRestaurantMetadataProvider
import com.example.doordashproject.common.AndroidLogger
import com.example.doordashproject.common.AndroidThreadManager
import com.example.doordashproject.image.GlideImageLoader
import org.koin.dsl.module

object RestaurantsComponent {
    val module = module {
        factory { RestaurantsPresenter(get(), get(), get(), get()) }
        single<RestaurantMetadataProvider> { LocalRestaurantMetadataProvider(get()) }
        single<RestaurantsService> { DoorDashRestaurantsService() }
        single<ThreadManager> { AndroidThreadManager() }
        single<Logger> { AndroidLogger() }
        single<ImageLoader<ImageView>> { (context: Context) -> GlideImageLoader(context) }
    }
}