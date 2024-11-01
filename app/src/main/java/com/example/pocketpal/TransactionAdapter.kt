package com.example.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketpal.R
import com.example.pocketpal.Transaction
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import com.example.pocketpal.IncomeFragment
import com.example.pocketpal.TransactionDiffCallback
import com.example.pocketpal.formatCurrency


class TransactionAdapter(private var transactionList: List<Transaction>,
                         private val clickListener: OnTransactionClickListener
) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {



    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_transTitle)
        val category: TextView = itemView.findViewById(R.id.tv_transCat)
        val amount: TextView = itemView.findViewById(R.id.tv_transAmount)
        val date: TextView = itemView.findViewById(R.id.tv_transDate)
        val imageView: ImageView = itemView.findViewById(R.id.iv_transIcon)
        val parentLayout: CardView = itemView.findViewById(R.id.transItemParentLayout) // Update to CardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.title.text = transaction.title
        holder.category.text = transaction.category
        holder.amount.text = formatCurrency(transaction.amount)
        holder.date.text = transaction.date
        holder.imageView.setImageResource(getCategoryImage(transaction.category))

        // Set text color based on transaction type
        if (transaction.type.equals("Income", ignoreCase = true)) {
            holder.amount.setTextColor(holder.itemView.context.getColor(R.color.income_color))
        } else {
            holder.amount.setTextColor(holder.itemView.context.getColor(R.color.expense_color))
        }

        holder.parentLayout.setOnClickListener {
            clickListener.onTransactionClick(transaction.ID) // Pass transaction ID on click
        }
    }

    fun updateTransactions(newTransactions: List<Transaction>) {
        val diffCallback = TransactionDiffCallback(transactionList, newTransactions)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.transactionList = newTransactions
        diffResult.dispatchUpdatesTo(this)
    }

    interface OnTransactionClickListener {
        fun onTransactionClick(transactionId: String)
    }

    private fun getCategoryImage(category: String): Int {
        return when (category) {
            "Food" -> R.drawable.grocery_icon
            "School" -> R.drawable.school_24px
            "Transportation" -> R.drawable.transpo_icon
            "Personal" -> R.drawable.spa_24px
            "Savings" -> R.drawable.savings_24px
            else -> R.drawable.atr_24px
            }
    }


    override fun getItemCount() = transactionList.size
}
