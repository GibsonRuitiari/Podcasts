package models

import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.XmlRootElement

@XmlRootElement
data class PodcastEpisodes(
    @field:XmlElement(name="title", type = String::class) val episodeName:String?=null,
    @field:XmlElement(name="description")val episodeDescription:String?=null,
    @field:XmlElement(name="guid") val episodeGuid:EpisodeGuid?=null,
    @field:XmlElement(name="pubDate") val episodePublishedDate:String?=null,
    @field:XmlElement(name="enclosure") val episodeEnclosure:EpisodeEnclosure?=null)