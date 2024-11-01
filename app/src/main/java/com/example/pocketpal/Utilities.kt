package com.example.pocketpal

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog

fun showTransactionDialog(context: Context,
                          transaction: Transaction,
                          transactionViewModel: TransactionViewModel, ) {
    val detailsDialog = Dialog(context)
    detailsDialog.setContentView(R.layout.details_dialog)
    detailsDialog.window?.setBackgroundDrawableResource(R.drawable.details_dialog_bg)

    // Reference dialog views
    val tvID = detailsDialog.findViewById<TextView>(R.id.details_ID)
    val tvTitle = detailsDialog.findViewById<TextView>(R.id.details_title)
    val tvAmount = detailsDialog.findViewById<TextView>(R.id.details_amount)
    val tvType = detailsDialog.findViewById<TextView>(R.id.details_type)
    val tvCategory = detailsDialog.findViewById<TextView>(R.id.details_category)
    val tvDate = detailsDialog.findViewById<TextView>(R.id.details_date)
    val tvNote = detailsDialog.findViewById<TextView>(R.id.details_note)

    val closeBtn = detailsDialog.findViewById<ImageView>(R.id.closeBtn)
    val del = detailsDialog.findViewById<ImageView>(R.id.delBtn)
    val edit = detailsDialog.findViewById<ImageView>(R.id.editBtn)

    // Set dialog content
    tvID.text = transaction.ID
    tvTitle.text = transaction.title
    tvAmount.text = transaction.amount.toString()
    tvType.text = transaction.type
    tvCategory.text = transaction.category
    tvDate.text = transaction.date
    tvNote.text = transaction.note

    if (transaction.type == "Income") {
        tvType.setTextColor(context.resources.getColor(R.color.income_color))
    } else {
        tvType.setTextColor(context.resources.getColor(R.color.expense_color))
    }

    // Set close button action
    closeBtn.setOnClickListener {
        detailsDialog.dismiss()
    }

    // Set delete button action with confirmation dialog
    del.setOnClickListener {
        showDeleteConfirmationDialog(context, transaction, transactionViewModel) {
            detailsDialog.dismiss()
        }

    }

    edit.setOnClickListener {
        Log.d("EditClicked", "clicked")
        val totalIncome = transactionViewModel.getTotalIncome()
        showEditTransactionBottomSheet(totalIncome,context, transaction, transactionViewModel) { updatedTransaction ->
            tvID.text = updatedTransaction.ID
            tvTitle.text = updatedTransaction.title
            tvAmount.text = updatedTransaction.amount.toString()
            tvType.text = updatedTransaction.type
            tvCategory.text = updatedTransaction.category
            tvDate.text = updatedTransaction.date
            tvNote.text = updatedTransaction.note

            // Update text color based on new type
            if (updatedTransaction.type == "Income") {
                tvType.setTextColor(ContextCompat.getColor(context, R.color.income_color))
            } else {
                tvType.setTextColor(ContextCompat.getColor(context, R.color.expense_color))
            }
        }
    }

    // Show the dialog
    detailsDialog.show()
}


// modify this function if you want to make a custom confirmation dialog
fun showDeleteConfirmationDialog(
    context: Context,
    transaction: Transaction,
    transactionViewModel: TransactionViewModel,
    dismissParentDialog: () -> Unit
) {
    val confirmDialog = Dialog(context)
    confirmDialog.setContentView(R.layout.confirmation_dialog)
    confirmDialog.window?.setBackgroundDrawableResource(R.drawable.white_bg)

    val btnCancel = confirmDialog.findViewById<Button>(R.id.con_no)
    val btnConfirm = confirmDialog.findViewById<Button>(R.id.con_yes)

    btnCancel.setOnClickListener {
        confirmDialog.dismiss()
    }

    btnConfirm.setOnClickListener {
        transactionViewModel.deleteTransaction(transaction)
        confirmDialog.dismiss()  // Dismiss confirmation dialog
        dismissParentDialog()    // Dismiss parent details dialog

    }


    confirmDialog.show()
}

fun showEditTransactionBottomSheet(
    totalIncome: Double,
    context: Context,
    transaction: Transaction,
    transactionViewModel: TransactionViewModel,
    onSave: (Transaction) -> Unit
) {
    Log.d("EditransactionDialog", "Bottom Sheet Dialog opened") // Add this line at the start of the function

    val type = arrayOf("Income", "Expense")
    val categories = arrayOf("Food", "School", "Transportation", "Personal", "Savings", "Others")

    // Inflate the layout for the Bottom Sheet Dialog
    val bottomSheetView = LayoutInflater.from(context).inflate(R.layout.edit_dialog, null)

    // Create the Bottom Sheet Dialog
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(bottomSheetView)

    // Set the width and height of the Bottom Sheet Dialog
    val layoutParams = bottomSheetDialog.window?.attributes
    layoutParams?.width = ViewGroup.LayoutParams.WRAP_CONTENT // 80% of screen width
    layoutParams?.height = (context.resources.displayMetrics.widthPixels * 0.9).toInt() // Adjust height as needed
    bottomSheetDialog.window?.attributes = layoutParams
    // Reference dialog views
    val tvID = bottomSheetView.findViewById<TextView>(R.id.tv_id)
    val editTitle = bottomSheetView.findViewById<EditText>(R.id.edit_trans_title)
    val editAmount = bottomSheetView.findViewById<EditText>(R.id.edit_trans_amount)
    val editType = bottomSheetView.findViewById<Spinner>(R.id.edit_trans_type)
    val editCategory = bottomSheetView.findViewById<Spinner>(R.id.edit_trans_category)
    val tvDate = bottomSheetView.findViewById<TextView>(R.id.tv_trans_date)
    val editNote = bottomSheetView.findViewById<EditText>(R.id.edit_trans_note)
    val saveBtn = bottomSheetView.findViewById<ImageView>(R.id.edit_saveBtn)

    // Set up type adapter
    val typeAdapter = ArrayAdapter(
        context,
        android.R.layout.simple_dropdown_item_1line,
        type
    )
    typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    editType.adapter = typeAdapter

    // Set up category adapter
    val categoryAdapter = ArrayAdapter(
        context,
        android.R.layout.simple_dropdown_item_1line,
        categories
    )
    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    editCategory.adapter = categoryAdapter

    // Set dialog content with existing transaction data
    editType.setSelection(typeAdapter.getPosition(transaction.type))
    editCategory.setSelection(categoryAdapter.getPosition(transaction.category))
    tvID.text = transaction.ID
    editTitle.setText(transaction.title)
    editAmount.setText(transaction.amount.toString())
    tvDate.text = transaction.date
    editNote.setText(transaction.note)

    // Save edited data when Save button is clicked
    saveBtn.setOnClickListener {
        // Update the transaction object with new values
        transaction.title = editTitle.text.toString()
        transaction.amount = editAmount.text.toString().toDoubleOrNull() ?: 0.0
        transaction.type = editType.selectedItem.toString()
        transaction.category = editCategory.selectedItem.toString()
        transaction.note = editNote.text.toString()

        if (transaction.type == "Expense" && transaction.amount > totalIncome) {
            Toast.makeText(context, "Expense amount cannot exceed total income", Toast.LENGTH_SHORT).show()
            return@setOnClickListener
        }
        // Update the database with new transaction details
        transactionViewModel.updateTransaction(transaction)

        // Refresh the adapter data

        // Call the onSave callback to update the details dialog
        onSave(transaction)

        // Dismiss the Bottom Sheet Dialog
        bottomSheetDialog.dismiss()
    }

    // Show the Bottom Sheet Dialog
    bottomSheetDialog.show()
}

fun formatCurrency(value: Double): String {
    return if (value >= 10_000) {
        // Format with 'k' notation and one decimal place
        "₱ ${String.format("%.1fk", value / 1_000)}"
    } else {
        // Format normally with two decimal places
        "₱ ${String.format("%.2f", value)}"
    }
}



