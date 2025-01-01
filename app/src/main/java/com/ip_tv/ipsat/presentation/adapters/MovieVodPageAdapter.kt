package com.ip_tv.ipsat.presentation.adapters

import Resource
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LayoutAnimationController
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ip_tv.ipsat.databinding.ItemMoviePageBinding
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.utils.BaseFragment
import com.ip_tv.ipsat.utils.MediaPageTransformer
import com.ip_tv.ipsat.utils.gone
import com.ip_tv.ipsat.utils.invisible
import com.ip_tv.ipsat.utils.setAnimation
import com.ip_tv.ipsat.utils.setSlideIn
import com.ip_tv.ipsat.utils.setSlideUp
import com.ip_tv.ipsat.utils.showSnack
import com.ip_tv.ipsat.utils.visible

class MovieVodPageAdapter(
    private val fragment: BaseFragment<*>,
    private val binding: ItemMoviePageBinding,
) : RecyclerView.Adapter<MovieVodPageAdapter.MovieVodPageViewHolder>() {
    private val ready = MutableLiveData(false)
    private var trendHandler: Handler? = null
    private lateinit var trendRun: Runnable
    private var trendingViewPager: ViewPager2? = null

    private lateinit var moviesShowMoreClick: (Int) -> Unit
    private lateinit var kidsShowMoreClick: (Int) -> Unit
    private lateinit var seriesShowMoreClick: (Int) -> Unit
    private lateinit var documentaryShowMoreClick: (Int) -> Unit
    private lateinit var searchIconClick:() ->Unit

    fun setMoviesShowMoreClick(moviesShowMoreClick: (Int) -> Unit) {
        this.moviesShowMoreClick = moviesShowMoreClick
    }

    fun setKidsShowMoreClick(moviesShowMoreClick: (Int) -> Unit) {
        this.kidsShowMoreClick = moviesShowMoreClick
    }
    fun setSeriesShowMoreClick(moviesShowMoreClick: (Int) -> Unit) {
        this.seriesShowMoreClick = moviesShowMoreClick
    }
    fun setDocumentaryShowMoreClick(moviesShowMoreClick: (Int) -> Unit) {
        this.documentaryShowMoreClick = moviesShowMoreClick
    }

    fun setSearchIconClick(searchIconClick:() ->Unit){
        this.searchIconClick = searchIconClick
    }

    inner class MovieVodPageViewHolder(var binding: ItemMoviePageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieVodPageViewHolder {
        return MovieVodPageViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: MovieVodPageViewHolder, position: Int) {
        holder.binding.apply {
            trendingViewPager = bannerViewPager
            setAnimation(
                root.context,
                root,
                150,
                floatArrayOf(0.0f, 1.0f, 0.0f, 1.0f),
                pivot = 0.5f to 0.5f
            )
            if (ready.value == false)
                ready.postValue(true)
        }
    }

    fun updateRecent(adaptor: MovieAdapter) {
        binding.animeUpdatedRecyclerView.adapter = adaptor
        binding.animeUpdatedRecyclerView.layoutManager =
            LinearLayoutManager(
                binding.animeUpdatedRecyclerView.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        binding.animeUpdatedRecyclerView.visibility = View.VISIBLE
        binding.recentlyMore.visible()

        binding.animeRecently.visibility = View.VISIBLE
        binding.animeRecently.startAnimation(setSlideUp())
        binding.animeUpdatedRecyclerView.layoutAnimation =
            LayoutAnimationController(setSlideUp(), 0.25f)
        binding.animeRecently.visibility = View.VISIBLE
        binding.moviesShowMore.setOnClickListener {
            moviesShowMoreClick.invoke(adaptor.itemCount)
        }
        binding.kidsShowMore.setOnClickListener {
            kidsShowMoreClick.invoke(adaptor.itemCount)
        }
        binding.seriesShowMore.setOnClickListener {
            seriesShowMoreClick.invoke(adaptor.itemCount)
        }
        binding.documentaryShowMore.setOnClickListener {
            documentaryShowMoreClick.invoke(adaptor.itemCount)
        }
        binding.searchIcon.setOnClickListener {
            searchIconClick.invoke()
        }
    }

    fun updateKids(adaptor: MovieAdapter) {
        binding.kidsRecyclerView.adapter = adaptor
        binding.kidsRecyclerView.layoutManager =
            LinearLayoutManager(
                binding.kidsRecyclerView.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        binding.kidsRecyclerView.visibility = View.VISIBLE
        binding.kidsMore.visible()
        binding.kidsRecyclerView.layoutAnimation =
            LayoutAnimationController(setSlideIn(), 0.25f)
        binding.kidsRecyclerView.startAnimation(setSlideUp())
        binding.kidsTxt.visible()
        binding.kidsMore.visible()
    }

    fun updateSeries(adaptor: MovieAdapter) {
        binding.animeTopStarsRecyclerView.adapter = adaptor
        binding.topSeriesMore.visible()
        binding.animeTopStarsRecyclerView.layoutManager =
            LinearLayoutManager(
                binding.animeTopStarsRecyclerView.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        binding.animeTopStarsRecyclerView.visibility = View.VISIBLE
        binding.animeTopStarsRecyclerView.layoutAnimation =
            LayoutAnimationController(setSlideIn(), 0.25f)
        binding.animeUpdatedRecyclerView.startAnimation(setSlideUp())
        binding.animeTopStars.visible()
    }

    fun handleBannerState(state: Resource<ArrayList<Movie>>) {
        when (state) {
            is Resource.Error -> {
                binding.animeTrendingProgressBar.invisible()
                fragment.showSnack(
                    binding.root,
                    state.throwable.message.toString()
                )
            }

            is Resource.Loading -> {
                binding.animeTrendingProgressBar.visible()
                binding.bannerViewPager.invisible()
            }

            is Resource.Success -> {
                binding.animeTrendingProgressBar.invisible()
                binding.bannerViewPager.visible()
                BannerAdapter(
                    mediaList = state.data,
                    activity = fragment.requireActivity()
                ).also { binding.bannerViewPager.adapter = it

                    it.setItemClickListener {

                    }
                    it.setPlayItemListener {

                    }

                }

                trendHandler = Handler(Looper.getMainLooper())
                trendRun = Runnable {
                    binding.bannerViewPager.currentItem += 1
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
                binding.bannerViewPager.getChildAt(0).overScrollMode =
                    RecyclerView.OVER_SCROLL_NEVER
                binding.bannerViewPager.offscreenPageLimit = 3
                binding.bannerViewPager.layoutAnimation =
                    LayoutAnimationController(setSlideIn(), 0.50f)

            }

            else -> {}
        }
    }
    fun  updatePopularVertical(){
        binding.movieVertical.visible()
    }

    fun updateDocumentary(adaptor: MovieAdapter) {
        binding.documentaryTxt.visible()
        binding.documentaryMore.visible()
        binding.documentaryRecyclerView.adapter = adaptor
        binding.documentaryRecyclerView.layoutManager =
            LinearLayoutManager(
                binding.documentaryRecyclerView.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        binding.documentaryRecyclerView.visibility = View.VISIBLE
        binding.documentaryRecyclerView.layoutAnimation =
            LayoutAnimationController(setSlideIn(), 0.25f)
        binding.documentaryRecyclerView.startAnimation(setSlideUp())
    }

}