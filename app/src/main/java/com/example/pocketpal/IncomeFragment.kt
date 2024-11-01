package com.example.pocketpal

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketpal.databinding.FragmentIncomeFragmentBinding
import com.example.recyclerview.TransactionAdapter


class IncomeFragment : Fragment(), TransactionAdapter.OnTransactionClickListener {

    private lateinit var binding: FragmentIncomeFragmentBinding
    private lateinit var viewModel: TransactionViewModel
    private lateinit var transactionDB: DataBaseHandler
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var selectedOption: String

    private val historySelector = listOf("Income", "Expense")
    private lateinit var historyAdapter: ArrayAdapter<String>




    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_income_fragment, container, false)



        transactionDB = DataBaseHandler(requireContext())
        viewModel = ViewModelProvider(this, TransactionViewModelFactory(transactionDB)).get(TransactionViewModel::class.java)

        recyclerView = binding.incHistoryList // Access the recyclerView after inflating the layout
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = TransactionAdapter(emptyList(), this)
        recyclerView.adapter = adapter

        historyAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, viewModel.transactions.value?.map { it.type } ?: emptyList())
        binding.histSelector.setAdapter(historyAdapter)
        selectedOption = historySelector[0]


        binding.histSelector.setText(selectedOption,false)
        updateFragmentData(historySelector[0])

        viewModel.totalIncome.observe(viewLifecycleOwner) { totalIncome ->
            binding.totalHolder = formatCurrency(totalIncome)
            updateFragmentData(selectedOption)
        }


        binding.histSelector.setOnItemClickListener { _, _, position, _ ->
            selectedOption = historySelector[position]

            if (selectedOption == "Income") {
                viewModel.totalIncome.observe(viewLifecycleOwner) { totalIncome ->
                    binding.totalHolder = formatCurrency(totalIncome)
                    updateFragmentData(selectedOption)
                }
            } else {
                viewModel.totalExpense.observe(viewLifecycleOwner) { totalExpense ->
                    binding.totalHolder = formatCurrency(totalExpense)
                    updateFragmentData(selectedOption)
                }
            }
            updateFragmentData(selectedOption)
        }

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            
            adapter.updateTransactions(transactions)
        }


        // Inflate the layout for this fragment
        return binding.root
    }

    private fun updateFragmentData(selectedOption: String) {
        val filteredTransactions = viewModel.getTransactionsByType(selectedOption)
        adapter.updateTransactions(filteredTransactions)

        // Update color and title based on selectedOption
        if (selectedOption == "Income") {
            binding.titleHolder = "TOTAL INCOME"
            binding.title.setTextColor(ContextCompat.getColor(requireContext(), R.color.income_color))
        } else {
            binding.titleHolder = "TOTAL EXPENSE"
            binding.title.setTextColor(ContextCompat.getColor(requireContext(), R.color.expense_color))
        }
    }

    // repopulate the selector again
    override fun onResume() {
        super.onResume()
        // Repopulate the selector
        historyAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, historySelector)
        binding.histSelector.setAdapter(historyAdapter)
        selectedOption = historySelector[0]
        binding.histSelector.setText(selectedOption, false)
        viewModel.loadTransactions()



        // Update the fragment data to reflect current transactions
        updateFragmentData(selectedOption)
    }

    override fun onTransactionClick(transactionId: String) {
        val clickedTransaction = transactionDB.getTransactionByID(transactionId)
        if (clickedTransaction != null) {
            showTransactionDialog(requireContext(), clickedTransaction, viewModel)
        }
    }






}