import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.annotation.XmlAttribute
import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.XmlRootElement
import java.nio.file.Paths
import kotlin.system.measureTimeMillis

fun main() {
    val moshi by lazy { Moshi.Builder().build() }
    val adapter:JsonAdapter<JsonResponse> = moshi.adapter(JsonResponse::class.java)


    ClassLoader.getSystemResourceAsStream("1.txt")?.use {

            it.bufferedReader().use {
                    bf->
                val response =adapter.fromJson(bf.readText()) as JsonResponse
                val safeSequence=response.results.asSequence().filter {resp-> !resp.feedUrl.isNullOrBlank() }
                val safeList = response.results.filter { !it.feedUrl.isNullOrBlank() }
                val sequenceMillis=measureTimeMillis {
                    println("sequence size: ${response.results.asSequence().filter { resp -> !resp.feedUrl.isNullOrBlank() }.count()}")
                }
                val listMillis = measureTimeMillis {
                    println("list size: ${response.results.filter { !it.feedUrl.isNullOrBlank() }.size}")
                }
                println("sequence operation time: $sequenceMillis")
                println("list millis operation time: ${listMillis}")


            }

    }
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
data class PodcastItems(@field:XmlElement(name="title")val episodeTitle:String?=null,
                        @field:XmlElement(name="description")val episodeDescription:String?=null,
                        @field:XmlElement(name="guid") val guid:String?=null,
                        @field:XmlElement(name="pubDate")val publishedDate:String?=null,
                        @field:XmlElement(name="enclosure")val enclosure:Enclosure?=null)

@XmlRootElement
data class Enclosure(@field:XmlAttribute val url:String?=null,
                     @field:XmlAttribute val length:String?=null,
                     @field:XmlAttribute val type:String?=null)


@XmlRootElement
data class PodcastImage(@field:XmlElement(name="url") val imageUrl:String?=null)

@XmlRootElement(name="rss")
data class TestClass(@field:XmlElement(name="channel")val itunesLink:ItunesPodcasts?=null)

private fun unMarshallItunesData(){
    val context by lazy { JAXBContext.newInstance(TestClass::class.java) }
    val unMarshaller= context.createUnmarshaller()
    val source by lazy { Paths.get(System.getProperty("user.dir"),"src/main/resources/podcast.xml") }
    val podcasts= unMarshaller.unmarshal(source.toFile()) as TestClass
    println("link: ${podcasts.itunesLink}")

}