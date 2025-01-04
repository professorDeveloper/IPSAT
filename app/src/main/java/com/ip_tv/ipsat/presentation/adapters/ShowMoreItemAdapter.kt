package com.ip_tv.ipsat.presentation.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.ItemMovieBinding
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.utils.animationTransaction
import com.ip_tv.ipsat.utils.loadImage
import com.ip_tv.ipsat.utils.setAnimation
import com.ip_tv.ipsat.utils.setSafeOnClickListener
import java.io.Serializable
import kotlin.math.log


class ShowMoreItemAdapter(
    private val activity: Fragment,
    private val matchParent: Boolean = false,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
     val mediaList: ArrayList<Movie> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  MediaViewHolder(
                ItemMovieBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )


    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

                val b = (holder as MediaViewHolder).binding
                setAnimation(activity.requireActivity(), b.root)

                val media = mediaList?.getOrNull(position)
                Log.d("TAG", "onBindViewHolder: ${media.toString()}")
                if (media != null) {
                    holder.binding.apply {
                        itemImg.loadImage(media.image )
                        titleItem.text = media.name
                        b.itemCompactScore.text = media.rating.toString()
                    }

                }
    }

    override fun getItemCount() = mediaList!!.size


    inner class MediaViewHolder(val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            if (matchParent) itemView.updateLayoutParams { width = -1 }
            itemView.setSafeOnClickListener { clicked(bindingAdapterPosition) }
            itemView.setOnLongClickListener { longClicked(bindingAdapterPosition) }
        }
    }


    fun clicked(position: Int) {
        if ((mediaList?.size ?: 0) > position && position != -1) {
            val bundle = Bundle()
            bundle.putSerializable("movie", mediaList.get(position))
            if (matchParent) {
                activity. findNavController().navigate(R.id.detailSeriesScreen,bundle, animationTransaction().build())
            }else {
                activity. findNavController().navigate(R.id.detailScreen,bundle, animationTransaction().build())
            }

        }
    }

    fun submitList(newMovies: List<Movie>) {
        val startPosition = mediaList.size
        mediaList.addAll(newMovies)
        notifyItemRangeInserted(startPosition, newMovies.size)
    }

    fun submitListNew(newMovies: List<Movie>) {
        mediaList.clear()
        mediaList.addAll(newMovies)
        notifyDataSetChanged()
    }

    fun longClicked(position: Int): Boolean {
        if ((mediaList?.size ?: 0) > position && position != -1) {
            val media = mediaList?.get(position) ?: return false
//            if (activity.supportFragmentManager.findFragmentByTag("list") == null) {
//                MediaListDialogSmallFragment.newInstance(media)
//                    .show(activity.supportFragmentManager, "list")
//                return true
//            }
        }
        return false
    }
}