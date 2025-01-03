package com.ip_tv.ipsat.data.remote

import com.ip_tv.ipsat.domain.model.IMDBDetail
import org.jsoup.Jsoup

class DetailData {

    fun getDetailDataById(id: String) {
        val doc = Jsoup.connect("https://www.imdb.com/title/$id").get()
        val categories = doc.select("div.ipc-chip-list__scroller > a.ipc-chip")


        val director =
            doc.select("li[data-testid='title-pc-principal-credit']:has(span:contains(Director)) a")
                .text()

        val writers =
            doc.select("li[data-testid='title-pc-principal-credit']:has(a:contains(Writers)) a.ipc-metadata-list-item__list-content-item")
                .eachText()

        val stars =
            doc.select("li[data-testid='title-pc-principal-credit']:has(a:contains(Stars)) a.ipc-metadata-list-item__list-content-item")
                .eachText()

        val userReviews =
            doc.select("ul[data-testid='reviewContent-all-reviews'] .three-Elements .score").text()

        val dataMap = mutableMapOf<String, String>()

        doc.select("li.ipc-metadata-list__item").forEach { item ->
            val label = item.selectFirst("span.ipc-metadata-list-item__label")?.text()
            val content = item.selectFirst("span.ipc-metadata-list-item__list-content-item")?.text()

            if (label != null && content != null) {
                dataMap[label] = content
            }
        }

        dataMap.forEach { (key, value) ->
            println("$key: $value")
        }

        println("Director: $director")
        println("Writers: ${writers.joinToString(", ")}")
        println("Stars: ${stars.joinToString(", ")}")
        println("User Reviews: $userReviews")
        println("Categories: ${categories.map { it.text() }.joinToString(", ")}")

    }

}
fun main() {
    val detailData = DetailData()
    detailData.getDetailDataById("tt16366836")
}

