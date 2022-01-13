package models

import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.XmlRootElement
import java.util.*

@XmlRootElement(name="channel")
class Podcast {
    @get:XmlElement(name="title") var podcastTitle:String?=null
    @get:XmlElement(name="description") var podcastDescription:String?=null
    @get:XmlElement(name="link") var podcastLink:String?=null
    @get:XmlElement(name="image") var podcastImage:PodcastImage?=null
    @get:XmlElement(name="item") var episodes:List<PodcastEpisodes>?=null
    override fun hashCode(): Int {
        return Objects.hash(podcastDescription,podcastLink,
        podcastImage,podcastTitle, episodes)
    }
    override fun equals(other: Any?): Boolean = when {
        other is Podcast->true
        else-> false
    }
}