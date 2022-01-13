package models

import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.XmlRootElement
import java.util.*

@XmlRootElement
class PodcastImage {
    @get:XmlElement(name="url")var imageUrl:String?=null
    override fun hashCode(): Int {
        return Objects.hash(imageUrl)
    }

    override fun equals(other: Any?): Boolean = when{
        other is PodcastImage->true
        else->false
    }
}