package com.example.pocketpal

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.pocketpal.databinding.FragmentInsightsBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter


class InsightsFragment : Fragment() {

    private lateinit var binding: FragmentInsightsBinding
    private lateinit var lineChart: LineChart
    private lateinit var viewModel: TransactionViewModel
    private lateinit var transactionDB: DataBaseHandler
    private lateinit var cardDataList: List<Cards>
    private lateinit var chartAdapter: LineChartAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentInsightsBinding.inflate(inflater, container, false)


        transactionDB = DataBaseHandler(requireContext())
        viewModel = ViewModelProvider(this, TransactionViewModelFactory(transactionDB)).get(TransactionViewModel::class.java)

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            cardDataList = listOf(
                Cards("Income and Expense", transactions),
                Cards("Income and Expense", transactions)

            )
            chartAdapter = LineChartAdapter(cardDataList)
            binding.lineChartPager.adapter = chartAdapter
        }

        return binding.root
    }

//    private fun setupLineChart(transactions: List<Transaction>) {
//        val incomeEntries = ArrayList<Entry>()
//        val expenseEntries = ArrayList<Entry>()
//
//
//
//        // Add initial entry (0, 0) for both lines
//        incomeEntries.add(Entry(0f, 0f))
//        expenseEntries.add(Entry(0f, 0f))
//
//        lineChart.description.isEnabled = false  // Disable the default description
//        lineChart.animateX(500)
//        lineChart.legend.isEnabled = false  // disable legend
//
//        transactions.reversed().forEachIndexed { index, transaction ->
//            if (transaction.type == "Income") {
//                incomeEntries.add(Entry((index + 1).toFloat(), transaction.amount.toFloat())) // Increment index to avoid overlapping with initial entry
//            } else {
//                expenseEntries.add(Entry((index + 1).toFloat(), transaction.amount.toFloat())) // Increment index to avoid overlapping with initial entry
//            }
//        }
//
//        // Create datasets for income and expenses
//        val incomeDataSet = LineDataSet(incomeEntries, "Income")
//        incomeDataSet.color = ContextCompat.getColor(requireContext(), R.color.income_color)  // Set income line color
//        incomeDataSet.lineWidth = 2f
//        incomeDataSet.setDrawCircles(false)  // Remove circles at data points
//        incomeDataSet.setDrawValues(false)    // Remove values displayed on the line
//
//        val expenseDataSet = LineDataSet(expenseEntries, "Expenses")
//        expenseDataSet.color = ContextCompat.getColor(requireContext(), R.color.expense_color)  // Set expense line color
//        expenseDataSet.lineWidth = 2f
//        expenseDataSet.setDrawCircles(false)  // Remove circles at data points
//        expenseDataSet.setDrawValues(false)    // Remove values displayed on the line
//
//        // Create LineData with both datasets
//        val lineData = LineData(incomeDataSet, expenseDataSet)
//        lineChart.data = lineData
//
//        // Customize X-Axis
//        val xAxis = lineChart.xAxis
//        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.setDrawGridLines(false)  // Remove grid lines on X-axis
//
//        // Customize Y-Axis
//        val leftAxis = lineChart.axisLeft
//        leftAxis.setDrawGridLines(true)  // Enable horizontal grid lines on Y-axis
//        lineChart.axisRight.isEnabled = false  // Disable the right Y-axis
//
//        leftAxis.valueFormatter = object : ValueFormatter() {
//            override fun getFormattedValue(value: Float): String {
//                return if (value >= 1000 || value <= -1000) {
//                    "${(value / 1000).toInt()}k"  // Format values starting from 1000 or -1000 as "k"
//                } else {
//                    value.toInt().toString()  // Show values below 1000 and above -1000 as-is
//                }
//            }
//        }
//
//        lineChart.invalidate()  // Refresh chart to display data
//    }





}