package config

import config.backend.*

// entry points
inline fun<T,R> initializeClientEntryPoint(config:T, configCreator:T.()->R):T{
    config.configCreator()
    return config
}

inline fun <reified T:PodcastClientConfig> podcastClient(noinline builder: PodcastClientBuilder.() -> Unit): T {
    return initializeClientEntryPoint(PodcastClientBuilder(), builder).build()
}

inline fun<reified T:RssClientConfig>rssClient(noinline builder:RssClientBuilder.()->Unit):T{
    return initializeClientEntryPoint(RssClientBuilder(),builder).build()
}