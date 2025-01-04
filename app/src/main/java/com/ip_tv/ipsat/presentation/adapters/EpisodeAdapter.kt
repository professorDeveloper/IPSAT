package com.ip_tv.ipsat.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ip_tv.ipsat.databinding.ItemEpisodeBinding
import com.ip_tv.ipsat.domain.model.Item0
import com.ip_tv.ipsat.utils.LocalData
import com.ip_tv.ipsat.utils.loadImage
import com.ip_tv.ipsat.utils.setAnimation

class EpisodeAdapter(private val activity: Fragment) :RecyclerView.Adapter<EpisodeAdapter.EpisodeVh>() {
    private var list =ArrayList<Item0>()
    inner class EpisodeVh(private var binding:ItemEpisodeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: Item0) {
            binding.apply {
                setAnimation(activity.requireActivity(), binding.root)
                itemEpisodeNumber.text =item.displayNumber
                itemEpisodeTitle.text=item.name
                itemEpisodeImage.loadImage(LocalData.detailSeriesImage)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeVh {
        return EpisodeVh(ItemEpisodeBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun submitList(newList:List<Item0>){
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: EpisodeVh, position: Int) {
        holder.onBind(list[position])
    }
}