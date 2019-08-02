package com.seventhmoon.jupyterplay.utils

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.util.Log
import com.seventhmoon.jupyterplay.common.MediaSessionConnection
import com.seventhmoon.jupyterplay.media.MusicService
import com.seventhmoon.jupyterplay.viewmodels.MainActivityViewModel
import com.seventhmoon.jupyterplay.viewmodels.MediaItemFragmentViewModel
import com.seventhmoon.jupyterplay.viewmodels.NowPlayingFragmentViewModel

object InjectorUtils {
    private val mTAG = InjectorUtils::class.java.name
    private fun provideMediaSessionConnection(context: Context): MediaSessionConnection {
        Log.d(mTAG, "->provideMediaSessionConnection")
        return MediaSessionConnection.getInstance(context,
            ComponentName(context, MusicService::class.java)
        )
    }

    fun provideMainActivityViewModel(context: Context): MainActivityViewModel.Factory {
        Log.d(mTAG, "->provideMainActivityViewModel")
        val applicationContext = context.applicationContext
        val mediaSessionConnection = provideMediaSessionConnection(applicationContext)
        return MainActivityViewModel.Factory(mediaSessionConnection)
    }

    fun provideMediaItemFragmentViewModel(context: Context, mediaId: String)
            : MediaItemFragmentViewModel.Factory {
        Log.d(mTAG, "->provideMediaItemFragmentViewModel")
        val applicationContext = context.applicationContext
        val mediaSessionConnection = provideMediaSessionConnection(applicationContext)
        return MediaItemFragmentViewModel.Factory(mediaId, mediaSessionConnection)
    }

    fun provideNowPlayingFragmentViewModel(context: Context)
            : NowPlayingFragmentViewModel.Factory {
        Log.d(mTAG, "->provideNowPlayingFragmentViewModel")
        val applicationContext = context.applicationContext
        val mediaSessionConnection = provideMediaSessionConnection(applicationContext)
        return NowPlayingFragmentViewModel.Factory(
            applicationContext as Application, mediaSessionConnection)
    }
}