package com.example.core.common

interface ThreadManager {
    fun runOnMainThread(task: () -> Unit)
}