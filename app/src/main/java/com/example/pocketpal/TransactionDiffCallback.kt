package com.example.pocketpal

import androidx.recyclerview.widget.DiffUtil
import com.example.pocketpal.Transaction

class TransactionDiffCallback(
    private val oldList: List<Transaction>,
    private val newList: List<Transaction>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Compare IDs or some unique identifier to check if items are the same
        return oldList[oldItemPosition].ID == newList[newItemPosition].ID
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Compare the content of the items
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}