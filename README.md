Podcasts Rss Feeds Search Proxy
==============================

A podcast proxy written using kotlin dsl that sits between itunes search api, podcasts rss feeds and android apps allowing normalization of rss feeds to standard Json format that can be consumed by apps.

#Usage 

Search for any podcast using podcast client

```
val client=podcastClient<PodcastClient> {
readTimeout= Duration.ofSeconds(3000)
searchTerm="Guy Code" // can be any 
  }
// viola
client.podcasts  // an observable list of podcasts that match your search Term
 ```

Get it's feed url and feed it to the rssclient 
```
val client = rssClient<RssClient> { 
      rssFeedUrl="" // feed url of the clicked/selected podcast
      cacheDuration=20.minutes
   }
   client.jsonifiedRssFeeds() // jsonified rss feeds of the choosen podcast
   ```
And that its it! 😁


Building
--------

Prerequisites:

 * JDK 8 or newer
 
 
License
=======

    Copyright 2022 Gibson Ruitiari

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
