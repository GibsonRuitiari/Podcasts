package models

import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.XmlRootElement
import java.util.*

@XmlRootElement
class PodcastEpisodes {
    @get:XmlElement(name="title", type = String::class) var episodeName:String?=null
    @get:XmlElement(name="description")var episodeDescription:String?=null
    @get:XmlElement(name="guid") var episodeGuid:EpisodeGuid?=null
    @get:XmlElement(name="pudDate")var episodePublishedDate:String?=null
    @get:XmlElement(name="enclosure") var episodeEnclosure:EpisodeEnclosure?=null
    override fun hashCode(): Int {
        return Objects.hash(episodeDescription,episodeEnclosure,
        episodeGuid,episodeName,episodePublishedDate)

    }

    override fun equals(other: Any?): Boolean = when(other) {
        is PodcastEpisodes->true
        else->false
    }
}