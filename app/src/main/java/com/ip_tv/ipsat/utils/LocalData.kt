package com.ip_tv.ipsat.utils

import com.ip_tv.ipsat.domain.model.AppUpdate
import com.ip_tv.ipsat.domain.model.EventModelItem
import com.ip_tv.ipsat.domain.model.SubCategoryItem

object LocalData {
    const val YOUTUBE_KEY = "AIzaSyChfRI0EqdYR6TSlZJ7gV6TWYynkn74e5w"
    const val EXTRA_REVEAL_CENTER_PADDING = 40
        var appUpdate: AppUpdate? = null
    val eventList = ArrayList<EventModelItem>()
    val trailerLink = "https://cdn.jwplayer.com/manifests/47AmxXWS.m3u8"
    var detailSeriesImage: String =
        "https://images.unsplash.com/photo-1520342868574-5fa3804e551c?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=6ff92caffcdd63681a35134a6770ed3b&auto=format&fit=crop&w=1951&q=80"
    val tags: Map<Boolean, ArrayList<String>> = mapOf(
        false to arrayListOf(
            "Comedy",
            "Thriller",
            "Adventure",
            "Mystery",
            "Crime",
            "Biography",
            "Family",
            "Documentary",
            "History",
            "War",
            "Science Fiction",
            "Fantasy",
            "Western",
            "Animation",
            "Action",
            "Horror",
            "Drama",
            "Romance",
            "Adventure"
        ),
    )

     lateinit var changeSearchResponse: (String) -> Unit
     lateinit var clearSearchResponse: () -> Unit
     lateinit var isDataHave: (Boolean) -> Unit
    var selectedCategory =ArrayList<SubCategoryItem>()
    fun setSearchResponseListener(listener: (String) -> Unit) {
        changeSearchResponse = listener
    }

    fun setDataHaveListener(listener: (Boolean) -> Unit) {
        isDataHave = listener
    }

    fun setClearSearchResponseListener(listener: () -> Unit) {
        clearSearchResponse = listener
    }




    val years = (1970 until 2025).map { it.toString() }.reversed().toMutableList()
    val country = arrayListOf(
        "United States",
        "United Kingdom",
        "India",
        "Germany",
        "France",
        "Spain",
        "Italy",
        "Japan",
        "South Korea",
        "Mexico",
        "Russia",
        "Turkey",
        "Netherlands",
        "Belgium",
        "Switzerland",
    )
    val rating = (1..10).map { it.toString() }.toMutableList()

}