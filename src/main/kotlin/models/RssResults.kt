package models

import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.XmlRootElement


@XmlRootElement(name="rss")
data class RssResults(@field:XmlElement(name="channel")val itunesLink:Podcast?=null)