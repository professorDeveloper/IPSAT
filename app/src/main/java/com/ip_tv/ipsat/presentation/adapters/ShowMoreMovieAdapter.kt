//package com.ip_tv.ipsat.presentation.adapters
//
//import android.annotation.SuppressLint
//import android.text.TextWatcher
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import android.view.inputmethod.InputMethodManager
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.ip_tv.ipsat.databinding.ItemChipBinding
//import com.ip_tv.ipsat.databinding.ItemSearchHeaderBinding
//import com.ip_tv.ipsat.databinding.ShowMoreMoviesScreenBinding
//import com.ip_tv.ipsat.presentation.dialogs.SearchFilterBottomDialog
//import com.ip_tv.ipsat.presentation.screens.ShowMoreMoviesScreen
//import com.ip_tv.ipsat.utils.gone
//import com.ip_tv.ipsat.utils.preventTwoClick
//import com.ip_tv.ipsat.utils.visible
//
//
//class ShowMoreMovieAdapter(private val activity: ShowMoreMoviesScreen) :
//    RecyclerView.Adapter<ShowMoreMovieAdapter.SearchHeaderViewHolder>() {
//    private val itemViewType = 6969
//    var search: Runnable? = null
//    private var isGenereClicked = false
////    private var searchState = SearchState.EMPTY_SEARCH
//    var requestFocus: Runnable? = null
//    lateinit var binding2: ItemSearchHeaderBinding
//    lateinit var itemHistoryListener: (String) -> Unit
//
//
//    fun setItemHistoryListenerChip(itemHistoryListener: (String) -> Unit) {
//        this.itemHistoryListener = itemHistoryListener
//    }
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHeaderViewHolder {
//        val binding =
//            ItemSearchHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return SearchHeaderViewHolder(binding)
//    }
//
//
//    @SuppressLint("ClickableViewAccessibility")
//    override fun onBindViewHolder(holder: SearchHeaderViewHolder, position: Int) {
//        val binding = holder.binding
//        val imm: InputMethodManager = activity.requireActivity()
//            .getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
//
//
//
//        binding.searchChipRecycler.adapter = SearchChipAdapter(activity).also {
////            activity.updateChips = {
////                it.update()
////                isGenereClicked = true
////
////            }
////            it.setChipItemClickListener {
////            }
//        }
//        val chipLayoutManager =
//            LinearLayoutManager(binding.root.context, RecyclerView.HORIZONTAL, false)
//        binding.searchChipRecycler.layoutManager = chipLayoutManager
//        binding.searchChipRecycler.scrollToPosition(1)
//
//        binding.searchFilter.setOnClickListener {
//            val bottomSheetFragment = SearchFilterBottomDialog()
//            bottomSheetFragment.activity = activity
//            bottomSheetFragment.show(activity.childFragmentManager, "hash_tag")
//            it.preventTwoClick()
//        }
//
//
//
//
//    }
//
//
//
//
//
//    override fun getItemCount(): Int = 1
//
//    inner class SearchHeaderViewHolder(val binding: ItemSearchHeaderBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        init {
//
//            binding2 = binding
//        }
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return itemViewType
//    }
//
//
//    class SearchChipAdapter(val activity: ShowMoreMoviesScreen) :
//        RecyclerView.Adapter<SearchChipAdapter.SearchChipViewHolder>() {
//        lateinit var chipItemClickListener: () -> Unit
//
//        @JvmName("setChipItemClickListener1")
//        fun setChipItemClickListener(listener: () -> Unit) {
//            chipItemClickListener = listener
//        }
//
////        private var chips = activity.result.toChipList()
//
//        inner class SearchChipViewHolder(val binding: ItemChipBinding) :
//            RecyclerView.ViewHolder(binding.root)
//
//        override fun onCreateViewHolder(
//            parent: ViewGroup,
//            viewType: Int
//        ): SearchChipViewHolder {
//            val binding =
//                ItemChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//            return SearchChipViewHolder(binding)
//        }
//
//
//        override fun onBindViewHolder(holder: SearchChipViewHolder, position: Int) {
//////            val chip = chips[position]
////            holder.binding.root.apply {
////                text = chip.text
////                setOnClickListener {
////                    activity.result.removeChip(chip)
////                    update()
////                    chipItemClickListener.invoke()
//////                    activity.search()
////                }
//            }
//        }
//
////        @SuppressLint("NotifyDataSetChanged")
////        fun update() {
////
////            chips = activity.result.toChipList()
////            notifyDataSetChanged()
////        }
////
////        override fun getItemCount(): Int = chips.size
//    }
//}