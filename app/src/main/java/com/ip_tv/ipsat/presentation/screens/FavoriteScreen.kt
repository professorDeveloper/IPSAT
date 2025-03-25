package com.ip_tv.ipsat.presentation.screens

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.FavoriteScreenBinding
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.presentation.adapters.MovieAdapter
import com.ip_tv.ipsat.presentation.viewmodel.FavoriteViewModel
import com.ip_tv.ipsat.utils.animationTransaction
import com.ip_tv.ipsat.utils.showSnack
import com.kongzue.dialogx.dialogs.WaitDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

@AndroidEntryPoint
class FavoriteScreen : Fragment() {
    private var _binding: FavoriteScreenBinding? = null
    private val binding get() = _binding!!
    private val model by viewModels<FavoriteViewModel>()
    private lateinit var adapter: MovieAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FavoriteScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.getFavoriteMovies()
        model.favoriteMovieResponse.observe(viewLifecycleOwner) {
            adapter = MovieAdapter(this).apply {
                submitListNew(it)
                setItemClickListener {
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

            }
            binding.favoriteRv.adapter = adapter
            binding.materialAppBar.setOnMenuItemClickListener { menu ->
                when (menu.itemId) {
                    R.id.action_sort -> {
                        showSortPopup(binding.materialAppBar!!, it)
                    }
                }
                true
            }

        }
    }

    private fun showSortPopup(anchor: View, list: ArrayList<Movie>) {
        val wrapper = ContextThemeWrapper(
            requireContext(),
            com.google.android.material.R.style.Widget_Material3_PopupMenu
        )
        val popupMenu = PopupMenu(wrapper, anchor, Gravity.END)
        popupMenu.menuInflater.inflate(R.menu.menu_sort, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.sort_by_name -> {
                    val newList = filterByName(list)
                    adapter.submitListNew(newList)
                    true
                }

                R.id.sort_by_date -> {
                    val newList = filterByDate(list)
                    adapter.submitListNew(newList)
                    true
                }

                R.id.sort_by_rating -> {
                    val newList = filterByRating(list)
                    adapter.submitListNew(newList)

                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }
}

fun filterByName(arrayList: ArrayList<Movie>): List<Movie> {
    return arrayList.sortedByDescending { it.name.toLowerCase() }
}

fun filterByDate(arrayList: ArrayList<Movie>): List<Movie> {
    return arrayList.sortedByDescending { it.release_year }
}

fun filterByRating(arrayList: ArrayList<Movie>): List<Movie> {
    return arrayList.sortedByDescending { it.rating }
}