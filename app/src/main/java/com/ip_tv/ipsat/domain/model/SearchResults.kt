package com.ip_tv.ipsat.domain.model

import java.io.Serializable

data class SearchResults(
    val type: String,
    var isAdult: Boolean,
    var onList: Boolean? = null,
    var perPage: Int? = null,
    var search: String? = null,
    var rating: String? = null,
    var genres: MutableList<String>? = null,
    var excludedGenres: MutableList<String>? = null,
    var tags: MutableList<String>? = null,
    var excludedTags: MutableList<String>? = null,
    var format: String? = null,
    var releaseYear: Int? = null,
    var country: String? = null,
    var page: Int = 1,
    var results: ArrayList<Movie>,
    var hasNextPage: Boolean,
) : Serializable {
    fun toChipList(): List<SearchChip> {
        val list = mutableListOf<SearchChip>()
        rating?.let {
            list.add(SearchChip("rating", "Rating : $it"))
        }
        format?.let {
            list.add(SearchChip("FORMAT", "Format : $it"))
        }
        country?.let {
            list.add(SearchChip("country", it))
        }
        releaseYear?.let {
            list.add(SearchChip("releaseYear", it.toString()))
        }
        genres?.forEach {
            list.add(SearchChip("GENRE", it))
        }
        excludedGenres?.forEach {
            list.add(SearchChip("EXCLUDED_GENRE", "Not $it"))
        }
        tags?.forEach {
            list.add(SearchChip("TAG", it))
        }
        excludedTags?.forEach {
            list.add(SearchChip("EXCLUDED_TAG", "Not $it"))
        }
        return list
    }

    fun removeChip(chip: SearchChip) {
        when (chip.type) {
            "rating" -> rating = null
            "releaseYear" -> releaseYear = null
            "country" -> country = null
            "categoryProperty" -> tags?.remove(chip.text)
//            "EXCLUDED_TAG" -> excludedTags?.remove(chip.text)
        }
    }

    data class SearchChip(
        val type: String,
        val text: String
    )
}