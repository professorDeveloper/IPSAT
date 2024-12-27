package com.ip_tv.ipsat.presentation.screens

import Resource
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.LayoutAnimationController
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ip_tv.ipsat.databinding.MovieVodScreenBinding
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.presentation.adapters.BannerAdapter
import com.ip_tv.ipsat.presentation.viewmodel.MovieViewModel
import com.ip_tv.ipsat.utils.BaseFragment
import com.ip_tv.ipsat.utils.MediaPageTransformer
import com.ip_tv.ipsat.utils.gone
import com.ip_tv.ipsat.utils.setSlideIn
import com.ip_tv.ipsat.utils.showSnack
import com.ip_tv.ipsat.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

@AndroidEntryPoint
class MovieVodScreen:BaseFragment<MovieVodScreenBinding>(MovieVodScreenBinding::inflate) {
    private val model by activityViewModels<MovieViewModel>()
    private var isBannerLoaded = false
    private var trendHandler: Handler? = null
    private lateinit var trendRun: Runnable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isBannerLoaded) {
            model.loadBanner()
        }    }
    override fun onViewCreate(savedInstanceState: Bundle?) {
        observeModel()
        requireActivity().window.statusBarColor = Color.parseColor("#25B8B8B8")

    }

   private fun observeModel(){
        lifecycleScope.launch {
            model.initBanner
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collect { handleBannerState(it) }
        }
    }
    private fun handleBannerState(state:Resource<ArrayList<Movie>>) {
        when(state){
            is Resource.Error -> {
                binding.progressBanner.gone()
                showSnack(
                    binding.root,
                    state.throwable.message.toString()
                )
            }
            is Resource.Loading -> {
                binding.bannerViewPager.gone()
                binding.progressBanner.visible()
            }
            is Resource.Success -> {
                isBannerLoaded = true
                binding.bannerViewPager.visible()
                binding.progressBanner.gone()
                binding.bannerViewPager.adapter = BannerAdapter(
                    mediaList = state.data,
                    activity=requireActivity()
                )
                trendHandler =Handler(Looper.getMainLooper())
                trendRun = Runnable {
                    binding.bannerViewPager.currentItem = binding.bannerViewPager.currentItem + 1
                }
                binding.bannerViewPager.registerOnPageChangeCallback(
                    object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            trendHandler!!.removeCallbacks(trendRun)
                            trendHandler!!.postDelayed(trendRun, 4000)
                        }
                    }
                )

                binding.bannerViewPager.setPageTransformer(MediaPageTransformer())
                binding.bannerViewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                binding.bannerViewPager.offscreenPageLimit = 3
                binding.bannerViewPager.layoutAnimation =
                    LayoutAnimationController(setSlideIn(), 0.50f)

            }
            else -> {}
        }
    }
}
