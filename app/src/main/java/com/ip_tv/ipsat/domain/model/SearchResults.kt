package com.ip_tv.ipsat.domain.model

import java.io.Serializable

data class SearchResults(
    var search: String? = null,
    var rating: String? = null,
    var genres: MutableList<String>? = null,
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
        country?.let {
            list.add(SearchChip("country", it))
        }
        releaseYear?.let {
            list.add(SearchChip("releaseYear", it.toString()))
        }
        genres?.forEach {
            list.add(SearchChip("GENRE", it))
        }

        return list
    }

    fun removeChip(chip: SearchChip) {
        when (chip.type) {
            "rating" -> rating = null
            "releaseYear" -> releaseYear = null
            "country" -> country = null
            "categoryProperty" -> genres?.remove(chip.text)
//            "EXCLUDED_TAG" -> excludedTags?.remove(chip.text)
        }
    }

    data class SearchChip(
        val type: String,
        val text: String
    )
}