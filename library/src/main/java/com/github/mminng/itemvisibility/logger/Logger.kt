package com.github.mminng.itemvisibility.logger

import android.util.Log
import com.github.mminng.itemvisibility.BuildConfig

internal fun loggerD(msg: String) {
    if (BuildConfig.DEBUG) {
        Log.d("ItemVisibilityHelper", msg)
    }
}

internal fun loggerI(msg: String) {
    if (BuildConfig.DEBUG) {
        Log.i("ItemVisibilityHelper", msg)
    }
}