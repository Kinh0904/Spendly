package com.example.spendly.presentation.transaction.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.spendly.MainActivity
import com.example.spendly.databinding.FragmentBudgetBinding
import com.example.spendly.presentation.category.CategoryStore
import com.example.spendly.utils.NavigationHelper

class BudgetFragment : Fragment() {
    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!
    private val limits = mutableMapOf("Ăn uống" to 2_000_000, "Mua sắm" to 800_000, "Hóa đơn" to 1_000_000, "Đi lại" to 700_000, "Giải trí" to 500_000)
    private val spent = mapOf("Ăn uống" to 1_800_000, "Mua sắm" to 870_000, "Hóa đơn" to 600_000, "Đi lại" to 380_000, "Giải trí" to 200_000)
    private val currency get() = java.text.NumberFormat.getIntegerInstance(java.util.Locale("vi", "VN"))
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View { _binding = FragmentBudgetBinding.inflate(inflater, container, false); return binding.root }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.navBack.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.setLimit.setOnClickListener { setLimit() }
        NavigationHelper.setupBottomToolbar(binding.root, NavigationHelper.Destination.STATISTICS)
        render()
    }
    private fun render() {
        val total = limits.values.sum(); val used = spent.values.sum()
        binding.totalUsed.text = "Đã dùng ${currency.format(used)} / ${currency.format(total)} đ"
        binding.remaining.text = "Còn ${currency.format((total - used).coerceAtLeast(0))} đ"
        binding.totalProgress.progress = (used * 100 / total).coerceAtMost(100)
        binding.budgetRows.removeAllViews()
        limits.forEach { (name, limit) ->
            val amount = spent[name] ?: 0; val pct = if(limit == 0) 0 else amount * 100 / limit
            val row = LinearLayout(requireContext()).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(14), dp(12), dp(14), dp(12)); setBackgroundColor(0xFFFFFFFF.toInt()); isClickable = true; setOnClickListener { editLimit(name, limit) } }
            val line = LinearLayout(requireContext()).apply { orientation = LinearLayout.HORIZONTAL }
            line.addView(TextView(requireContext()).apply { text = "${CategoryStore.categories.find { it.name == name }?.emoji ?: "•"}  $name"; textSize = 14f; setTextColor(0xFF282638.toInt()); layoutParams = LinearLayout.LayoutParams(0, -2, 1f) })
            line.addView(TextView(requireContext()).apply { text = if(pct >= 100) "Vượt · $pct%" else if(pct >= 80) "Gần hạn mức · $pct%" else "An toàn · $pct%"; textSize = 11f; setTextColor(if(pct >= 100) 0xFFE35D6A.toInt() else 0xFF38AA91.toInt()) })
            row.addView(line)
            row.addView(TextView(requireContext()).apply { text = "${currency.format(amount)} / ${currency.format(limit)} đ"; textSize = 12f; setTextColor(0xFF777584.toInt()); setPadding(0, dp(6), 0, dp(7)) })
            val progress = android.widget.ProgressBar(requireContext(), null, android.R.attr.progressBarStyleHorizontal).apply { max = 100; this.progress = pct.coerceAtMost(100); progressTintList = android.content.res.ColorStateList.valueOf(if(pct >= 100) 0xFFEF6474.toInt() else 0xFF8058F5.toInt()) }
            row.addView(progress, LinearLayout.LayoutParams(-1, dp(6)))
            val card = com.google.android.material.card.MaterialCardView(requireContext()).apply { radius = dp(18).toFloat(); cardElevation = 0f; setCardBackgroundColor(0xFFFFFFFF.toInt()); addView(row) }
            binding.budgetRows.addView(card, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(10) })
        }
    }
    private fun setLimit() { val options = limits.keys.toTypedArray(); AlertDialog.Builder(requireContext()).setTitle("Chọn hạn mục") .setItems(options) { _, n -> editLimit(options[n], limits[options[n]] ?: 0) }.show() }
    private fun editLimit(name: String, value: Int) { val input = EditText(requireContext()).apply { inputType = 2; setText(value.toString()); hint = "Hạn mức tháng (đ)" }; AlertDialog.Builder(requireContext()).setTitle("Hạn mức · $name").setView(input).setNegativeButton("Hủy", null).setPositiveButton("Lưu") { _, _ -> limits[name] = input.text.toString().toIntOrNull()?.coerceAtLeast(0) ?: value; render() }.show() }
    private fun dp(v:Int) = (v * resources.displayMetrics.density).toInt()
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

class ExpenseEntryFragment : Fragment() {
    private var _binding: com.example.spendly.databinding.FragmentExpenseEntryBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View { _binding = com.example.spendly.databinding.FragmentExpenseEntryBinding.inflate(inflater, container, false); return binding.root }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.navBack.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.saveExpense.setOnClickListener { if (binding.expenseAmount.text.isNullOrBlank()) binding.expenseAmount.error = "Nhập số tiền" else { android.widget.Toast.makeText(requireContext(), "Đã lưu khoản chi (bản demo)", android.widget.Toast.LENGTH_SHORT).show(); parentFragmentManager.popBackStack() } }
        NavigationHelper.setupBottomToolbar(binding.root, NavigationHelper.Destination.HOME)
    }
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
