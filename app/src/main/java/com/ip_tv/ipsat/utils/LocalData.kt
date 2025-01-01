package com.ip_tv.ipsat.utils

object LocalData {
    val tags: Map<Boolean,ArrayList<String>> = mapOf(
        false to arrayListOf("Comedy","Thriller","Adventure","Mystery","Crime","Biography","Family","Documentary","History","War","Science Fiction","Fantasy","Western","Animation",
            "Action","Horror","Drama","Romance","Adventure"),
    )
    val years = (1970 until 2025).map { it }.reversed().toMutableList()
    val country = arrayListOf("United States","United Kingdom","India","Germany","France","Spain","Italy","Japan","South Korea","Mexico","Russia","Turkey","Netherlands","Belgium","Switzerland",)
    val rating = (1..10).map { it.toString() }.toMutableList()

}