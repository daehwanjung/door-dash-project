package com.example.core.common

interface Presenter<View> {
    var view: View?

    fun attachView(view: View) { this.view = view }
    fun detachView() { this.view = null }
}