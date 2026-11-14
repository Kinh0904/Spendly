package com.example.spendly.presentation.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.spendly.R
import com.example.spendly.databinding.FragmentStatisticsBinding
import com.example.spendly.databinding.ItemStatisticsCategoryBinding

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: StatisticsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[StatisticsViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        com.example.spendly.utils.NavigationHelper.setupBottomToolbar(
            binding.root,
            com.example.spendly.utils.NavigationHelper.Destination.STATISTICS
        )
        setupSelectorListeners()
        observeViewModel()
    }

    private fun setupSelectorListeners() {
        val periodSelector = binding.periodSelector

        periodSelector.btnPeriodWeek.setOnClickListener {
            viewModel.setPeriod(StatisticsPeriod.WEEK)
        }
        periodSelector.btnPeriodYear.setOnClickListener {
            viewModel.setPeriod(StatisticsPeriod.YEAR)
        }
        periodSelector.btnPeriodMonth.setOnClickListener {
            viewModel.setPeriod(StatisticsPeriod.MONTH)
        }

        periodSelector.btnChartTypeBar.setOnClickListener {
            viewModel.setChartType(ChartType.BAR)
        }
        periodSelector.btnChartTypePie.setOnClickListener {
            viewModel.setChartType(ChartType.PIE)
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            renderPeriodSelector(state.period)
            renderChartTypeSelector(state.chartType)

            // Render Chart Card
            binding.chartCard.tvChartTotalLabel.text = state.totalLabel
            binding.chartCard.tvChartTotalAmount.text = state.totalAmountFormatted

            if (state.chartType == ChartType.BAR) {
                binding.chartCard.barChartView.visibility = View.VISIBLE
                binding.chartCard.pieChartView.visibility = View.GONE
                binding.chartCard.barChartView.setData(state.barItems)

                // Show comparison card, hide category list
                binding.comparisonCard.root.visibility = View.VISIBLE
                binding.categoryList.root.visibility = View.GONE

                binding.comparisonCard.tvComparisonTitle.text = state.comparisonTitle
                binding.comparisonCard.tvComparisonPercent.text = state.comparisonPercent
                binding.comparisonCard.tvComparisonDesc.text = state.comparisonDesc
            } else {
                binding.chartCard.barChartView.visibility = View.GONE
                binding.chartCard.pieChartView.visibility = View.VISIBLE
                binding.chartCard.pieChartView.setData(state.pieSlices)

                // Show category list, hide comparison card
                binding.comparisonCard.root.visibility = View.GONE
                binding.categoryList.root.visibility = View.VISIBLE

                renderCategoryList(state.categoryList)
            }

            // Render Budget Card
            binding.budgetCard.tvBudgetTitle.text = state.budgetTitle
            binding.budgetCard.tvBudgetUsed.text = state.budgetUsedText
            binding.budgetCard.progressBudget.progress = state.budgetProgress
            binding.budgetCard.tvBudgetRemaining.text = state.budgetRemainingText
        }
    }

    private fun renderPeriodSelector(period: StatisticsPeriod) {
        val ps = binding.periodSelector

        resetSelectorPill(ps.btnPeriodWeek)
        resetSelectorPill(ps.btnPeriodYear)
        resetSelectorPill(ps.btnPeriodMonth)

        when (period) {
            StatisticsPeriod.WEEK -> highlightSelectorPill(ps.btnPeriodWeek)
            StatisticsPeriod.MONTH -> highlightSelectorPill(ps.btnPeriodMonth)
            StatisticsPeriod.YEAR -> highlightSelectorPill(ps.btnPeriodYear)
        }
    }

    private fun renderChartTypeSelector(chartType: ChartType) {
        val ps = binding.periodSelector

        resetSelectorPill(ps.btnChartTypeBar)
        resetSelectorPill(ps.btnChartTypePie)

        when (chartType) {
            ChartType.BAR -> highlightSelectorPill(ps.btnChartTypeBar)
            ChartType.PIE -> highlightSelectorPill(ps.btnChartTypePie)
        }
    }

    private fun highlightSelectorPill(textView: TextView) {
        textView.setBackgroundResource(R.drawable.bg_statistics_pill_active)
        textView.setTextColor(Color.parseColor("#27243A"))
        textView.setTypeface(null, android.graphics.Typeface.BOLD)
    }

    private fun resetSelectorPill(textView: TextView) {
        textView.background = null
        textView.setTextColor(Color.parseColor("#7A768F"))
        textView.setTypeface(null, android.graphics.Typeface.NORMAL)
    }

    private fun renderCategoryList(categories: List<CategoryDataItem>) {
        val container = binding.categoryList.layoutCategoryContainer
        container.removeAllViews()

        val inflater = LayoutInflater.from(requireContext())
        categories.forEach { item ->
            val itemBinding = ItemStatisticsCategoryBinding.inflate(inflater, container, false)
            itemBinding.viewCatColorDot.backgroundTintList = android.content.res.ColorStateList.valueOf(
                Color.parseColor(item.colorHex)
            )
            itemBinding.tvCatName.text = item.name
            itemBinding.tvCatPercent.text = item.percentage
            itemBinding.tvCatAmount.text = item.amountFormatted
            container.addView(itemBinding.root)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}