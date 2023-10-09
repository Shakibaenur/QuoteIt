package com.shakibaenur.quoteapplication.core.base

import android.app.Application
import android.content.Context
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import dagger.hilt.android.HiltAndroidApp

/**
 * Created by Shakiba E Nur on 03,March,2023
 */
@HiltAndroidApp
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Logger.addLogAdapter(AndroidLogAdapter())
    }

    companion object {
        fun get(context: Context): BaseApplication = context.applicationContext as BaseApplication
    }
}