package com.ip_tv.ipsat.presentation.screens

import Resource
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.SearchScreenBinding
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.presentation.adapters.ShowMoreItemAdapter
import com.ip_tv.ipsat.presentation.viewmodel.SearchViewModel
import com.ip_tv.ipsat.utils.BaseFragment
import com.ip_tv.ipsat.utils.gone
import com.ip_tv.ipsat.utils.readData
import com.ip_tv.ipsat.utils.saveData
import com.ip_tv.ipsat.utils.showSnack
import com.ip_tv.ipsat.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchScreen : BaseFragment<SearchScreenBinding>(SearchScreenBinding::inflate) {
    private val model by viewModels<SearchViewModel>()
    private lateinit var adapter: ShowMoreItemAdapter
    private var query = ""
    private var historyList = readData<ArrayList<String>>("history_search") ?: arrayListOf()
    override fun onViewCreate(savedInstanceState: Bundle?) {
        adapter = ShowMoreItemAdapter(
            requireActivity()
        )
        manageSearch()
        model.searchResult.observe(this) { observeModel(it) }
    }

    private fun observeModel(state: Resource<ArrayList<Movie>>) {
        when (state) {
            is Resource.Error -> {
                binding.progressBar.gone()
                showSnack(binding.root, state.throwable.message.toString())
            }

            is Resource.Loading -> {
                binding.progressBar.visible()
                binding.movieListRv.gone()
            }

            is Resource.Success -> {
                adapter.submitListNew(state.data)
                binding.movieListRv.visible()
                binding.movieListRv.adapter = adapter
                binding.progressBar.gone()
                if (!historyList.contains(query)) {
                    historyList.add(query)
                }
            }

            else -> {}
        }
    }

    private var searchJob: Job? = null

    private fun manageSearch() {
        binding.searchBar.isSuggestionsEnabled =true
        binding.searchBar.lastSuggestions =historyList
        binding.searchBar.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                binding.searchBar.hideSuggestionsList()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()
                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    if (!s.isNullOrEmpty()) {
                        query = s.toString()
                        model.getSearchResult(s.toString())
                    }
                }

            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    override fun onPause() {
        super.onPause()
        saveData("history_search", historyList)
    }

    override fun onDestroy() {
        super.onDestroy()
        saveData("history_search", historyList)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        saveData("history_search", historyList)
    }
}