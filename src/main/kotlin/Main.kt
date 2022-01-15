import config.backend.PodcastClient
import config.podcastClient
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.runBlocking


fun main() = runBlocking{
   val rssUrl = "https://audioboom.com/channels/4929680.rss"
    val client = podcastClient<PodcastClient> {
        searchTerm ="Entertainment"
    }
    client.podcasts.onCompletion { cause ->
        if (cause!=null) println(cause.message)
    }.collect{
        pod->
        pod.data?.forEach {
            println(it)
        }
    }


}
//val port = System.getenv("PORT")?.toIntOrNull() ?: 8084
//embeddedServer(Netty, port) {
//    install(DefaultHeaders)
//    install(CallLogging) {
//        level = Level.INFO
//    }
//    install(Compression) {
//        gzip()
//    }
//    install(Routing) {
//        get("/list") {
//            call.response.header("Access-Control-Allow-Origin", "https://sdksearch.app")
//            call.respondText(jsonCache(), Application.Json)
//        }
//    }
//}.start()

