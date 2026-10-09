package com.arflix.tv.data.repository

import com.arflix.tv.data.model.PlaybackEvent
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Process-wide source of truth for playback telemetry.
 *
 * Player implementations publish complete state here. The replay slot is the
 * authoritative current state, so a new transport subscriber cannot miss an
 * event between reading a snapshot and starting collection.
 */
@Singleton
class PlaybackEventPublisher @Inject constructor() {
    private val mutableEvents = MutableSharedFlow<PlaybackEvent>(
        replay = 1,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    ).apply {
        tryEmit(PlaybackEvent.idle())
    }

    val events: SharedFlow<PlaybackEvent> = mutableEvents.asSharedFlow()

    fun snapshot(): PlaybackEvent = (
        mutableEvents.replayCache.lastOrNull() ?: PlaybackEvent.idle()
    ).copy(
        event = "snapshot",
        timestamp = System.currentTimeMillis(),
    )

    fun publish(event: PlaybackEvent) {
        mutableEvents.tryEmit(event)
    }

    fun publishIdle() {
        publish(PlaybackEvent.idle(event = "stop"))
    }
}
