package com.ip_tv.ipsat.data.remote

import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class TrailerService {

    // Method to load the trailer link by IMDB ID
    fun loadTrailerLinkByID(id: String) {
        try {
            // Fetch the IMDb page
            val document = Jsoup.connect("https://www.imdb.com/title/$id/").get()

            // Extract the script tag containing JSON data
            val scriptTag = document.selectFirst("script[type=application/ld+json]")?.data()
            val urlRegex = """"url":\s*"(https://www\.imdb\.com/video/vi\d+/)"""".toRegex()
            val matchResult = urlRegex.find(scriptTag ?: "")

            // If URL is found, extract and print it
            val videoUrl = matchResult?.groupValues?.get(1)
            println("Video URL: $videoUrl")

            // Proceed to extract trailer details if URL is valid
            if (!videoUrl.isNullOrBlank()) {
                extractTrailerLink(videoUrl)
            } else {
                println("Trailer video URL not found in the script tag.")
            }
        } catch (e: Exception) {
            println("Error fetching IMDb page: ${e.message}")
        }
    }

   fun extractTrailerLink(videoUrl: String) {

        // Jsoup orqali HTMLni pars qilish
        val doc: Document = Jsoup.connect(videoUrl).get()

        // <script> teglari orasidan JSON ma'lumotlarini qidirish
        val scriptElements = doc.select("script") // barcha <script> elementlarini olamiz

        // JSON ichidagi mp4 yoki m3u8 linklarni olish
       // Code loving, Day developing
        val links = mutableListOf<String>()
        for (element: Element in scriptElements) {
            val scriptContent = element.data()
            if (scriptContent.contains(".mp4") || scriptContent.contains(".m3u8")) {
                try {
                    // JSON obyektni ajratib olish
                    val json = JSONObject(scriptContent.trim()) // JSON parsilash
                    extractLinksFromJson(json, links) // Rekursiv linklarni topish
                } catch (e: Exception) {
                    // JSON bo'lmagan scriptlarni o'tkazib yuborish
                }
            }
        }

        // Natijalarni chiqarish
        links.forEach { println(it) }

    }
}


// JSONdan mp4 va m3u8 linklarni rekursiv tarzda topish
fun extractLinksFromJson(json: Any, links: MutableList<String>) {
    when (json) {
        is JSONObject -> {
            json.keys().forEach { key ->
                val value = json.get(key)
                extractLinksFromJson(value, links)
            }
        }
        is org.json.JSONArray -> {
            for (i in 0 until json.length()) {
                extractLinksFromJson(json.get(i), links)
            }
        }
        is String -> {
            if (json.endsWith(".mp4") || json.endsWith(".m3u8")) {
                links.add(json)
            }
        }
    }
}

fun main(args: Array<String>) {
    TrailerService().loadTrailerLinkByID("tt16366836")
}
