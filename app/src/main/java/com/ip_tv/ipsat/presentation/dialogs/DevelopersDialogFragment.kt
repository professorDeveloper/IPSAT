package com.ip_tv.ipsat.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ip_tv.ipsat.databinding.BottomSheetDevelopersBinding
import com.ip_tv.ipsat.databinding.ItemDeveloperBinding
import com.ip_tv.ipsat.domain.model.Developer
import com.ip_tv.ipsat.utils.loadImage
import com.ip_tv.ipsat.utils.openLinkInBrowser
import com.ip_tv.ipsat.utils.setAnimation

class DevelopersDialogFragment : BottomSheetDialogFragment() {
    private var _binding: BottomSheetDevelopersBinding? = null
    private val binding get() = _binding!!

    private val developers = arrayOf(
        Developer(
            "Brahmi mounir",
            "https://static.vecteezy.com/system/resources/thumbnails/025/337/669/small_2x/default-male-avatar-profile-icon-social-media-chatting-online-user-free-vector.jpg",
            "CEO Owner",
            "https://t.me/+213540460497"
        ),
        Developer(
            "Azamov X",
            "https://github.com/professorDeveloper/Scraping-Tutorial/assets/108933534/b7c85044-3c9c-4d2f-8146-529e380ca3e9",
            "Developer",
            "https://t.me/stc_android"
        ),


        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetDevelopersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.devsRecyclerView.adapter = DevelopersAdapter(developers, this)
        binding.devsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}


class DevelopersAdapter(private val developers: Array<Developer>, private val activity: Fragment) :
    RecyclerView.Adapter<DevelopersAdapter.DeveloperViewHolder>() {

    inner class DeveloperViewHolder(val binding: ItemDeveloperBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeveloperViewHolder {
        return DeveloperViewHolder(
            ItemDeveloperBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DeveloperViewHolder, position: Int) {
        val b = holder.binding
        setAnimation(b.root.context, b.root)
        val dev = developers[position]
        b.devName.text = dev.name
        b.devProfile.loadImage(dev.pfp)
        b.devRole.text = dev.role
        b.root.setOnClickListener {
            openLinkInBrowser(developers[position].url, activity.requireActivity())
        }
    }

    override fun getItemCount(): Int = developers.size
}