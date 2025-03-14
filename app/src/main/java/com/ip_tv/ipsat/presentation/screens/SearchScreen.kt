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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.SearchScreenBinding
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.presentation.adapters.MovieAdapter
import com.ip_tv.ipsat.presentation.adapters.ShowMoreItemAdapter
import com.ip_tv.ipsat.presentation.viewmodel.SearchViewModel
import com.ip_tv.ipsat.utils.BaseFragment
import com.ip_tv.ipsat.utils.DialogUtils
import com.ip_tv.ipsat.utils.LocalData
import com.ip_tv.ipsat.utils.animationTransaction
import com.ip_tv.ipsat.utils.gone
import com.ip_tv.ipsat.utils.readData
import com.ip_tv.ipsat.utils.saveData
import com.ip_tv.ipsat.utils.showSnack
import com.ip_tv.ipsat.utils.visible
import com.kongzue.dialogx.dialogs.WaitDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchScreen : BaseFragment<SearchScreenBinding>(SearchScreenBinding::inflate) {
    private val model by viewModels<SearchViewModel>()
    private lateinit var adapter: MovieAdapter
    private var query = ""
    private var historyList = readData<ArrayList<String>>("history_search") ?: arrayListOf()
    override fun onViewCreate(savedInstanceState: Bundle?) {
        adapter = MovieAdapter(
            this
        )
        manageSearch()
        observeModel()
        model.searchResult.observe(viewLifecycleOwner) { observeModel(it) }
    }

    private fun observeModel(state: Resource<ArrayList<Movie>>) {
        when (state) {
            is Resource.Error -> {
                binding.progressBar.gone()
                DialogUtils.createTapadooDialog(
                    requireActivity(),
                    "Search Eerrorr",
                    state.throwable.message.toString(), R.color.map_red
                )
            }

            is Resource.Loading -> {
                binding.progressBar.visible()
                binding.movieListRv.gone()
            }

            is Resource.Success -> {
                if (state.data.isNotEmpty()) {
                    adapter.submitListNew(state.data)
                    adapter.setItemClickListener {
                        WaitDialog.setMessage("Loading..").show(requireActivity())
                        lifecycleScope.launch {
                            delay(300)
                            model.checkMovieSeries(it.id, it).apply {
                                WaitDialog.dismiss()
                                if (this) {
                                    val bundle = Bundle()
                                    bundle.putSerializable("movie", it)
                                    findNavController().navigate(
                                        R.id.detailSeriesScreen,
                                        bundle,
                                        animationTransaction().build()
                                    )
                                } else {
                                    val bundle = Bundle()
                                    bundle.putSerializable("movie", it)
                                    findNavController().navigate(R.id.detailScreen, bundle)
                                }
                            }
                        }
                    }
                    binding.movieListRv.visible()
                    binding.movieListRv.adapter = adapter
                    binding.progressBar.gone()
                    if (!historyList.contains(query)) {
                        historyList.add(query)
                    }
                } else {
                    binding.progressBar.gone()
                    DialogUtils.createTapadooDialog(
                        requireActivity(),
                        "Search Error",
                        "Search result not found",
                        R.color.map_red
                    )
                }
            }

            else -> {}
        }
    }

    private fun observeModel() {
        lifecycleScope.launch {
        }
    }

    private var searchJob: Job? = null

    private fun manageSearch() {
        binding.searchBar.isSuggestionsEnabled = true
        binding.searchBar.lastSuggestions = historyList
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