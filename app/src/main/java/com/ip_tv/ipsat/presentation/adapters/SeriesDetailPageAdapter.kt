package com.ip_tv.ipsat.presentation.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ip_tv.ipsat.databinding.SeriesDetailItemBinding
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.domain.model.SeriesDetailResponse
import com.ip_tv.ipsat.utils.BaseFragment
import com.ip_tv.ipsat.utils.loadImage

class SeriesDetailPageAdapter(
    private val fragment: BaseFragment<*>,
    private val binding: SeriesDetailItemBinding,
) : RecyclerView.Adapter<SeriesDetailPageAdapter.SeriesPageViewHolder>() {
    private lateinit var seriesShowMoreClick: (Int) -> Unit
    fun setSubTitleClick(moviesShowMoreClick: (Int) -> Unit) {
        this.seriesShowMoreClick = moviesShowMoreClick
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
            binding.epCard.setOnClickListener {
                seriesShowMoreClick.invoke(-1)
            }
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
//        if (data.subtitleList.isNotEmpty()) {
//            binding.mainContainer.visible()
//            binding.epCard.visible()
//            binding.epTextView.text = data.subtitleList.get(0).language
//            binding.epCard.setOnClickListener {
//
//            }
//        } else {
//            binding.mainContainer.gone()
//        }
    }



}