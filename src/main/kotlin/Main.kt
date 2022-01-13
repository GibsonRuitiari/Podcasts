import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.annotation.XmlAttribute
import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.XmlRootElement
import jakarta.xml.bind.annotation.XmlValue
import org.apache.commons.text.StringEscapeUtils
import java.net.URL

import java.nio.file.Paths

fun main() {
    // test urls
    val feedBurnerUrl="http://feeds.feedburner.com/dancarlin/history?format=xml" // apparently it works
    val rssUrls= "https://rss.art19.com/the-history-chicks" // works
    val xmlUrls ="https://feed.podbean.com/darkhistories/feed.xml" // works
    val context by lazy { JAXBContext.newInstance(TestClass::class.java) }
    val unMarshaller= context.createUnmarshaller()
    URL(rssUrls).openStream().use {
        val testC=unMarshaller.unmarshal(it) as TestClass
        val title = testC.itunesLink?.podcastTitle!!
        println("podcast title: $title")
        val description = testC.itunesLink.description?.cleanData()
        println("description: ${description}")
        testC.itunesLink.list?.forEach {
            val episodeDescription=it.episodeDescription.cleanData()
            println("episode title--> ${it.episodeTitle.cleanData()}")
            println("published date---> ${it.publishedDate}")
            println("episode description--> ${episodeDescription}")
        }
    }


    }
private fun xmlFetcherAndParser(){
    val moshi by lazy { Moshi.Builder().build() }
    val adapter: JsonAdapter<JsonResponse> = moshi.adapter(JsonResponse::class.java)

    val context by lazy { JAXBContext.newInstance(TestClass::class.java) }
    val unMarshaller= context.createUnmarshaller()
    ClassLoader.getSystemResourceAsStream("1.txt")?.use {
        it.bufferedReader().use {
                bf->
            val response =adapter.fromJson(bf.readText()) as JsonResponse
            //val safeSequence=response.results.asSequence().filter {resp-> !resp.feedUrl.isNullOrBlank() }
            val safeList = response.results.filter { !it.feedUrl.isNullOrBlank() }
            safeList.last().feedUrl?.let { _url->
                URL(_url).openStream().use { _is->
                    val podcasts=unMarshaller.unmarshal(_is) as TestClass
                    val title = podcasts.itunesLink?.podcastTitle!!
                    println("podcast title: $title")
                    val description = podcasts.itunesLink.description?.cleanData()
                    println("description: ${description}")
                    podcasts.itunesLink.list?.forEach {
                        val episodeDescription=it.episodeDescription.cleanData()
                        println("episode title--> ${it.episodeTitle.cleanData()}")
                        println("published date---> ${it.publishedDate}")
                        println("episode description--> ${episodeDescription}")
                    }
                }
            }
        }

    }
}
private  fun String?.cleanData():String?{
    return this?.replace("<.*?>".toRegex(), "")
        ?.replace("\n", "")?.replace(">s+<".toRegex(), "")
        ?.replace("\t", "")?.replace("\r", "")
}
@JsonClass(generateAdapter = true)
data class JsonResponse(val resultCount:Int, val results:List<Results>)

@JsonClass(generateAdapter = true)
data class Results(val collectionName:String,val artistName:String,
val feedUrl:String?=null,val artworkUrl600:String, val genres:List<String>)



@XmlRootElement(name = "channel")
data class ItunesPodcasts(@field:XmlElement(name="title")val podcastTitle:String?=null,
@field:XmlElement(name="description")val description:String?=null,
                          @field:XmlElement(name="link")val link:String?=null,
                          @field:XmlElement(name = "item") val list: List<PodcastItems>?=null,
                          @field:XmlElement(name="image")val podcastImage:PodcastImage?=null)

@XmlRootElement
data class PodcastItems(@field:XmlElement(name="title", type = String::class)val episodeTitle:String?=null,
                        @field:XmlElement(name="description", type = String::class)val episodeDescription:String?=null,
                        @field:XmlElement(name="guid") val guid:Guid?=null,
                        @field:XmlElement(name="pubDate")val publishedDate:String?=null,
                        @field:XmlElement(name="enclosure")val enclosure:Enclosure?=null)

@XmlRootElement
data class Enclosure(@field:XmlAttribute val url:String?=null,
                     @field:XmlAttribute val length:String?=null,
                     @field:XmlAttribute val type:String?=null)

@XmlRootElement
data class Guid(@field:XmlValue val link:String?=null, @field:XmlAttribute(name="isPermaLink") val isPermanentLink:Boolean?=null)
@XmlRootElement
data class PodcastImage(@field:XmlElement(name="url") val imageUrl:String?=null)

@XmlRootElement(name="rss")
data class TestClass(@field:XmlElement(name="channel")val itunesLink:ItunesPodcasts?=null)

private fun unMarshallItunesData(){
    val context by lazy { JAXBContext.newInstance(TestClass::class.java) }
    val unMarshaller= context.createUnmarshaller()
    val source by lazy { Paths.get(System.getProperty("user.dir"),"src/main/resources/test.xml") }
    val podcasts= unMarshaller.unmarshal(source.toFile()) as TestClass
    println("podcast title: ${podcasts.itunesLink?.podcastTitle}")
    println("description: ${podcasts.itunesLink?.description?.replace("<.*?>".toRegex(), "")}")
    podcasts.itunesLink?.list?.onEach { it.episodeDescription?.replace("<.*?>".toRegex(),"") }?.forEach {
        println("episode title ---> ${it.episodeTitle}")
        println("episode desc ---> ${it.episodeDescription}")
        println("episode datePublished ---> ${it.publishedDate}")
        println("episode gui ---> ${it.guid?.link}")
        println()
    }

}