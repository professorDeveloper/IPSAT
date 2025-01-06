package com.ip_tv.ipsat.presentation.dialogs

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ip_tv.ipsat.databinding.ChooseQualityBottomSheetBinding
import com.ip_tv.ipsat.databinding.ItemStreamBinding
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.domain.model.Urlobj
import com.ip_tv.ipsat.domain.model.VodMovieResponse
import kotlinx.coroutines.CoroutineScope

class ChooseSubtitleBottomSheet : BottomSheetDialogFragment() {
    private var _binding: ChooseQualityBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val vodMovieResponse: VodMovieResponse by lazy {
        arguments?.getSerializable("vod") as VodMovieResponse
    }
    private val movie : Movie by lazy {
        arguments?.getSerializable("movie") as Movie
    }
    private var scope: CoroutineScope = lifecycleScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ChooseQualityBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ExtractorAdapter()
        binding.selectorRecyclerView.adapter = adapter
        binding.selectorRecyclerView.setHasFixedSize(true)

        adapter.add(vodMovieResponse.urlobj.toMutableList())
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun startExoplayer(media: Movie) {

        dismiss()
//            stopAddingToList()
//            val intent = Intent(activity, ExoplayerView::class.java)
//            ExoplayerView.media = media
//            ExoplayerView.initialized = true
//            startActivity(intent)
    }



    private inner class ExtractorAdapter :
        RecyclerView.Adapter<ExtractorAdapter.StreamViewHolder>() {
        val links = mutableListOf<Urlobj>()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StreamViewHolder =
            StreamViewHolder(
                ItemStreamBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        override fun onBindViewHolder(holder: StreamViewHolder, position: Int) {
            val extractor = links[position]
            holder.binding.streamName.text = extractor.typeName

            holder.binding.urlQuality.text=extractor.hdtv
            holder.binding.urlSize.text =extractor.playflag.toString()

        }

        override fun getItemCount(): Int = links.size

        fun add(videoExtractor: MutableList<Urlobj>) {
            links.clear()
            links.addAll(videoExtractor)
            notifyDataSetChanged()
        }



        private inner class StreamViewHolder(val binding: ItemStreamBinding) :
            RecyclerView.ViewHolder(binding.root)
    }

    companion object {
        fun newInstance(
            server: Movie,
            vodMovieResponse: VodMovieResponse,
        ): ChooseQualityBottomSheet =
            ChooseQualityBottomSheet().apply {
                arguments = Bundle().apply {
                    putSerializable("vod", vodMovieResponse)
                    putSerializable("movie", server)
                }
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {}

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}