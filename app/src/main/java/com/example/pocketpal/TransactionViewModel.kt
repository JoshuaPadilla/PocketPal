package com.example.pocketpal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TransactionViewModel(private val db: DataBaseHandler) : ViewModel() {

    private val _totalIncome = MutableLiveData<Double>()
    val totalIncome: LiveData<Double> get() = _totalIncome

    private val _totalExpense = MutableLiveData<Double>()
    val totalExpense: LiveData<Double> get() = _totalExpense

    private val _availableBalance = MutableLiveData<Double>()
    val availableBalance: LiveData<Double> get() = _availableBalance

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> get() = _transactions

    init {
        loadTransactions()
        updateTotalIncomeAndExpense()
        updateAvailableBalance()
    }


    // Load all transactions from the database
    fun loadTransactions() {
        _transactions.value = db.getAllTransactions()
        updateTotalIncomeAndExpense()
    }

    // Add a new transaction
    fun addTransaction(transaction: Transaction) {
        db.addTransaction(transaction)
        loadTransactions()
        updateTotalIncomeAndExpense()
    }

    // Update an existing transaction
    fun updateTransaction(transaction: Transaction) {
        db.updateTransaction(transaction)
        loadTransactions()
        updateTotalIncomeAndExpense()
    }

    // Delete a transaction
    fun deleteTransaction(transaction: Transaction) {
        db.deleteTransaction(transaction.ID)
        loadTransactions()
        updateTotalIncomeAndExpense()
    }

    fun getTransactionsByType(type: String): List<Transaction> {
        val transactionsByType = db.getTransactionsByType(type) // Fetch transactions from the database
        _transactions.value = transactionsByType // Update LiveData with the fetched transactions
        return transactionsByType // Return the list of transactions
    }

    // Update both total income and expense
    private fun updateTotalIncomeAndExpense() {
        val incomeTransactions = db.getTransactionsByType("Income")
        _totalIncome.value = incomeTransactions.sumOf { it.amount }

        val expenseTransactions = db.getTransactionsByType("Expense")
        _totalExpense.value = expenseTransactions.sumOf { it.amount }

        updateAvailableBalance() // Update available balance after updating totals
    }

    // Calculate total balance
    private fun updateAvailableBalance() {
        val income = _totalIncome.value ?: 0.0
        val expense = _totalExpense.value ?: 0.0
        _availableBalance.value = income - expense
    }

    // Calculate total income
    fun getTotalIncome(): Double {
        val incomeTransactions = db.getTransactionsByType("Income")
        return incomeTransactions.sumOf { it.amount }
    }

    // Calculate total expenses
    fun getTotalExpense(): Double {
        val expenseTransactions = db.getTransactionsByType("Expense")
        return expenseTransactions.sumOf { it.amount }
    }

    // Calculate total income by category
    fun getTotalIncomeByCategory(category: String): Double {
        val incomeTransactions = db.getTransactionsByType("Income")
        return incomeTransactions
            .filter { it.category == category }
            .sumOf { it.amount }
    }

    // Calculate total expense by category
    fun getTotalExpenseByCategory(category: String): Double {
        val expenseTransactions = db.getTransactionsByType("Expense")
        return expenseTransactions
            .filter { it.category == category }
            .sumOf { it.amount }
    }
}
