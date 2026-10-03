package com.arflix.tv.server

import android.util.Log
import com.arflix.tv.data.repository.PlaybackEventPublisher
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.send
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Read-only LAN WebSocket for ARVIO playback telemetry.
 *
 * Endpoint: ws://<device-ip>:8765/playback
 *
 * Every connection receives the current snapshot immediately, then complete
 * playback states as they are published. Incoming application messages are
 * intentionally ignored; playback control is outside this interface.
 */
@Singleton
class PlaybackWebSocketServer @Inject constructor(
    private val publisher: PlaybackEventPublisher,
) {
    companion object {
        private const val TAG = "PlaybackWebSocket"
        const val PORT = 8765
    }

    @Volatile
    private var server: ApplicationEngine? = null

    @Synchronized
    fun start() {
        if (server != null) return

        val created = embeddedServer(
            factory = CIO,
            host = "0.0.0.0",
            port = PORT,
        ) {
            install(WebSockets)
            routing {
                webSocket("/playback") {
                    send(Frame.Text(publisher.snapshot().toJson()))

                    val eventJob = launch {
                        publisher.events.collect { event ->
                            send(Frame.Text(event.toJson()))
                        }
                    }

                    try {
                        for (ignored in incoming) {
                            // Read-only endpoint: consume client frames until disconnect.
                        }
                    } finally {
                        eventJob.cancelAndJoin()
                    }
                }
            }
        }

        try {
            created.start(wait = false)
            server = created
            Log.i(TAG, "Playback WebSocket listening on 0.0.0.0:$PORT/playback")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            runCatching { created.stop(0, 0) }
            Log.e(TAG, "Unable to start playback WebSocket on port $PORT", e)
        }
    }

    @Synchronized
    fun stop() {
        server?.stop(500, 1_000)
        server = null
    }
}
