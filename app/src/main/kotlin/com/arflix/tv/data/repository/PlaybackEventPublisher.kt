package com.arflix.tv.data.repository

import com.arflix.tv.data.model.PlaybackEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Process-wide source of truth for playback telemetry.
 *
 * Player implementations publish complete state here. Network transports consume
 * the state without becoming coupled to player or ExoPlayer internals.
 */
@Singleton
class PlaybackEventPublisher @Inject constructor() {
    private val current = MutableStateFlow(PlaybackEvent.idle())
    private val mutableEvents = MutableSharedFlow<PlaybackEvent>(
        extraBufferCapacity = 64,
    )

    val events: SharedFlow<PlaybackEvent> = mutableEvents.asSharedFlow()

    fun snapshot(): PlaybackEvent = current.value.copy(
        event = "snapshot",
        timestamp = System.currentTimeMillis(),
    )

    fun publish(event: PlaybackEvent) {
        current.value = event
        mutableEvents.tryEmit(event)
    }

    fun publishIdle() {
        publish(PlaybackEvent.idle(event = "stop"))
    }
}
