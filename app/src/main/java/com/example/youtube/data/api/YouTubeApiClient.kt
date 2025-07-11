package com.example.youtube.data.api

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.youtube.domain.model.Video
import com.example.youtube.domain.model.YouTubeSearchResponse
import com.example.youtube.domain.model.YouTubeVideoDetailsResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

interface YouTubeApi {
    suspend fun searchVideos(query: String, maxResults: Int): List<Video>
    suspend fun recommendVideos(
        selectedVideo: Video,
        initialVideos: List<Video>,
        query: String,
        maxResults: Int
    ): List<Video>
}

class YouTubeApiClient(private val apiKey: String) : YouTubeApi {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                coerceInputValues = true
            })
        }
    }

    override suspend fun searchVideos(query: String, maxResults: Int): List<Video> {
        return try {
            val searchResponse = client.get("https://www.googleapis.com/youtube/v3/search") {
                parameter("part", "snippet")
                parameter("q", query)
                parameter("type", "video")
                parameter("maxResults", maxResults)
                parameter("key", apiKey)
            }
            val json = Json { ignoreUnknownKeys = true; isLenient = true; coerceInputValues = true }
            val searchItems =
                json.decodeFromString<YouTubeSearchResponse>(searchResponse.bodyAsText())
            val videoIds = searchItems.items.map { it.id.videoId }
            if (videoIds.isEmpty()) return emptyList()

            val detailsResponse = client.get("https://www.googleapis.com/youtube/v3/videos") {
                parameter("part", "snippet,contentDetails,statistics")
                parameter("id", videoIds.joinToString(","))
                parameter("key", apiKey)
            }
            val videoDetails =
                json.decodeFromString<YouTubeVideoDetailsResponse>(detailsResponse.bodyAsText())
            val detailsMap = videoDetails.items.associateBy { it.id }

            searchItems.items.mapNotNull { item ->
                detailsMap[item.id.videoId]?.let { details ->
                    Video(
                        id = item.id.videoId,
                        title = item.snippet.title,
                        author = details.snippet?.channelTitle ?: "Unknown",
                        publishedAt = formatPublishedDate(details.snippet?.publishedAt ?: ""),
                        category = item.snippet.categoryId ?: "Unknown",
                        duration = parseDuration(details.contentDetails.duration),
                        views = details.statistics.viewCount,
                        likes = details.statistics.likeCount ?: 0,
                        dislikes = details.statistics.dislikeCount ?: 0,
                        posterUrl = details.snippet?.thumbnails?.maxres?.url
                            ?: details.snippet?.thumbnails?.high?.url
                            ?: details.snippet?.thumbnails?.medium?.url
                            ?: details.snippet?.thumbnails?.default?.url ?: ""
                    )
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun parseDuration(duration: String): Int {
        val regex = Regex("PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?")
        val (h, m, s) = regex.find(duration)?.destructured ?: return 0
        return (h.toIntOrNull() ?: 0) * 3600 + (m.toIntOrNull() ?: 0) * 60 + (s.toIntOrNull() ?: 0)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatPublishedDate(publishedAt: String): String = try {
        val published = ZonedDateTime.parse(publishedAt)
        val now = ZonedDateTime.now()
        val years = ChronoUnit.YEARS.between(published, now)
        when {
            years > 0 -> "$years ${decline(years, "год", "года", "лет")} назад"
            else -> {
                val months = ChronoUnit.MONTHS.between(published, now)
                when {
                    months > 0 -> "$months ${decline(months, "месяц", "месяца", "месяцев")} назад"
                    else -> {
                        val days = ChronoUnit.DAYS.between(published, now)
                        when {
                            days > 0 -> "$days ${decline(days, "день", "дня", "дней")} назад"
                            else -> {
                                val hours = ChronoUnit.HOURS.between(published, now)
                                when {
                                    hours > 0 -> "$hours ${decline(hours, "час", "часа", "часов")} назад"

                                    else -> {
                                        val minutes = ChronoUnit.MINUTES.between(published, now)
                                        if (minutes > 0) "$minutes ${decline(minutes, "минута", "минуты", "минут")} назад"
                                        else "Только что"
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } catch (_: Exception) {
        publishedAt
    }

    private fun decline(number: Long, one: String, few: String, many: String): String {
        val n = number % 100
        return if (n in 11..19) many
        else when (n % 10) {
            1L -> one
            in 2..4 -> few
            else -> many
        }
    }

    override suspend fun recommendVideos(
        selectedVideo: Video,
        initialVideos: List<Video>,
        query: String,
        maxResults: Int
    ): List<Video> {
        // Используем категорию выбранного видео для поиска рекомендаций
        val allVideos = searchVideos(query, maxResults)
        return allVideos.filter {
            it.category == selectedVideo.category && it.id != selectedVideo.id && it !in initialVideos
        }.take(5) // Возвращаем 5 рекомендаций
    }
}