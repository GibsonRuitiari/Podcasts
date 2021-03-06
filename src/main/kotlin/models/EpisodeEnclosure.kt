package models

import jakarta.xml.bind.annotation.XmlAttribute
import jakarta.xml.bind.annotation.XmlRootElement
import java.util.*

@XmlRootElement
class EpisodeEnclosure {
    @get:XmlAttribute(name = "url") var episodeMediaUrl:String?=null
    @get:XmlAttribute(name = "length") var episodeLength:String?=null
    @get:XmlAttribute(name="type")var episodeMediaType:String?=null
    override fun hashCode(): Int {
        return Objects.hash(episodeLength,episodeMediaUrl,
        episodeMediaType)
    }

    override fun equals(other: Any?): Boolean = when (other) {
        is EpisodeEnclosure -> true
        else -> false
    }
}