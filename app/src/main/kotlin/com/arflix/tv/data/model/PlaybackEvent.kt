package com.arflix.tv.data.model

import org.json.JSONObject

/**
 * Complete playback state published over the local ARVIO telemetry interface.
 *
 * Messages are deliberately snapshots rather than deltas so a client can recover
 * state after reconnecting without replaying earlier events.
 */
data class PlaybackEvent(
    val event: String,
    val state: String,
    val mediaType: String? = null,
    val tmdbId: Int? = null,
    val title: String? = null,
    val episodeTitle: String? = null,
    val season: Int? = null,
    val episode: Int? = null,
    val positionMs: Long? = null,
    val durationMs: Long? = null,
    val progressPercent: Int? = null,
    val poster: String? = null,
    val backdrop: String? = null,
    val source: String? = null,
    val addonId: String? = null,
    val channelId: String? = null,
    val channel: String? = null,
    val channelNumber: String? = null,
    val channelGroup: String? = null,
    val programme: String? = null,
    val programmeDescription: String? = null,
    val programmeArtwork: String? = null,
    val programmeCategory: String? = null,
    val programmeStart: Long? = null,
    val programmeEnd: Long? = null,
    val programmeProgressPercent: Int? = null,
    val catchupAvailable: Boolean? = null,
    val nextProgramme: String? = null,
    val nextProgrammeStart: Long? = null,
    val nextProgrammeEnd: Long? = null,
    val logo: String? = null,
    val isLive: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
) {
    fun toJson(): String = JSONObject().apply {
        put("event", event)
        put("state", state)
        put("is_live", isLive)
        put("timestamp", timestamp)

        mediaType?.let { put("media_type", it) }
        tmdbId?.let { put("tmdb_id", it) }
        title?.let { put("title", it) }
        episodeTitle?.let { put("episode_title", it) }
        season?.let { put("season", it) }
        episode?.let { put("episode", it) }
        positionMs?.let { put("position_ms", it) }
        durationMs?.let { put("duration_ms", it) }
        progressPercent?.let { put("progress_percent", it) }
        poster?.let { put("poster", it) }
        backdrop?.let { put("backdrop", it) }
        source?.let { put("source", it) }
        addonId?.let { put("addon_id", it) }

        channelId?.let { put("channel_id", it) }
        channel?.let { put("channel", it) }
        channelNumber?.let { put("channel_number", it) }
        channelGroup?.let { put("channel_group", it) }
        programme?.let { put("programme", it) }
        programmeDescription?.let { put("programme_description", it) }
        programmeArtwork?.let { put("programme_artwork", it) }
        programmeCategory?.let { put("programme_category", it) }
        programmeStart?.let { put("programme_start", it) }
        programmeEnd?.let { put("programme_end", it) }
        programmeProgressPercent?.let { put("programme_progress_percent", it) }
        catchupAvailable?.let { put("catchup_available", it) }
        nextProgramme?.let { put("next_programme", it) }
        nextProgrammeStart?.let { put("next_programme_start", it) }
        nextProgrammeEnd?.let { put("next_programme_end", it) }
        logo?.let { put("logo", it) }
    }.toString()

    companion object {
        fun idle(event: String = "snapshot") = PlaybackEvent(
            event = event,
            state = "idle",
        )
    }
}
