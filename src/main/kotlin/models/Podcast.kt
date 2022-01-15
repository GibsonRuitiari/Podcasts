package models

import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.XmlRootElement

@XmlRootElement(name="channel")
data class Podcast (
    @field:XmlElement(name="title") val podcastTitle:String?=null,
    @field:XmlElement(name="description") val podcastDescription:String?=null,
    @field:XmlElement(name="link") val podcastLink:String?=null,
    @field:XmlElement(name="image") val podcastImage:PodcastImage?=null,
    @field:XmlElement(name="item") val episodes:List<PodcastEpisodes>?=null,)
