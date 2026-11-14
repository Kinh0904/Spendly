package com.example.spendly.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spendly.databinding.FragmentHomeBinding
import com.example.spendly.utils.NavigationHelper
import com.example.spendly.MainActivity
import com.example.spendly.presentation.assistant.AssistantFragment
import com.example.spendly.presentation.category.CategoryFragment
import com.example.spendly.presentation.review.ExpenseReviewFragment
import com.example.spendly.presentation.transaction.add.BudgetFragment
import com.example.spendly.presentation.receipt.ReceiptFragment
import com.example.spendly.presentation.voice.VoiceExpenseFragment

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NavigationHelper.setupBottomToolbar(binding.root, NavigationHelper.Destination.HOME)
        binding.viewQuickaddSection.btnQuickAddMic.setOnClickListener { (activity as? MainActivity)?.navigateToFragment(VoiceExpenseFragment()) }
        binding.viewQuickaddSection.btnQuickAddPlus.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(requireContext()).setTitle("Thêm khoản chi")
                .setItems(arrayOf("Nhập thủ công", "Ghi bằng giọng nói", "Quét hóa đơn")) { _, which ->
                    (activity as? MainActivity)?.navigateToFragment(when(which) { 0 -> com.example.spendly.presentation.transaction.add.ExpenseEntryFragment(); 1 -> VoiceExpenseFragment(); else -> ReceiptFragment() })
                }.show()
        }
        binding.viewUtilitiesSection.cardBudget.setOnClickListener { (activity as? MainActivity)?.navigateToFragment(BudgetFragment()) }
        binding.viewUtilitiesSection.cardScanNotification.setOnClickListener { (activity as? MainActivity)?.navigateToFragment(ReceiptFragment()) }
        binding.viewUtilitiesSection.cardExpenseReport.setOnClickListener { (activity as? MainActivity)?.navigateToFragment(AssistantFragment()) }
        binding.viewUtilitiesSection.cardSettingsUtility.setOnClickListener { (activity as? MainActivity)?.navigateToFragment(CategoryFragment()) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
