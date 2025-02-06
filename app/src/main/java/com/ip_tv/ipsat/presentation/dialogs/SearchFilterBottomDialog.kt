package com.ip_tv.ipsat.presentation.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.ip_tv.ipsat.app.App
import com.ip_tv.ipsat.databinding.SearchFilterBottomSheetBinding
import com.ip_tv.ipsat.utils.LocalData
class FilterBottomSheetDialog(context: Context) : BottomSheetDialogFragment() {

    private lateinit var binding: SearchFilterBottomSheetBinding
    private var selectedCountry: String? = null
    private var selectedYear: String? = null
    private val selectedCategories = mutableListOf<String>()

    var onFiltersApplied: ((String?, String?, List<String>) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = SearchFilterBottomSheetBinding.inflate(inflater, container, false)

        selectedCountry = arguments?.getString("selectedCountry")
        selectedYear = arguments?.getString("selectedYear")
        selectedCategories.addAll(arguments?.getStringArrayList("selectedCategories") ?: emptyList())

        setupSingleSelectionChipGroup(binding.countryChipGroup, LocalData.country) { selected ->
            selectedCountry = selected
        }

        setupSingleSelectionChipGroup(binding.yearsChipGroup, LocalData.years) { selected ->
            selectedYear = selected
        }

        setupMultiSelectionChipGroup(binding.categoriesChipGroup, LocalData.tags[false]!!)

        binding.searchFilterApply.setOnClickListener {
            onFiltersApplied?.invoke(selectedCountry, selectedYear, selectedCategories)
            dismiss()
        }

        binding.searchFilterCancel.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    private fun setupSingleSelectionChipGroup(
        chipGroup: ChipGroup,
        items: List<String>,
        onItemSelected: (String?) -> Unit
    ) {
        chipGroup.isSingleSelection = true
        items.forEach { item ->
            val chip = Chip(context).apply {
                text = item
                isCheckable = true
                // Ensure the chip is checked if it matches the selected value
                isChecked = item == selectedCountry || item == selectedYear
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        onItemSelected(item)
                    }else {
                        onItemSelected(null)
                    }
                }
            }
            chipGroup.addView(chip)
        }
    }

    private fun setupMultiSelectionChipGroup(chipGroup: ChipGroup, items: List<String>) {
        items.forEach { item ->
            val chip = Chip(context).apply {
                text = item
                isCheckable = true
                isChecked = selectedCategories.contains(item) // Restore selected state for categories
                setOnCheckedChangeListener { button, isChecked ->
                    if (isChecked) {
                        selectedCategories.add(button.text.toString()) // Add to selectedCategories when checked
                    } else {
                        selectedCategories.remove(button.text.toString()) // Remove from selectedCategories when unchecked
                    }
                }
            }
            chipGroup.addView(chip)
        }
    }

    companion object {
        fun newInstance(
            selectedCountry: String?, selectedYear: String?, selectedCategories: List<String>?
        ): FilterBottomSheetDialog {
            val dialog = FilterBottomSheetDialog(
                requireNotNull(App.Companion.instance)
            )
            val args = Bundle()
            args.putString("selectedCountry", selectedCountry)
            args.putString("selectedYear", selectedYear)
            args.putStringArrayList("selectedCategories", ArrayList(selectedCategories))
            dialog.arguments = args
            return dialog
        }
    }
}

