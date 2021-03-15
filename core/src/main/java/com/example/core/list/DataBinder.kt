package com.example.core.list

interface DataBinder<DataView> {
    val itemCount: Int

    fun bindData(dataView: DataView, position: Int)
}