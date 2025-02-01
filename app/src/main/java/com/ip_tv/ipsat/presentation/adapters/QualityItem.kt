package com.ip_tv.ipsat.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

data class QualityItem(val typeName: String, val playUrl: String)

class QualityAdapter(
    private val qualities: List<QualityItem>,
    private var selectedIndex: Int, // Set default selected quality
    private val onQualitySelected: (QualityItem,Int) -> Unit
) : RecyclerView.Adapter<QualityAdapter.QualityViewHolder>() {

    inner class QualityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val qualityText: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(qualityItem: QualityItem, position: Int) {
            qualityText.text = qualityItem.typeName

            // Highlight selected quality
            if (position == selectedIndex) {
                qualityText.setBackgroundColor(
                    ContextCompat.getColor(itemView.context, android.R.color.holo_blue_light)
                )
            } else {
                qualityText.setBackgroundColor(
                    ContextCompat.getColor(itemView.context, android.R.color.transparent)
                )
            }

            itemView.setOnClickListener {
                selectedIndex = position
                onQualitySelected(qualityItem,absoluteAdapterPosition)
                notifyDataSetChanged() // Update selection UI
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QualityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            android.R.layout.simple_list_item_1, parent, false
        )
        return QualityViewHolder(view)
    }

    override fun onBindViewHolder(holder: QualityViewHolder, position: Int) {
        holder.bind(qualities[position], position)
    }

    override fun getItemCount() = qualities.size
}
