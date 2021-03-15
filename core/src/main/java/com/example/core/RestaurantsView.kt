package com.example.core

interface RestaurantsView {
    fun updateResults()
    fun updateMoreResults()
    fun handleResultsEmpty()
    fun handleMoreResultsEmpty()
    fun handleError()
    fun showDetails(id: Int)
}