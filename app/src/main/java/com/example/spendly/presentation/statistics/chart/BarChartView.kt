package com.example.spendly.presentation.statistics.chart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

data class BarItem(
    val label: String,
    val value: Float,
    val isHighlighted: Boolean = false,
    val tooltipText: String? = null,
)

class BarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private var barItems: List<BarItem> = emptyList()

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#7A768F")
        textSize = 28f
        textAlign = Paint.Align.CENTER
    }

    private val tooltipBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#7C4DFF")
        style = Paint.Style.FILL
    }

    private val tooltipTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 26f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    fun setData(items: List<BarItem>) {
        this.barItems = items
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (barItems.isEmpty()) return

        val width = width.toFloat()
        val height = height.toFloat()

        val labelPadding = 40f
        val topPadding = 60f
        val chartHeight = height - labelPadding - topPadding

        val maxVal = barItems.maxOfOrNull { it.value }?.takeIf { it > 0f } ?: 1f
        val count = barItems.size
        val availableWidth = width - 40f
        val totalBarSpace = availableWidth / count
        val barWidth = (totalBarSpace * 0.55f).coerceAtMost(48f)

        barItems.forEachIndexed { index, item ->
            val centerX = 20f + totalBarSpace * index + totalBarSpace / 2f
            val left = centerX - barWidth / 2f
            val right = centerX + barWidth / 2f

            val barHeightRatio = (item.value / maxVal).coerceIn(0.15f, 1.0f)
            val barTop = topPadding + chartHeight * (1f - barHeightRatio)
            val barBottom = topPadding + chartHeight

            val rect = RectF(left, barTop, right, barBottom)
            barPaint.color = if (item.isHighlighted) Color.parseColor("#7C4DFF") else Color.parseColor("#D1C7FB")
            canvas.drawRoundRect(rect, 16f, 16f, barPaint)

            // Draw label
            labelPaint.color = if (item.isHighlighted) Color.parseColor("#7C4DFF") else Color.parseColor("#7A768F")
            labelPaint.isFakeBoldText = item.isHighlighted
            canvas.drawText(item.label, centerX, height - 8f, labelPaint)

            // Draw tooltip if highlighted
            if (item.isHighlighted && !item.tooltipText.isNullOrEmpty()) {
                val text = item.tooltipText
                val textWidth = tooltipTextPaint.measureText(text)
                val padding = 16f
                val pillWidth = textWidth + padding * 2
                val pillHeight = 44f

                var pillLeft = centerX - pillWidth / 2f
                var pillRight = centerX + pillWidth / 2f
                if (pillRight > width - 10f) {
                    pillRight = width - 10f
                    pillLeft = pillRight - pillWidth
                }
                if (pillLeft < 10f) {
                    pillLeft = 10f
                    pillRight = pillLeft + pillWidth
                }

                val pillTop = (barTop - pillHeight - 12f).coerceAtLeast(4f)
                val pillBottom = pillTop + pillHeight
                val pillRect = RectF(pillLeft, pillTop, pillRight, pillBottom)

                canvas.drawRoundRect(pillRect, 16f, 16f, tooltipBgPaint)
                canvas.drawText(text, pillRect.centerX(), pillRect.centerY() + 8f, tooltipTextPaint)
            }
        }
    }
}