package com.ip_tv.ipsat.presentation.screens.show_more

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.ShowMoreDocumentaryScreenBinding
import com.ip_tv.ipsat.databinding.ShowMoreMoviesScreenBinding
import com.ip_tv.ipsat.domain.model.SearchResults
import com.ip_tv.ipsat.presentation.adapters.ShowMoreItemAdapter
import com.ip_tv.ipsat.presentation.dialogs.FilterBottomSheetDialog
import com.ip_tv.ipsat.presentation.viewmodel.ShowMoreDocumentaryViewModel
import com.ip_tv.ipsat.presentation.viewmodel.ShowMoreMovieViewModel
import com.ip_tv.ipsat.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
@AndroidEntryPoint
class ShowMoreDocumentaryScreen :   BaseFragment<ShowMoreDocumentaryScreenBinding>(
    ShowMoreDocumentaryScreenBinding::inflate) {
    private val scope = lifecycleScope
    var lastSearchedText = ""
    private var screenWidth: Float = 0f
    private lateinit var mediaAdapter: ShowMoreItemAdapter
    private var selectedCountry: String? = null
    private var selectedYear: String? = null
    private var default= "All"
    private var isDefaultCategory=false
    private var isDefaultCountry=false
    private var isDefaultYear=false

    private var selectedCategories: MutableList<String> = mutableListOf()

    val model: ShowMoreDocumentaryViewModel by viewModels()

    override fun onViewCreate(savedInstanceState: Bundle?) {
        if (model.notSet) {
            model.notSet = false
            model.searchResults = SearchResults(
                null,
                null,
                null,
                2024,
                null,
                1,
                arrayListOf(),
                true
            )
        }
        model.loadSearch(model.searchResults)
        mediaAdapter = ShowMoreItemAdapter(this,)

        binding.moviesShowMoreRv.adapter = ConcatAdapter(
            mediaAdapter,
        )
        mediaAdapter.setItemClickListener {
            val bundle = Bundle()
            bundle.putSerializable("movie", it)
            findNavController().navigate(R.id.detailScreen, bundle)
        }
        model.loadSearch(model.searchResults)
        model.result.observe(this ){
            model.searchResults?.apply {
                results =it?.results!!
                hasNextPage = it.hasNextPage
            }
            mediaAdapter.submitListNew(it!!.results)

        }
        model.nextPageResult.observe(this) {
            model.searchResults?.apply {
                results =it?.results!!
                page=it.page
                hasNextPage = it.hasNextPage
            }
            mediaAdapter.submitList(it!!.results)
        }

        binding.searchFilter.setOnClickListener {
            val filterDialog = FilterBottomSheetDialog.newInstance(
                selectedCountry, selectedYear, selectedCategories
            )
            filterDialog.onFiltersApplied = { country, year, categories ->
                model.searchResults.page =1
                if (country==null) {
                    isDefaultCountry=true
                    selectedCountry=country
                }else {
                    isDefaultCountry=false
                    selectedCountry = country
                }
                if (year==null) {
                    isDefaultYear=true
                    selectedYear=year
                }else {
                    isDefaultYear=false
                    selectedYear = year
                }
                if (categories.isEmpty()) {
                    isDefaultCategory=true
                    selectedCategories= arrayListOf()
                }else {
                    isDefaultCategory=false
                    selectedCategories = categories.toMutableList()
                }
                model.searchResults.country =if (isDefaultCountry) default else selectedCountry
                model.searchResults.releaseYear =if (isDefaultYear) (-1).toInt() else selectedYear!!.toInt()
                model.searchResults.genres =if (isDefaultCategory) arrayListOf() else categories.toMutableList()
                model.loadSearch(model.searchResults)
            }
            filterDialog.show(parentFragmentManager, "FilterDialog")
        }
        binding.moviesShowMoreRv.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(v: RecyclerView, dx: Int, dy: Int) {
                if (!v.canScrollVertically(1)) {
                    if (model.searchResults.hasNextPage && model.searchResults.results.isNotEmpty()) {
                        scope.launch(Dispatchers.IO) {
                            model.loadNextPage(model.searchResults)
                        }
                    }
                }
                super.onScrolled(v, dx, dy)
            }
        })

    }


}
