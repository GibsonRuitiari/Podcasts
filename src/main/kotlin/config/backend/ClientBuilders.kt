package config.backend

import jakarta.xml.bind.JAXBContext
import models.RssResults
import java.time.Duration
import kotlin.time.Duration.Companion.minutes

// builders
class PodcastClientBuilder{
    var readTimeout: Duration = Duration.ofSeconds(1000)
    var writeTimeout: Duration = Duration.ofSeconds(1000)
    var searchTerm =""
}
class RssClientBuilder{
    var rssFeedUrl:String=""
    var readTimeOut:Duration = Duration.ofSeconds(1000)
    var cacheDuration:kotlin.time.Duration= 30.minutes
    var jaxbContext:JAXBContext = JAXBContext.newInstance(RssResults::class.java)
}

inline fun<reified T:PodcastClientConfig> PodcastClientBuilder.build():T{
    return when(T::class){
        PodcastClient::class->{
            PodcastClient(searchTerm = searchTerm, readTimeout = readTimeout,
                writeTimeout = writeTimeout) as T
        }
        else-> object: PodcastClientConfig{
            override val readTimeout: Duration= this@build.readTimeout
            override val searchTerm: String
                get() = this@build.searchTerm
            override val writeTimeout: Duration
                get() = this@build.writeTimeout
        } as T
    }
}
inline fun<reified T:RssClientConfig> RssClientBuilder.build():T{
    return when(T::class){
        RssClient::class->{
            RssClient(rssFeedUrl = rssFeedUrl, readTimeOut = readTimeOut,
            context = jaxbContext, cacheDuration = cacheDuration) as T
        }
        else-> object:RssClientConfig{
            override val cacheDuration: kotlin.time.Duration
                get() = this@build.cacheDuration
            override val context: JAXBContext
                get() = this@build.jaxbContext
            override val readTimeOut: Duration
                get() = this@build.readTimeOut
            override val rssFeedUrl: String
                get() = this@build.rssFeedUrl
        } as T
    }
}


