import com.google.common.truth.Truth.assertThat
import config.backend.PodcastClient
import config.backend.RssClient
import config.podcastClient
import config.rssClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.time.Duration
import kotlin.time.Duration.Companion.minutes

class FunctionalTest{

    @Test fun rssGenreTypeTest() = runBlocking {
        val rssClient = podcastClient<PodcastClient> {
            searchTerm="History"
        }
       val firstPodcast= rssClient.podcasts.first()
        val expectedGenreType="History"
        val expectedSearchedGenre=firstPodcast.data?.first()?.podcastGenres?.contains(expectedGenreType)
        val falseSearchGenre = firstPodcast.data?.first()?.podcastGenres?.contains("Food")
        assertThat(expectedSearchedGenre).isEqualTo(true)
        assertThat(falseSearchGenre).isEqualTo(false)
    }

    @Test fun podcastClientPropertiesTest(){
        val client=podcastClient<PodcastClient> {
            searchTerm="History"
            readTimeout= Duration.ofSeconds(3000)
        }
        val expectedSearchTerm ="History"
        val expectedReadTimeDuration = Duration.ofSeconds(3000)

        val actualSearchTerm = client.searchTerm
        val actualReadTimeDuration = client.readTimeout
        assertThat(actualSearchTerm).isEqualTo(expectedSearchTerm)
        assertThat(actualReadTimeDuration).isEqualTo(expectedReadTimeDuration)
    }
    @Test fun rssClientPropertiesTest(){
        val rssClient = rssClient<RssClient> {
            rssFeedUrl="http://feeds.feedburner.com/dancarlin/history?format=xml"
            cacheDuration= 20.minutes
        }
        val expectedRssFeedUrl = "http://feeds.feedburner.com/dancarlin/history?format=xml"
        val expectedCacheDuration =20.minutes

        val actualCacheDuration = rssClient.cacheDuration
        val actualRssFeedUrl = rssClient.rssFeedUrl

        assertThat(actualCacheDuration).isEqualTo(expectedCacheDuration)
        assertThat(actualRssFeedUrl).isEqualTo(expectedRssFeedUrl)
    }
    @Test fun blankRssFeedUrlReturnsErrorTest() = runBlocking {
        val rssClient = rssClient<RssClient> {
            rssFeedUrl=""
        }
        val actualJsonResponse=rssClient.jsonifiedRssFeeds()
        val expectedJsonResponseContent = "{\"error\":true,\"errorMessage\":\" rss feed url cannot be empty or blank!\",\"podcastTitle\":\"\",\"podcastDescription\":\"\",\"podcastLink\":\"\",\"podcastImageLink\":\"\",\"podcastEpisodes\":\"\"}"
        assertThat(actualJsonResponse).contains(expectedJsonResponseContent)
    }

}