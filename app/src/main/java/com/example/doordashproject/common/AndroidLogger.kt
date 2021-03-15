package com.example.doordashproject.common

import android.util.Log
import com.example.core.common.Logger

class AndroidLogger : Logger {
    override fun log(tag: String, message: String) {
        Log.d(tag, message)
    }
}