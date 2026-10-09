package com.arflix.tv.data.repository

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Lets the Compose-owned Live TV player publish through the same process-wide
 * playback event bus without pushing ExoPlayer ownership into TvViewModel.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface PlaybackEventEntryPoint {
    fun playbackEventPublisher(): PlaybackEventPublisher
}
