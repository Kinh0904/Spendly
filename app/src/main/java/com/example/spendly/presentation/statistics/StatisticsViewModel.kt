package com.example.spendly.presentation.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spendly.presentation.statistics.chart.BarItem
import com.example.spendly.presentation.statistics.chart.PieSlice

enum class StatisticsPeriod {
    WEEK, MONTH, YEAR
}

enum class ChartType {
    BAR, PIE
}

data class CategoryDataItem(
    val name: String,
    val percentage: String,
    val amountFormatted: String,
    val colorHex: String,
)

data class StatisticsUiState(
    val period: StatisticsPeriod = StatisticsPeriod.WEEK,
    val chartType: ChartType = ChartType.BAR,
    val totalLabel: String = "Tổng chi tuần này",
    val totalAmountFormatted: String = "960.000 đ",
    val comparisonTitle: String = "So với tuần trước",
    val comparisonPercent: String = "4%",
    val comparisonDesc: String = "Giảm 4% so với tuần trước",
    val budgetTitle: String = "Ngân sách tuần",
    val budgetUsedText: String = "Đã dùng 960.000 / 1.250.000 đ",
    val budgetProgress: Int = 76,
    val budgetRemainingText: String = "Còn lại 290.000 đ",
    val barItems: List<BarItem> = emptyList(),
    val pieSlices: List<PieSlice> = emptyList(),
    val categoryList: List<CategoryDataItem> = emptyList(),
)

class StatisticsViewModel : ViewModel() {

    private val _uiState = MutableLiveData<StatisticsUiState>()
    val uiState: LiveData<StatisticsUiState> get() = _uiState

    init {
        loadData(StatisticsPeriod.WEEK, ChartType.BAR)
    }

    fun setPeriod(period: StatisticsPeriod) {
        val currentType = _uiState.value?.chartType ?: ChartType.BAR
        loadData(period, currentType)
    }

    fun setChartType(chartType: ChartType) {
        val currentPeriod = _uiState.value?.period ?: StatisticsPeriod.WEEK
        loadData(currentPeriod, chartType)
    }

    private fun loadData(period: StatisticsPeriod, chartType: ChartType) {
        val newState = when (period) {
            StatisticsPeriod.WEEK -> createWeekState(chartType)
            StatisticsPeriod.MONTH -> createMonthState(chartType)
            StatisticsPeriod.YEAR -> createYearState(chartType)
        }
        _uiState.value = newState
    }

    private fun createWeekState(chartType: ChartType) = StatisticsUiState(
        period = StatisticsPeriod.WEEK,
        chartType = chartType,
        totalLabel = "Tổng chi tuần này",
        totalAmountFormatted = "960.000 đ",
        comparisonTitle = "So với tuần trước",
        comparisonPercent = "4%",
        comparisonDesc = "Giảm 4% so với tuần trước",
        budgetTitle = "Ngân sách tuần",
        budgetUsedText = "Đã dùng 960.000 / 1.250.000 đ",
        budgetProgress = 76,
        budgetRemainingText = "Còn lại 290.000 đ",
        barItems = listOf(
            BarItem("T2", 35f),
            BarItem("T3", 55f),
            BarItem("T4", 40f),
            BarItem("T5", 60f),
            BarItem("T6", 75f),
            BarItem("T7", 65f),
            BarItem("CN", 85f, isHighlighted = true, tooltipText = "160 nghìn đ"),
        ),
        pieSlices = listOf(
            PieSlice("Ăn uống", 38f, "#FF7675"),
            PieSlice("Mua sắm", 25f, "#FDCB6E"),
            PieSlice("Hóa đơn", 18f, "#00B894"),
            PieSlice("Đi lại", 11f, "#74B9FF"),
            PieSlice("Khác", 8f, "#A4B0BE"),
        ),
        categoryList = listOf(
            CategoryDataItem("Ăn uống", "38%", "360.000 đ", "#FF7675"),
            CategoryDataItem("Mua sắm", "25%", "240.000 đ", "#FDCB6E"),
            CategoryDataItem("Hóa đơn", "18%", "170.000 đ", "#00B894"),
            CategoryDataItem("Đi lại", "11%", "110.000 đ", "#74B9FF"),
            CategoryDataItem("Khác", "8%", "80.000 đ", "#A4B0BE"),
        ),
    )

    private fun createMonthState(chartType: ChartType) = StatisticsUiState(
        period = StatisticsPeriod.MONTH,
        chartType = chartType,
        totalLabel = "Tổng chi tháng này",
        totalAmountFormatted = "3.850.000 đ",
        comparisonTitle = "So với tháng trước",
        comparisonPercent = "12%",
        comparisonDesc = "Giảm 12% so với tháng trước",
        budgetTitle = "Ngân sách tháng",
        budgetUsedText = "Đã dùng 3.850.000 / 5.000.000 đ",
        budgetProgress = 77,
        budgetRemainingText = "Còn lại 1.150.000 đ",
        barItems = listOf(
            BarItem("T1", 40f),
            BarItem("T2", 65f),
            BarItem("T3", 50f),
            BarItem("T4", 38f),
            BarItem("T5", 90f, isHighlighted = true, tooltipText = "3,85 triệu đ"),
            BarItem("T6", 55f),
        ),
        pieSlices = listOf(
            PieSlice("Ăn uống", 38f, "#FF7675"),
            PieSlice("Mua sắm", 24f, "#FDCB6E"),
            PieSlice("Hóa đơn", 18f, "#00B894"),
            PieSlice("Đi lại", 12f, "#74B9FF"),
            PieSlice("Khác", 8f, "#A4B0BE"),
        ),
        categoryList = listOf(
            CategoryDataItem("Ăn uống", "38%", "1.450.000 đ", "#FF7675"),
            CategoryDataItem("Mua sắm", "24%", "930.000 đ", "#FDCB6E"),
            CategoryDataItem("Hóa đơn", "18%", "690.000 đ", "#00B894"),
            CategoryDataItem("Đi lại", "12%", "460.000 đ", "#74B9FF"),
            CategoryDataItem("Khác", "8%", "320.000 đ", "#A4B0BE"),
        ),
    )

    private fun createYearState(chartType: ChartType) = StatisticsUiState(
        period = StatisticsPeriod.YEAR,
        chartType = chartType,
        totalLabel = "Tổng chi năm này",
        totalAmountFormatted = "46.200.000 đ",
        comparisonTitle = "So với năm trước",
        comparisonPercent = "8%",
        comparisonDesc = "Giảm 8% so với năm trước",
        budgetTitle = "Ngân sách năm",
        budgetUsedText = "Đã dùng 46.200.000 / 60.000.000 đ",
        budgetProgress = 77,
        budgetRemainingText = "Còn lại 13.800.000 đ",
        barItems = listOf(
            BarItem("T1", 45f),
            BarItem("T2", 50f),
            BarItem("T3", 48f),
            BarItem("T4", 60f),
            BarItem("T5", 58f),
            BarItem("T6", 52f),
            BarItem("T7", 62f),
            BarItem("T8", 55f),
            BarItem("T9", 65f),
            BarItem("T10", 60f),
            BarItem("T11", 64f),
            BarItem("T12", 80f, isHighlighted = true, tooltipText = "4,25 triệu đ"),
        ),
        pieSlices = listOf(
            PieSlice("Ăn uống", 38f, "#FF7675"),
            PieSlice("Mua sắm", 24f, "#FDCB6E"),
            PieSlice("Hóa đơn", 18f, "#00B894"),
            PieSlice("Đi lại", 12f, "#74B9FF"),
            PieSlice("Khác", 8f, "#A4B0BE"),
        ),
        categoryList = listOf(
            CategoryDataItem("Ăn uống", "38%", "17.400.000 đ", "#FF7675"),
            CategoryDataItem("Mua sắm", "24%", "11.160.000 đ", "#FDCB6E"),
            CategoryDataItem("Hóa đơn", "18%", "8.280.000 đ", "#00B894"),
            CategoryDataItem("Đi lại", "12%", "5.520.000 đ", "#74B9FF"),
            CategoryDataItem("Khác", "8%", "3.840.000 đ", "#A4B0BE"),
        ),
    )
}