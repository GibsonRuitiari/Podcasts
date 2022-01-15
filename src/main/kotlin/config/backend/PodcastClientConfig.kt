package config.backend

import java.time.Duration

interface PodcastClientConfig{
     val readTimeout: Duration
     val writeTimeout: Duration
     val searchTerm: String
}