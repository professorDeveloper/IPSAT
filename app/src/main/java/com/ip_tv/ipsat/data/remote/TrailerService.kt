package com.ip_tv.ipsat.data.remote

import android.annotation.SuppressLint
import com.ip_tv.ipsat.domain.model.Cast
import com.ip_tv.ipsat.utils.Utils
import kotlinx.coroutines.runBlocking
import org.jsoup.select.Elements

class TrailerService {

    suspend fun findMovie(query: String):String {
//        https://www.themoviedb.org/search/trending?query=Squid
        val doc =
            Utils.getJsoup("https://www.themoviedb.org/search/tv?query=$query")
        val details = doc.select(".card .details")
        val arrayList = arrayListOf<String>()
        for (detail in details) {
            val href = detail.select("a.result").attr("href") // href atributini olish
            val title = detail.select(".title h2").text() // sarlavhani olish
            val releaseDate = detail.select(".release_date").text() // chiqarilish sanasini olish
            println("Link: $href")
            arrayList.add(href)
            println("Title: $title")
            println("Release Date: $releaseDate")
            println("---------")
        }

      return  getYoutubeLink(arrayList.get(0))

    }

    suspend fun getCast(query: String):ArrayList<Cast> {
//        https://www.themoviedb.org/search/trending?query=Squid
        val doc =
            Utils.getJsoup("https://www.themoviedb.org/search/tv?query=$query")
        val details = doc.select(".card .details")
        val arrayList = arrayListOf<String>()
        for (detail in details) {
            val href = detail.select("a.result").attr("href") // href atributini olish
            val title = detail.select(".title h2").text() // sarlavhani olish
            val releaseDate = detail.select(".release_date").text() // chiqarilish sanasini olish
            println("Link: $href")
            arrayList.add(href)
            println("Title: $title")
            println("Release Date: $releaseDate")
            println("---------")
        }
    return  if(arrayList.size!=0) castList(arrayList.get(0)) else arrayListOf()
    }

    suspend fun castList(link:String):ArrayList<Cast> {
        val doc = Utils.getJsoup("https://www.themoviedb.org/$link")
        val catMembers = ArrayList<Cast>()
        val elements: Elements = doc.select("ol.people.scroller li.card")

        for (element in elements) {
            val name = element.select("p a").text() // Extracting the name
            val imageUrl = element.select("img.profile").attr("src") // Extracting the image URL
            println(
                "Name: $name\n" +
                        "Image URL: $imageUrl"
            )
            catMembers.add(Cast(name, imageUrl))
        }

        return catMembers

    }

    @SuppressLint("SuspiciousIndentation")
    suspend fun getYoutubeLink(href: String):String {
        val doc = Utils.getJsoup("https://www.themoviedb.org/$href")
        println("https://www.themoviedb.org/$href")
        val playTrailerElement = doc.select("a.no_click.play_trailer")
        val dataId = playTrailerElement.attr("data-id")
            println("data-id: $dataId")
        return dataId
    }


}

fun main(args: Array<String>) {
    runBlocking {
        TrailerService().getCast("Squid Game")
    }
}
