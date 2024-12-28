package com.ip_tv.ipsat.presentation.adapters

import android.annotation.SuppressLint
import android.content.Context

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.RequestOptions
import com.flaviofaria.kenburnsview.RandomTransitionGenerator
import com.ip_tv.ipsat.databinding.ItemBannerBinding
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.utils.loadImage
import jp.wasabeef.glide.transformations.BlurTransformation

class BannerAdapter(
    private val mediaList: ArrayList<Movie>,
    private val activity: FragmentActivity,
    private val matchParent: Boolean = false,
    private val viewPager: ViewPager2? = null,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var clickListener: ((Movie) -> Unit)

    fun setItemClickListener(listener: ((Movie) -> Unit)) {
        clickListener = listener
    }

    lateinit var playListener: ((Movie) -> Unit)

    fun setPlayItemListener(listener: ((Movie) -> Unit)) {
        playListener = listener
    }

    lateinit var itemInfoListener: ((Movie) -> Unit)

    fun setViewInfoListener(listener: ((Movie) -> Unit)) {
        itemInfoListener = listener
    }


    @SuppressLint("ClickableViewAccessibility")
    inner class MediaPageSmallViewHolder(val binding: ItemBannerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {

//            itemView.setOnTouchListener { _, _ -> true }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MediaPageSmallViewHolder(
            ItemBannerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val b = (holder as MediaPageSmallViewHolder).binding
        val media = mediaList?.get(position)
        b.root.setOnClickListener {
                clickListener.invoke(media!!)
        }
        val banner =b.itemCompactBanner
        if (media != null) {
            banner.setTransitionGenerator(
                    RandomTransitionGenerator(
                        (10000 + 15000 * 700).toLong(),
                        AccelerateDecelerateInterpolator()
                    )
                )
            val context = banner.context
            if (!(activity).isDestroyed)
                Glide.with(context as Context)
                    .load(GlideUrl(media.image ))
                    .diskCacheStrategy(DiskCacheStrategy.ALL).override(400)
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(2, 3)))
                    .into(banner)

            b.itemCompactImage.loadImage(media.image)
            b.title.text = media.name
            b.itemCompactScore.text = media.rating.toString()
            b.itemDescription .text = media.language + " • " + media.release_year + " • " + media.country

            @SuppressLint("NotifyDataSetChanged")
            if (position == mediaList!!.size - 2 && viewPager != null) viewPager.post {
                val size = mediaList.size
                mediaList.addAll(mediaList)
                notifyItemRangeInserted(size - 1, mediaList.size)
            }
        }

    }

    fun longClicked(position: Int): Boolean {
//        if (mediaList!!.size > position && position != -1) {
//            val media = mediaList!!.get(position)
//            if (activity.supportFragmentManager.findFragmentByTag("list") == null) {
//                MediaListDialogSmallFragment.newInstance(media)
//                    .show(activity.supportFragmentManager, "list")
//                return true
//            }
//        }
        return false
    }

    override fun getItemCount(): Int {
        return mediaList!!.size
    }

}
