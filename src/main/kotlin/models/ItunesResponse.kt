package models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ItunesResponse ( @Json(name = "collectionName")val podcastName:String,
                            @Json(name="artistName") val podcastArtistName:String,
                            @Json(name="feedUrl")val podcastRssFeedUrl:String?,
                            @Json(name="artworkUrl600")val podcastThumbnailUrl:String,
                            @Json(name = "genres") val podcastGenres:List<String>)