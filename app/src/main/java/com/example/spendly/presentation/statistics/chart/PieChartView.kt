package com.example.spendly.presentation.statistics.chart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

data class PieSlice(
    val categoryName: String,
    val percentage: Float,
    val colorHex: String,
)

class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private var slices: List<PieSlice> = emptyList()

    private val slicePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val rectF = RectF()

    fun setData(items: List<PieSlice>) {
        this.slices = items
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (slices.isEmpty()) return

        val size = width.coerceAtMost(height).toFloat()
        val margin = 10f
        rectF.set(margin, margin, size - margin, size - margin)

        var startAngle = -90f
        val totalPercentage = slices.sumOf { it.percentage.toDouble() }.toFloat().takeIf { it > 0f } ?: 100f

        slices.forEach { slice ->
            val sweepAngle = (slice.percentage / totalPercentage) * 360f
            slicePaint.color = Color.parseColor(slice.colorHex)
            canvas.drawArc(rectF, startAngle, sweepAngle, true, slicePaint)
            startAngle += sweepAngle
        }
    }
}