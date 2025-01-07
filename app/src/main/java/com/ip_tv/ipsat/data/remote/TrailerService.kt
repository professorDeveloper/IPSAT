package com.ip_tv.ipsat.data.remote

import android.annotation.SuppressLint
import com.ip_tv.ipsat.utils.Utils

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

/*
fun main(args: Array<String>) {
    runBlocking {
        TrailerService().findMovie("Squid Game")
    }
}
*/
