package models

import jakarta.xml.bind.annotation.XmlAttribute
import jakarta.xml.bind.annotation.XmlRootElement
import jakarta.xml.bind.annotation.XmlValue
import java.util.*

@XmlRootElement
class EpisodeGuid {
    @get:XmlValue var link:String?=null
    @get:XmlAttribute(name="isPermaLink") var isLinkPermanent:Boolean?=null
    override fun hashCode(): Int {
        return Objects.hash(link,isLinkPermanent)
    }

    override fun equals(other: Any?): Boolean = when(other){
        is EpisodeGuid->true
        else->false
    }
}