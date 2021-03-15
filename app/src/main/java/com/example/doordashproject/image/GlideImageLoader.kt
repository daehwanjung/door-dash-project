package com.example.doordashproject.image

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.core.image.ImageLoader

class GlideImageLoader(private val context: Context) : ImageLoader<ImageView> {
    override fun loadImage(view: ImageView, url: String?) {
        Glide.with(context)
            .load(url)
            .into(view)
    }
}