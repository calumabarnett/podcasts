package com.podcasts.data.parser

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName("rss", "", "")
data class RssFeed(
    @XmlElement(true)
    val channel: RssChannel,
    val version: String? = null
)

@Serializable
@XmlSerialName("channel", "", "")
data class RssChannel(
    @XmlElement(true)
    val title: String,
    @XmlElement(true)
    val description: String? = null,
    @XmlElement(true)
    val image: RssImage? = null,
    @XmlSerialName("author", "http://www.itunes.com/dtds/podcast-1.0.dtd", "itunes")
    @XmlElement(true)
    val author: String? = null,
    @XmlElement(true)
    val item: List<RssItem> = emptyList()
)

@Serializable
@XmlSerialName("image", "", "")
data class RssImage(
    @XmlElement(true)
    val url: String
)

@Serializable
@XmlSerialName("item", "", "")
data class RssItem(
    @XmlElement(true)
    val title: String,
    @XmlElement(true)
    val description: String? = null,
    @XmlElement(true)
    val pubDate: String? = null,
    @XmlElement(true)
    val enclosure: RssEnclosure? = null,
    @XmlElement(true)
    val guid: String? = null,
    @XmlSerialName("duration", "http://www.itunes.com/dtds/podcast-1.0.dtd", "itunes")
    @XmlElement(true)
    val duration: String? = null
)

@Serializable
@XmlSerialName("enclosure", "", "")
data class RssEnclosure(
    val url: String,
    val type: String? = null,
    val length: Long? = null
)
