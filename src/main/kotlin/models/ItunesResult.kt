package models

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ItunesResult(val resultCount:Int, val results:List<ItunesResponse>)