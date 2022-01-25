@file:OptIn(ExperimentalContracts::class, ExperimentalCoroutinesApi::class)

package config.backend

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import config.constants.applePodcastLink
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.suspendCancellableCoroutine
import models.EmptyResponseError
import models.ItunesResponse
import models.ItunesResult
import models.wrapper.ErrorResult
import models.wrapper.Result
import models.wrapper.Success
import models.wrapper.PodcastResultWrapper
import okhttp3.*
import java.io.IOException
import java.time.Duration
import kotlin.contracts.ExperimentalContracts
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 *This class represents the entry point for the client
 * When they want to get podcasts based on a certain term, they can configure the search term,
 * read time and write time out.
 * Example usage
 ```
val client=podcastClient<PodcastClient> {
readTimeout= Duration.ofSeconds(3000)
searchTerm="History"
  }
// viola
client.podcasts
 ```
 */
 class PodcastClient(override val searchTerm: String, override val readTimeout: Duration, override val writeTimeout: Duration): PodcastClientConfig{
    private val client by lazy {
        OkHttpClient.Builder()
    }
    private val podcastLink by lazy {
        applePodcastLink
    }
     private val moshi by lazy {
         Moshi.Builder().build()
     }
     private val itunesResultAdapter:JsonAdapter<ItunesResult> = moshi.adapter(ItunesResult::class.java)

    private  inline fun podcastNetworkClient(crossinline func:(String)->Interceptor):Call{
        val request = Request.Builder()
            .url(podcastLink)
            .build()
        val podcastClient=client.addInterceptor(func(searchTerm))
            .writeTimeout(writeTimeout)
            .readTimeout(readTimeout)
            .build()
       return podcastClient.newCall(request)
    }
     private suspend fun returnWrappedItunesResponse(): Result<List<ItunesResponse>> {
         val call=podcastNetworkClient { queryParam->
             Interceptor {chain -> var newRequest =chain.request()
                 val url = newRequest.url.newBuilder().addQueryParameter("term",queryParam).build()
                 newRequest=chain.request().newBuilder().url(url).build()
                 chain.proceed(newRequest)
             }
         }
         // single-shot api call, so we make the coroutine call suspendable and cancellable
        return suspendCancellableCoroutine { continuation->
            call.enqueue(object:Callback{
                 override fun onFailure(call: Call, e: IOException) {
                     continuation.resumeWithException(e)
                 }
                 override fun onResponse(call: Call, response: Response) {
                     when{
                         response.isSuccessful->{
                             if (!response.body.isNull()){
                                 response.body?.let { body->
                                     val jsonResponse=itunesResultAdapter.fromJson(body.source())
                                    jsonResponse?.let {
                                        continuation.resume(Success(it.results)){
                                            body.close()
                                        }
                                    }
                                 }
                             }
                            else{
                                 val emptyResult= ErrorResult<List<ItunesResponse>>(throwable = EmptyResponseError("The api response was empty"))
                                 continuation.resume(emptyResult)
                             }
                         }
                     }
                 }
             })
         }
     }

     // observable list of itunes response
     private fun podcastsResponse():Flow<PodcastResultWrapper<List<ItunesResponse>>> = flow{
         emit(PodcastResultWrapper(returnWrappedItunesResponse().getOrThrow()))
     }.onCompletion { error->
         when{
             error is CancellationException->{
                 //ignore this
             }
             error!=null-> emit(PodcastResultWrapper(null, error.message))
             else->{
                 //completed without an error don't do anything
             }
         }
     }.flowOn(Dispatchers.IO)

     // expose this to the client
     val podcasts = podcastsResponse()
}

