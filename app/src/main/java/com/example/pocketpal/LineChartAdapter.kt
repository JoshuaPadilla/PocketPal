// ChartCardAdapter.kt
package com.example.pocketpal

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.components.XAxis



class LineChartAdapter(private val cardDataList: List<Cards>) : RecyclerView.Adapter<LineChartAdapter.ChartViewHolder>() {

    inner class ChartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.cardAdapterTitle)
        val lineChart: LineChart = view.findViewById(R.id.cardAdapterLineChart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.linechart_card_item, parent, false)
        return ChartViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChartViewHolder, position: Int) {
        val cardData = cardDataList[position]
        holder.titleTextView.text = cardData.title
        if (position == 0) {
            setupLineChart(holder.lineChart, cardData.transactions)
        } else if (position == 1) {
            byCategory(holder.lineChart, cardData.transactions)
        }
    }

    override fun getItemCount(): Int {
        return cardDataList.size
    }
}


private fun setupLineChart(lineChart: LineChart, transactions: List<Transaction>) {
    val incomeEntries = ArrayList<Entry>()
    val expenseEntries = ArrayList<Entry>()

    incomeEntries.add(Entry(0f, 0f))
    expenseEntries.add(Entry(0f, 0f))

    lineChart.description.isEnabled = false
    lineChart.animateX(500)
    lineChart.legend.isEnabled = false

    transactions.reversed().forEachIndexed { index, transaction ->
        if (transaction.type == "Income") {
            incomeEntries.add(Entry((index + 1).toFloat(), transaction.amount.toFloat()))
        } else {
            expenseEntries.add(Entry((index + 1).toFloat(), transaction.amount.toFloat()))
        }
    }

    val incomeDataSet = LineDataSet(incomeEntries, "Income").apply {
        color = R.color.income_color
        lineWidth = 2f
        setDrawCircles(false)
        setDrawValues(false)
    }

    val expenseDataSet = LineDataSet(expenseEntries, "Expenses").apply {
        color = R.color.expense_color
        lineWidth = 2f
        setDrawCircles(false)
        setDrawValues(false)
    }

    lineChart.data = LineData(incomeDataSet, expenseDataSet)

    lineChart.xAxis.apply {
        position = XAxis.XAxisPosition.BOTTOM
        setDrawGridLines(false)
    }

    lineChart.axisLeft.apply {
        setDrawGridLines(true)
        valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (value >= 1000 || value <= -1000) {
                    "${(value / 1000).toInt()}k"
                } else {
                    value.toInt().toString()
                }
            }
        }
    }

    lineChart.axisRight.isEnabled = false
    lineChart.invalidate()
}

private fun byCategory(lineChart: LineChart, transactions: List<Transaction>) {
    val food = ArrayList<Entry>()
    val school = ArrayList<Entry>()
    val transpo = ArrayList<Entry>()
    val personal = ArrayList<Entry>()
    val savings = ArrayList<Entry>()
    val others = ArrayList<Entry>()

    // Initialize entries
    food.add(Entry(0f, 0f))
    school.add(Entry(0f, 0f))
    transpo.add(Entry(0f, 0f))
    personal.add(Entry(0f, 0f))
    savings.add(Entry(0f, 0f))
    others.add(Entry(0f, 0f))

    lineChart.description.isEnabled = false
    lineChart.animateX(500)
    lineChart.legend.isEnabled = false

    // Process transactions
    transactions.reversed().forEachIndexed { index, transaction ->
        when (transaction.category.lowercase()) { // Normalize category names
            "food" -> food.add(Entry((index + 1).toFloat(), transaction.amount.toFloat()))
            "school" -> school.add(Entry((index + 1).toFloat(), transaction.amount.toFloat()))
            "transpo" -> transpo.add(Entry((index + 1).toFloat(), transaction.amount.toFloat()))
            "personal" -> personal.add(Entry((index + 1).toFloat(), transaction.amount.toFloat()))
            "savings" -> savings.add(Entry((index + 1).toFloat(), transaction.amount.toFloat()))
            else -> others.add(Entry((index + 1).toFloat(), transaction.amount.toFloat()))
        }
    }

    // Debug log to check data sizes
    Log.d("DataSize", "Food: ${food.size}, School: ${school.size}, Transportation: ${transpo.size}, Personal: ${personal.size}, Savings: ${savings.size}, Others: ${others.size}")

    // Create datasets with distinct colors
    val foodData = LineDataSet(food, "food").apply {
        color = 0xFF4CAF50.toInt()// Green
        lineWidth = 2f
        setDrawCircles(false)
        setDrawValues(false)
    }

    val schoolData = LineDataSet(school, "school").apply {
        color = 0xFF2196F3.toInt()// Blue
        lineWidth = 2f
        setDrawCircles(false)
        setDrawValues(false)
    }

    val transpoData = LineDataSet(transpo, "transportation").apply {
        color = 0xFFFF9800.toInt() // Orange
        lineWidth = 2f
        setDrawCircles(false)
        setDrawValues(false)
    }

    val personalData = LineDataSet(personal, "personal").apply {
        color = 0xFF9C27B0.toInt() // Purple
        lineWidth = 2f
        setDrawCircles(false)
        setDrawValues(false)
    }

    val savingsData = LineDataSet(savings, "savings").apply {
        color = 0xFF009688.toInt() // Teal
        lineWidth = 2f
        setDrawCircles(false)
        setDrawValues(false)
    }

    val othersData = LineDataSet(others, "others").apply {
        color = 0xFF9E9E9E.toInt() // Grey
        lineWidth = 2f
        setDrawCircles(false)
        setDrawValues(false)
    }

    lineChart.data = LineData(foodData, schoolData, transpoData, personalData, savingsData, othersData)

    lineChart.xAxis.apply {
        position = XAxis.XAxisPosition.BOTTOM
        setDrawGridLines(false)
    }

    lineChart.axisLeft.apply {
        setDrawGridLines(true)
        valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (value >= 1000 || value <= -1000) {
                    "${(value / 1000).toInt()}k"
                } else {
                    value.toInt().toString()
                }
            }
        }
    }

    lineChart.axisRight.isEnabled = false
    lineChart.invalidate()
}


