package config.network

import java.time.Duration

interface NetworkConfig{
     val readTimeout: Duration
     val writeTimeout: Duration
     val searchTerm: String
}