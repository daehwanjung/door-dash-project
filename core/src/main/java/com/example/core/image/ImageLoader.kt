package com.example.core.image

interface ImageLoader<View> {
    fun loadImage(view: View, url: String?)
}