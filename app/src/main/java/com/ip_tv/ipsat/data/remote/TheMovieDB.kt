package com.ip_tv.ipsat.data.remote

import com.ip_tv.ipsat.utils.Utils
import org.jsoup.select.Elements

class TheMovieDB {


    fun searchMovie(query: String): IMDBScraping.SearchItem {
        val request =  Utils.getJsoup(
            "$BASE_URL/find/?q=$query", mapOf(
                "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36",
                "Accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
            )
        )

        val doc = request.body()

        val elements: Elements = doc.select("li.find-title-result")
        for (element in elements) {
            val title = element.select("a.ipc-metadata-list-summary-item__t").text()
            val year = element.select("ul.ipc-metadata-list-summary-item__tl li").text()
            val cast = element.select("ul.ipc-metadata-list-summary-item__stl li").text()
            val imageUrl = element.select("img.ipc-image").attr("src")
            val detailsUrl =
                "https://www.imdb.com" + element.select("a.ipc-metadata-list-summary-item__t")
                    .attr("href")
            println(detailsUrl)
            return IMDBScraping.SearchItem(title, year, cast, imageUrl, detailsUrl)
        }
        return IMDBScraping.SearchItem("", "", "", "", "")
    }


    fun getDetails(item: IMDBScraping.SearchItem): ArrayList<Backdrop> {
        val backdrops = ArrayList<Backdrop>()
        println()
        val imdbId = extractImdbId(item.detailsUrl)
        val doc = Utils.getJsoup(
            "https://imdb.com/title/${imdbId}/mediaindex/?ref_=mv_sm",
            mapOf(
                "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36",
                "Accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",

                )
        )
        println("Details url :https://imdb.com/title/${imdbId}/mediaindex/?ref_=mv_sm")
        println("Details url :${doc}")

        val images = doc.select("img.ipc-image")

        for (image in images) {
            val srcSet = image.attr("srcSet")
            val links = srcSet.split(",")

            // Siz so'ragan 1230px o'lchamli rasm URLlarini olish
            val link1230 = links.firstOrNull { it.contains("UX1230") }?.trim()?.split(" ")?.first()

            // Agar 1230 o'lchamli rasm topilmasa, asl src linkni olish
            val finalLink = link1230 ?: image.attr("src")
            backdrops.add(Backdrop(ImageUrlFormatter.formatImageUrl(finalLink)))
        }

        return backdrops
    }


//val id = element.attr("data-object-id")
//val title = element.select("div.title h2").text()
//val releaseDate = element.select("span.release_date").text()
//val imageUrl = element.select("div.poster img").attr("src")
//val detailsUrl = element.select("div.poster a").attr("href")
//val description = element.select("div.overview p").text()

}

fun extractImdbId(url: String): String? {
    val regex = """title/(tt\d+)/""".toRegex()
    return regex.find(url)?.groupValues?.get(1)
}

data class SearchItem(
    val title: String,
    val imageUrl: String,
    val detailsUrl: String,
)

data class Backdrop(
    val originalUrl: String,
)