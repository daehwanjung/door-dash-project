package com.example.doordashproject.list

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.core.image.ImageLoader
import com.example.core.list.ItemClickListener
import com.example.core.list.RestaurantDataView
import com.example.core.model.Restaurant
import com.example.doordashproject.R

class RestaurantViewHolder(
    view: View,
    private val imageLoader: ImageLoader<ImageView>
) : RecyclerView.ViewHolder(view), RestaurantDataView {
    private val context = view.context
    private val image = view.findViewById<ImageView>(R.id.image)
    private val name = view.findViewById<TextView>(R.id.name)
    private val description = view.findViewById<TextView>(R.id.description)
    private val time = view.findViewById<TextView>(R.id.time)

    override fun bindData(restaurant: Restaurant, listener: ItemClickListener<Restaurant>) {
        imageLoader.loadImage(image, restaurant.imageUrl)
        name.text = restaurant.name
        description.text = restaurant.description
        val status = restaurant.status
        time.text = if (status is Restaurant.Status.Available) {
            context.getString(R.string.asap_time, status.asapMinutesMin)
        } else {
            context.getString(R.string.closed)
        }
        itemView.setOnClickListener {
            listener.onClick(restaurant)
        }
    }
}