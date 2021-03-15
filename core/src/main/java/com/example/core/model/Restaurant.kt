package com.example.core.model

data class Restaurant(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String?,
    val status: Status
) {
    sealed class Status(val asapMinutesMin: Int, val asapMinutesMax: Int) {
        class Unavailable : Status(-1, -1)
        class Available(min: Int, max: Int) : Status(min, max)
    }
}