package com.ip_tv.ipsat.presentation.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ip_tv.ipsat.databinding.ItemMovieBinding
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.utils.loadImage
import com.ip_tv.ipsat.utils.setAnimation
import com.ip_tv.ipsat.utils.setSafeOnClickListener
import java.io.Serializable
import kotlin.math.log


class ShowMoreItemAdapter(
    var type: Int,
    private val mediaList: ArrayList<Movie>,
    private val activity: FragmentActivity,
    private val matchParent: Boolean = false,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (type) {
            0 -> MediaViewHolder(
                ItemMovieBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )


            else -> throw IllegalArgumentException()
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (type) {
            0 -> {
                val b = (holder as MediaViewHolder).binding
                setAnimation(activity, b.root)

                val media = mediaList?.getOrNull(position)
                Log.d("TAG", "onBindViewHolder: ${media.toString()}")
                if (media != null) {
                    holder.binding.apply {
                        itemImg.loadImage(media.image )
                        titleItem.text = media.name
                    }
                }
            }

        }
    }

    override fun getItemCount() = mediaList!!.size

    override fun getItemViewType(position: Int): Int {
        return type
    }

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
//            val media = mediaList?.get(position)
//            ContextCompat.startActivity(
//                activity,
//                Intent(activity, DetailActivity::class.java).putExtra(
//                    "media",
//                    media as Serializable
//                ), null
//            )
        }
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