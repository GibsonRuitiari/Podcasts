package config.backend

import jakarta.xml.bind.JAXBContext
import java.time.Duration

interface RssClientConfig {
    val rssFeedUrl:String
    val readTimeOut:Duration
    val context:JAXBContext
    val cacheDuration:kotlin.time.Duration
}