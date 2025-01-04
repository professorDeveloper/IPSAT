package com.ip_tv.ipsat.presentation.adapters

import Resource
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LayoutAnimationController
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.ItemMoviePageBinding
import com.ip_tv.ipsat.databinding.SeriesDetailItemBinding
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.domain.model.SeriesDetailResponse
import com.ip_tv.ipsat.utils.BaseFragment
import com.ip_tv.ipsat.utils.MediaPageTransformer
import com.ip_tv.ipsat.utils.animationTransaction
import com.ip_tv.ipsat.utils.gone
import com.ip_tv.ipsat.utils.invisible
import com.ip_tv.ipsat.utils.loadImage
import com.ip_tv.ipsat.utils.setAnimation
import com.ip_tv.ipsat.utils.setSlideIn
import com.ip_tv.ipsat.utils.setSlideUp
import com.ip_tv.ipsat.utils.showSnack
import com.ip_tv.ipsat.utils.visible

class SeriesDetailPageAdapter(
    private val fragment: BaseFragment<*>,
    private val binding: SeriesDetailItemBinding,
) : RecyclerView.Adapter<SeriesDetailPageAdapter.SeriesPageViewHolder>() {
    fun setSubTitleClick(moviesShowMoreClick: (Int) -> Unit) {
//        this.seriesShowMoreClick = moviesShowMoreClick
    }


    inner class SeriesPageViewHolder(var binding: SeriesDetailItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesPageViewHolder {
        return SeriesPageViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return 1
    }



    override fun onBindViewHolder(holder: SeriesPageViewHolder, position: Int) {
        holder.binding.apply {

        }
    }

    fun  manageUI(data:SeriesDetailResponse,movie:Movie) {
        binding.tvMovieTitleValue.text = movie.name
        binding.tvVoteAverage.text=movie.rating.toString()
        binding.ivPoster.loadImage(movie.image)
        binding.yearValue.text = data.releaseYear
        binding.durationValue.text = data.language
        binding.ivBackdrop.loadImage(data.horizontalPoster)
        binding.tvGnreValue.text =
            "Property:" + data.property + "  Director: " + data.director


        binding.tvDescriptionTitle.text = "Episodes ${data.seriesList.totalNum} Count"
        if (data.subtitleList.isNotEmpty()) {
            binding.mainContainer.visible()
            binding.epTextView.text = data.subtitleList.get(0).language
            binding.epCard.setOnClickListener {

            }
        } else {
            binding.mainContainer.gone()
        }
    }



}