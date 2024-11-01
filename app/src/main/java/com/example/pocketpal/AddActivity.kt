package com.example.pocketpal

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.recyclerview.TransactionAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddActivity : AppCompatActivity() {

    private val expCategories = arrayOf("Food", "School", "Transportation", "Personal", "Savings", "Others")
    private val type = arrayOf("Income", "Expense")

    private lateinit var categoryAdapter: ArrayAdapter<String>
    private lateinit var typeAdapter: ArrayAdapter<String>
    private lateinit var spinnerCategory: AutoCompleteTextView
    private lateinit var spinnerType: AutoCompleteTextView

    private lateinit var okBtn:Button
    private lateinit var backBtn: ImageView

    private lateinit var inputTitle: TextInputEditText
    private lateinit var inputAmount: TextInputEditText
    private lateinit var inputNote: TextInputEditText


    private lateinit var transactionDB: DataBaseHandler
    private lateinit var viewModel: TransactionViewModel



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        transactionDB = DataBaseHandler(this)
        viewModel = TransactionViewModel(transactionDB)

        inputTitle = findViewById(R.id.trans_et_title)
        inputAmount = findViewById(R.id.trans_et_amount)
        inputNote = findViewById(R.id.trans_et_note)

        spinnerCategory = findViewById(R.id.trans_spinner_category)
        categoryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, expCategories)
        spinnerCategory.setAdapter(categoryAdapter)


        spinnerType = findViewById(R.id.trans_spinner_type)
        typeAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, type)
        spinnerType.setAdapter(typeAdapter)

        spinnerCategory.setText(expCategories[0],false)
        spinnerType.setText(type[0], false)


        okBtn = findViewById(R.id.trans_ok_btn)
        backBtn = findViewById(R.id.trans_bckBtn)


        backBtn.setOnClickListener{
            finish()
        }

        okBtn.setOnClickListener {
            if (addTransaction()) {
                finish()
            }
        }


    }

    private fun addTransaction(): Boolean {
        // Retrieve and validate the input values
        val totalIncome = viewModel.getTotalIncome()

        val title = inputTitle.text.toString().trim()
        val amountString = inputAmount.text.toString().trim()
        val note = inputNote.text.toString().trim()
        val categorySelected = spinnerCategory.text.toString().trim()
        val type = spinnerType.text.toString().trim()

        val amount = amountString.toDoubleOrNull()

        if (title.isEmpty() || amount == null || amount <= 0 || categorySelected.isEmpty() || type.isEmpty()) {
            val errorMessage = when {
                title.isEmpty() -> "Title cannot be empty"
                amount == null || amount <= 0 -> "Please enter a valid amount greater than 0"
                categorySelected.isEmpty() -> "Please select a category"
                type.isEmpty() -> "Please select a transaction type"
                else -> "Invalid input"
            }
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            return false
        }

        if (type == "Expense" && amount > totalIncome) {
            Toast.makeText(this, "Expense amount cannot exceed total income", Toast.LENGTH_SHORT).show()
            return false
        }

        // Set created date
        val createdAt = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())


        // Create a new Transaction object
        val transaction = Transaction(
            ID = genUniqID(),
            title = title,
            type = type,
            amount = amount,
            note = note,
            date = createdAt,
            category = categorySelected,
        )

        // Add the transaction to the global historyList
        Log.d("AddActivity", "Adding transaction: $transaction")
        transactionDB.addTransaction(transaction)
        //<<<<<<<<<<<<<<<<<<

        Toast.makeText(this, "Transaction added!", Toast.LENGTH_SHORT).show()
        clearInputs()
        return true
    }

    private fun clearInputs() {
        inputTitle.text?.clear()
        inputAmount.text?.clear()
        inputNote.text?.clear()
        spinnerCategory.setText(expCategories[0],false)
        spinnerType.setText(type[0], false)
    }

    private fun genUniqID(): String {
        val dateFormat = SimpleDateFormat("ddMMyy", Locale.getDefault())
        val date = dateFormat.format(Date())

        // Get the last transaction from the database
        val lastTransactionID = transactionDB.getLastTransactionID()

        // Extract the last part of the ID after the hyphen, or start from 1 if no previous ID exists
        val lastSequence = if (lastTransactionID != null && lastTransactionID.startsWith("PP-$date-")) {
            lastTransactionID.substringAfterLast("-").toIntOrNull() ?: 0
        } else {
            0
        }

        // Increment the sequence and format it to 6 digits
        val sequenceFormatted = String.format("%06d", lastSequence + 1)

        // Construct the new ID
        return "PP-$date-$sequenceFormatted"
    }




}