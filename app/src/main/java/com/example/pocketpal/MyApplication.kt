//package com.example.pocketpal
//
//import android.app.Application
//
//class MyApplication: Application() {
//
//    val historyList = mutableListOf<Transaction>()
//    val incomeList = mutableListOf<Transaction>()
//    val expenseList = mutableListOf<Transaction>()
//    var historyID = historyList.size
//    var incomeID = incomeList.size
//    var expenseID = expenseList.size
//    private val historyObservers = mutableListOf<(List<Transaction>) -> Unit>()
//
//    override fun onCreate() {
//        super.onCreate()
//
//
//    }
//    fun addHistoryObserver(observer: (List<Transaction>) -> Unit) {
//        historyObservers.add(observer)
//    }
//
//    fun removeHistoryObserver(observer: (List<Transaction>) -> Unit) {
//        historyObservers.remove(observer)
//    }
//
//    fun notifyHistoryChanged() {
//        for (observer in historyObservers) {
//            observer(historyList)
//        }
//    }
//
//    fun add(transaction: Transaction) {
//        // Add to history list with a unique ID
//        transaction.ID = historyID++
//        historyList.add(transaction)
//
//        // Determine type and add to specific list with a unique ID
//        when (transaction.type) {
//            "Income" -> {
//                transaction.ID = incomeID++
//                incomeList.add(transaction)
//            }
//            "Expense" -> {
//                transaction.ID = expenseID++
//                expenseList.add(transaction)
//            }
//        }
//
//        notifyHistoryChanged()
//    }
//
//
//
//
//
//}