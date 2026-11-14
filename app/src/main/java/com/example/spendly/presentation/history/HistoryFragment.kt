package com.example.spendly.presentation.history

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spendly.R
import com.example.spendly.databinding.FragmentHistoryBinding
import com.example.spendly.databinding.ItemTransactionBinding

data class TransactionItem(
    val title: String,
    val dateCategory: String,
    val amountFormatted: String,
    val iconRes: Int,
    val colorHex: String,
)

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        com.example.spendly.utils.NavigationHelper.setupBottomToolbar(
            binding.root,
            com.example.spendly.utils.NavigationHelper.Destination.HISTORY
        )
        setupRecyclerView()
        setupFilters()
    }

    private fun setupRecyclerView() {
        val sampleData = listOf(
            TransactionItem("Ăn trưa", "Ăn uống • Today 12:30", "-80.000 đ", R.drawable.ic_utensils, "#FFEFEA"),
            TransactionItem("Đi xe bus", "Đi lại • Today 08:15", "-10.000 đ", R.drawable.ic_transport, "#E3F2FD"),
            TransactionItem("Mua sách", "Mua sắm • Yesterday 15:40", "-220.000 đ", R.drawable.ic_book, "#EFEBFD"),
            TransactionItem("Tiền điện", "Hóa đơn • 10/03/2024", "-200.000 đ", R.drawable.ic_bolt, "#FFF8E7"),
            TransactionItem("Cà phê", "Ăn uống • 09/03/2024", "-35.000 đ", R.drawable.ic_utensils, "#FFEFEA"),
        )

        binding.rvTransactionHistory.adapter = TransactionAdapter(sampleData)
    }

    private fun setupFilters() {
        binding.btnFilterAll.setOnClickListener {
            highlightPill(binding.btnFilterAll)
            resetPill(binding.btnFilterExpense)
            resetPill(binding.btnFilterIncome)
        }

        binding.btnFilterExpense.setOnClickListener {
            resetPill(binding.btnFilterAll)
            highlightPill(binding.btnFilterExpense)
            resetPill(binding.btnFilterIncome)
        }

        binding.btnFilterIncome.setOnClickListener {
            resetPill(binding.btnFilterAll)
            resetPill(binding.btnFilterExpense)
            highlightPill(binding.btnFilterIncome)
        }
    }

    private fun highlightPill(tv: TextView) {
        tv.setBackgroundResource(R.drawable.bg_statistics_pill_active)
        tv.setTextColor(Color.parseColor("#27243A"))
        tv.setTypeface(null, android.graphics.Typeface.BOLD)
    }

    private fun resetPill(tv: TextView) {
        tv.background = null
        tv.setTextColor(Color.parseColor("#7A768F"))
        tv.setTypeface(null, android.graphics.Typeface.NORMAL)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class TransactionAdapter(private val items: List<TransactionItem>) :
        RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

        class ViewHolder(val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemTransactionBinding.inflate(inflater, parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.binding.tvTxTitle.text = item.title
            holder.binding.tvTxDateCategory.text = item.dateCategory
            holder.binding.tvTxAmount.text = item.amountFormatted
            holder.binding.ivTxIcon.setImageResource(item.iconRes)
            holder.binding.ivTxIcon.backgroundTintList = android.content.res.ColorStateList.valueOf(
                Color.parseColor(item.colorHex)
            )
        }

        override fun getItemCount() = items.size
    }
}