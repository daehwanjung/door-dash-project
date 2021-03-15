package com.example.core

import com.example.core.common.Constants
import com.example.core.common.Logger
import com.example.core.common.Presenter
import com.example.core.common.ThreadManager
import com.example.core.list.DataBinder
import com.example.core.list.ItemClickListener
import com.example.core.list.RestaurantDataView
import com.example.core.model.Restaurant
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.math.min

class RestaurantsPresenter(
    private val metadataProvider: RestaurantMetadataProvider,
    private val service: RestaurantsService,
    private val threadManager: ThreadManager,
    private val logger: Logger,
    override val coroutineContext: CoroutineContext = CoroutineName("restaurants")
) : Presenter<RestaurantsView>,
    DataBinder<RestaurantDataView>,
    ItemClickListener<Restaurant>,
    CoroutineScope {

    override var view: RestaurantsView? = null

    override val itemCount: Int
        get() = data.size

    private val data = mutableListOf<Restaurant>()

    private var currentJob: Job? = null
        set(value) {
            field?.cancel()
            field = value
        }

    private var currentOffset = 0
    private var searching = false

    override fun bindData(dataView: RestaurantDataView, position: Int) {
        dataView.bindData(data[position], this)
    }

    override fun onClick(item: Restaurant) {
        metadataProvider.fetchMetadata(item.id)
        view?.showDetails(item.id)
    }

    fun loadResults() {
        currentJob = launch {
            try {
                currentOffset = 0
                val results = search()
                if (results.isEmpty()) {
                    threadManager.runOnMainThread { view?.handleResultsEmpty() }
                } else {
                    data.clear()
                    data.addAll(results)
                    threadManager.runOnMainThread { view?.updateResults() }
                }
            } catch (e: RestaurantsService.CancellationException) {
                logger.log("RestaurantsPresenter", "Job cancelled.")
            } catch (e: RestaurantsService.NetworkException) {
                threadManager.runOnMainThread { view?.handleError() }
            }
        }
    }

    fun loadMoreResults() {
        if (!searching) {
            currentJob = launch {
                try {
                    val results = search()
                    if (results.isEmpty()) {
                        threadManager.runOnMainThread { view?.handleMoreResultsEmpty() }
                    } else {
                        data.addAll(results)
                        threadManager.runOnMainThread { view?.updateMoreResults() }
                    }
                } catch (e: RestaurantsService.CancellationException) {
                    logger.log("RestaurantsPresenter", "Job cancelled.")
                } catch (e: RestaurantsService.NetworkException) {
                    threadManager.runOnMainThread { view?.handleError() }
                }
            }
        }
    }

    private fun search(): List<Restaurant> {
        searching = true
        val results = service.restaurants(
            Constants.LATITUDE,
            Constants.LONGITUDE,
            currentOffset,
            Constants.LIMIT
        )
        currentOffset += min(results.size, Constants.LIMIT)
        searching = false
        return results
    }
}