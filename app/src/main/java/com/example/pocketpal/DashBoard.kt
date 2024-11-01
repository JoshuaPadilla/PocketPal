package com.example.pocketpal

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketpal.databinding.FragmentDashboardBinding
import com.example.recyclerview.TransactionAdapter


class DashBoard : Fragment(), TransactionAdapter.OnTransactionClickListener {

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var transactionDB: DataBaseHandler
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var detailsDialog: Dialog
    private lateinit var viewModel: TransactionViewModel

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false)

        val view = binding.root

        transactionDB = DataBaseHandler(requireContext())
        viewModel = ViewModelProvider(this, TransactionViewModelFactory(transactionDB)).get(TransactionViewModel::class.java)

        detailsDialog = Dialog(requireContext())


        adapter = TransactionAdapter(emptyList(), this)
        recyclerView = binding.lvTransHistory
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.adapter = adapter



        // observe transactions
        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            adapter.updateTransactions(transactions)
        }

        viewModel.totalIncome.observe(viewLifecycleOwner) { totalIncome ->
            binding.totalIncome = formatCurrency(totalIncome)
        }

        viewModel.totalExpense.observe(viewLifecycleOwner) { totalExpense ->
            binding.totalExpense = formatCurrency(totalExpense)
        }

        viewModel.availableBalance.observe(viewLifecycleOwner) { availableBalance ->
            binding.totalBalance = formatCurrency(availableBalance)
        }

        return view // Return the inflated view

    }

    override fun onResume() {
        super.onResume()
        // Refresh the data when fragment becomes visible
        viewModel.loadTransactions() // Ensure transactions are loaded
    }


    override fun onTransactionClick(transactionId: String) {
        val clickedTransaction = viewModel.transactions.value?.find { it.ID == transactionId }
        if (clickedTransaction != null) {
            showTransactionDialog(requireContext(), clickedTransaction, viewModel)
        }
    }



}