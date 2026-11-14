package com.example.spendly.presentation.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.spendly.MainActivity
import com.example.spendly.R
import com.example.spendly.databinding.FragmentCategoryBinding
import com.example.spendly.utils.NavigationHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder

data class SpendCategory(val name: String, val emoji: String, val count: Int, val color: Int, val type: String = "Chi", val budget: String = "")

object CategoryStore {
    val categories = mutableListOf(
        SpendCategory("Ăn uống", "🍴", 32, 0xFFFF8A65.toInt(), budget = "3.000.000 đ"),
        SpendCategory("Mua sắm", "▣", 18, 0xFFFFC857.toInt()),
        SpendCategory("Hóa đơn", "▤", 8, 0xFF42C6A0.toInt()),
        SpendCategory("Đi lại", "▱", 24, 0xFF5594F5.toInt()),
        SpendCategory("Giải trí", "♧", 12, 0xFFB45CF4.toInt()),
        SpendCategory("Khác", "•••", 5, 0xFFADB2BD.toInt())
    )
}

class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    private var selectedType = "Chi"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.navBack.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.tabExpense.setOnClickListener { selectedType = "Chi"; render() }
        binding.tabIncome.setOnClickListener { selectedType = "Thu"; render() }
        binding.addCategory.setOnClickListener { editCategory(null) }
        binding.budgetManagement.setOnClickListener { (activity as? MainActivity)?.navigateToFragment(com.example.spendly.presentation.transaction.add.BudgetFragment()) }
        NavigationHelper.setupBottomToolbar(binding.root, NavigationHelper.Destination.SETTINGS)
        render()
    }

    private fun render() {
        val list = CategoryStore.categories.filter { it.type == selectedType }
        binding.tabExpense.isSelected = selectedType == "Chi"
        binding.tabIncome.isSelected = selectedType == "Thu"
        binding.categoryCount.text = "${list.size} danh mục"
        binding.categoryRows.removeAllViews()
        list.forEach { category ->
            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
                setPadding(dp(14), dp(12), dp(14), dp(12))
                isClickable = true
                setOnClickListener { editCategory(category) }
            }
            val icon = TextView(requireContext()).apply {
                text = category.emoji; textSize = 20f; gravity = android.view.Gravity.CENTER
                setTextColor(category.color); setBackgroundColor(0x18FFFFFF)
                layoutParams = LinearLayout.LayoutParams(dp(42), dp(42)).apply { marginEnd = dp(14) }
            }
            val label = LinearLayout(requireContext()).apply { orientation = LinearLayout.VERTICAL; layoutParams = LinearLayout.LayoutParams(0, -2, 1f) }
            label.addView(TextView(requireContext()).apply { text = category.name; textSize = 14f; setTextColor(0xFF242334.toInt()) })
            label.addView(TextView(requireContext()).apply { text = "${category.count} giao dịch"; textSize = 11f; setTextColor(0xFF858392.toInt()) })
            row.addView(icon); row.addView(label)
            row.addView(TextView(requireContext()).apply { text = "⠿"; textSize = 22f; setTextColor(0xFF777584.toInt()) })
            binding.categoryRows.addView(row)
            if (category != list.lastOrNull()) binding.categoryRows.addView(View(requireContext()).apply { setBackgroundColor(0xFFF3F1F8.toInt()); layoutParams = LinearLayout.LayoutParams(-1, dp(1)) })
        }
    }

    private fun editCategory(existing: SpendCategory?) {
        val ctx = requireContext()
        val form = LinearLayout(ctx).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(8), dp(20), 0) }
        val name = EditText(ctx).apply { hint = "Tên danh mục"; setText(existing?.name.orEmpty()) }
        val icon = EditText(ctx).apply { hint = "Biểu tượng (ví dụ 🍴)"; setText(existing?.emoji.orEmpty()) }
        val budget = EditText(ctx).apply { hint = "Ngân sách tháng (không bắt buộc)"; setText(existing?.budget.orEmpty()); inputType = 2 }
        form.addView(TextView(ctx).apply { text = "Tên danh mục"; textSize = 13f })
        form.addView(name); form.addView(TextView(ctx).apply { text = "Biểu tượng"; textSize = 13f })
        form.addView(icon); form.addView(TextView(ctx).apply { text = "Ngân sách tháng"; textSize = 13f })
        form.addView(budget)
        val dialog = MaterialAlertDialogBuilder(ctx).setTitle(if (existing == null) "Thêm danh mục" else "Sửa danh mục")
            .setView(form).setNegativeButton("Hủy", null).setPositiveButton("Lưu", null)
            .apply { if (existing != null) setNeutralButton("Xóa", null) }.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val value = name.text.toString().trim()
                when {
                    value.isEmpty() -> name.error = "Vui lòng nhập tên danh mục"
                    CategoryStore.categories.any { it.type == selectedType && it.name.equals(value, true) && it != existing } -> name.error = "Tên danh mục đã tồn tại"
                    else -> {
                        val item = SpendCategory(value, icon.text.toString().ifBlank { "•" }, existing?.count ?: 0, existing?.color ?: 0xFF8257F5.toInt(), selectedType, budget.text.toString())
                        if (existing == null) CategoryStore.categories.add(item) else CategoryStore.categories[CategoryStore.categories.indexOf(existing)] = item
                        dialog.dismiss(); render()
                    }
                }
            }
            if (existing != null) dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener { dialog.dismiss(); confirmDelete(existing) }
        }
        dialog.show()
    }

    private fun confirmDelete(category: SpendCategory) {
        val candidates = CategoryStore.categories.filter { it.type == category.type && it != category }
        if (candidates.isEmpty()) { Toast.makeText(requireContext(), "Cần có danh mục khác để chuyển giao dịch", Toast.LENGTH_SHORT).show(); return }
        var target = candidates.first()
        MaterialAlertDialogBuilder(requireContext()).setTitle("Xóa ${category.name}?")
            .setMessage("Chọn danh mục nhận ${category.count} giao dịch để không mất dữ liệu.")
            .setSingleChoiceItems(candidates.map { it.name }.toTypedArray(), 0) { _, which -> target = candidates[which] }
            .setNegativeButton("Hủy", null)
            .setPositiveButton("Xóa") { _, _ -> target.let { dest ->
                val index = CategoryStore.categories.indexOf(dest)
                CategoryStore.categories[index] = dest.copy(count = dest.count + category.count)
                CategoryStore.categories.remove(category); render()
                Toast.makeText(requireContext(), "Đã chuyển giao dịch sang ${dest.name}", Toast.LENGTH_SHORT).show()
            } }.show()
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
