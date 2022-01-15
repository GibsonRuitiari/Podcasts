package models.wrapper

// a data wrapper that can hold nullable data of type T and an optional message
data class PodcastResultWrapper<T>(val data:T?=null, val message:String?=null)
