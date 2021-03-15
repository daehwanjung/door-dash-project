package com.example.doordashproject.common

import android.os.Handler
import android.os.Looper
import com.example.core.common.ThreadManager

class AndroidThreadManager : ThreadManager {
    private val mainThreadHandler = Handler(Looper.getMainLooper())

    override fun runOnMainThread(task: () -> Unit) {
        mainThreadHandler.post(task)
    }
}