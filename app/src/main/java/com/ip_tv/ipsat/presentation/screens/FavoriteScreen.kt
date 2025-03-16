package com.ip_tv.ipsat.presentation.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.FavoriteScreenBinding
import com.ip_tv.ipsat.presentation.adapters.MovieAdapter
import com.ip_tv.ipsat.presentation.viewmodel.FavoriteViewModel
import com.ip_tv.ipsat.utils.animationTransaction
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
            binding.favoriteRv.adapter = MovieAdapter(this).apply {
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

        }
    }
}