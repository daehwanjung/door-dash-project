package com.example.doordashproject.list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.core.image.ImageLoader
import com.example.core.list.DataBinder
import com.example.core.list.RestaurantDataView
import com.example.doordashproject.R

class RestaurantsAdapter(
    private val dataBinder: DataBinder<RestaurantDataView>,
    private val imageLoader: ImageLoader<ImageView>
) : RecyclerView.Adapter<RestaurantViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        return RestaurantViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_restaurant, parent, false),
            imageLoader
        )
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        dataBinder.bindData(holder, position)
    }

    override fun getItemCount(): Int {
        return dataBinder.itemCount
    }
}