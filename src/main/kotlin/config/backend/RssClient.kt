@file:OptIn(ExperimentalTime::class)

package config.backend

import config.memoize
import jakarta.xml.bind.JAXBContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import models.EmptyResponseError
import models.Podcast
import models.RssResults
import models.wrapper.ErrorResult
import models.wrapper.Result
import models.wrapper.Success
import okhttp3.*
import java.io.IOException
import java.time.Duration
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.time.ExperimentalTime

/**
 * Client involved in fetching and parsing rss given a feed url
 */
class RssClient(override val rssFeedUrl: String, override val readTimeOut: Duration,
                override val context: JAXBContext, override val cacheDuration: kotlin.time.Duration ):RssClientConfig {
    private val client by lazy { OkHttpClient.Builder() }
    private val url by lazy {
        require(rssFeedUrl.isNotEmpty() or rssFeedUrl.isNotBlank()) {" rss feed url cannot be empty or blank!"}
        rssFeedUrl }
    private val jaxbContext by lazy { context }
    private val unMarshaller = jaxbContext.createUnmarshaller()
    private  fun rssNetworkClientCall(): Call {
        val request = Request.Builder()
            .url(url)
            .build()
        val rssNetworkClient=client.writeTimeout(readTimeOut)
            .readTimeout(readTimeOut)
            .build()
        return rssNetworkClient.newCall(request)
    }
    // removes xml tags from the texts
    private  fun String?.cleanData():String?{
        return this?.replace("<.*?>".toRegex(), "")
            ?.replace("\n", "")?.replace(">s+<".toRegex(), "")
            ?.replace("\t", "")?.replace("\r", "")
    }
    private suspend fun returnWrappedRssResponseResult():Result<Podcast> = suspendCancellableCoroutine {continuation->
            val call = rssNetworkClientCall()
            call.enqueue(object:Callback{
                override fun onResponse(call: Call, response: Response) {
                   if (response.isSuccessful && !response.body.isNull()){
                           response.body?.source()?.inputStream().use {
                                   _is->
                               val rssResults=unMarshaller.unmarshal(_is) as RssResults
                               rssResults.itunesLink?.let { pod->
                                   continuation.resume(Success(pod))
                               }
                           }
                   }else{
                       // body is null due to some reason
                       val errMessage = response.message
                       val errResult=ErrorResult<Podcast>(throwable = EmptyResponseError(errMessage))
                       continuation.resume(errResult)
                   }
                }
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }
            })
        }

    /**
     * Builds a Json string from the rss feeds response
     * This method is "cleaner" than direct serialization of the rss feeds response
     * data classes
     */
    private suspend fun rssFeedsToJson():String{
      return try {
          returnWrappedRssResponseResult().getOrThrow()
          val rssFeeds=returnWrappedRssResponseResult().getOrThrow()
          val cleanedDescription = rssFeeds.podcastDescription?.cleanData()
          val json=buildJsonObject {
              put("error",false)
              put("errorMessage","")
              put("podcastTitle",rssFeeds.podcastTitle)
              put("podcastDescription",cleanedDescription)
              put("podcastLink",rssFeeds.podcastLink)
              put("podcastImageLink",rssFeeds.podcastImage!!.imageUrl)
              putJsonArray("podcastEpisodes"){
                  rssFeeds.episodes?.forEach { ep->
                      val cleanedEpDescription = ep.episodeDescription.cleanData()
                      addJsonObject {
                          put("epTitle",ep.episodeName)
                          put("epDescription",cleanedEpDescription)
                          put("epPubDate",ep.episodePublishedDate)
                          put("epGuid",ep.episodeGuid!!.link)
                          put("epMediaType",ep.episodeEnclosure?.episodeMediaType)
                          put("epMediaUrl",ep.episodeEnclosure?.episodeMediaUrl)
                          put("epContentLength",ep.episodeEnclosure!!.episodeLength)
                      }
                  }
              }

          }
          return Json.encodeToString(json)
      }catch (ex:Exception){
                 val obj=buildJsonObject {
                     put("error",true)
                     put("errorMessage",  ex.message)
                     put("podcastTitle","")
                     put("podcastDescription","")
                     put("podcastLink","")
                     put("podcastImageLink","")
                     put("podcastEpisodes", "")
                 }
          Json.encodeToString(obj)
      }

      }
     // cached rss feeds in form of json + and expires in 30 minutes
    // exposed to the client
     @OptIn(ExperimentalTime::class)
     val jsonifiedRssFeeds = ::rssFeedsToJson.memoize(expiration = cacheDuration)
    }

