package com.example.core.list

import com.example.core.model.Restaurant

interface RestaurantDataView {
    fun bindData(restaurant: Restaurant, listener: ItemClickListener<Restaurant>)
}