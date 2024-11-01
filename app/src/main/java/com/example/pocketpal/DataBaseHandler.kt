package com.example.pocketpal

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val DATABASE_NAME = "transactions.db"
val TABLE_NAME = "transactions"

val COLUMN_ID = "id"
val COLUMN_TITLE = "title"
val COLUMN_AMOUNT = "amount"
val COLUMN_DATE = "date"
val COLUMN_TYPE = "type"
val COLUMN_CATEGORY = "category"
val COLUMN_NOTE = "note"

class DataBaseHandler(var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID TEXT, " +
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_AMOUNT REAL, " +
                "$COLUMN_DATE TEXT, " +
                "$COLUMN_TYPE TEXT, " +
                "$COLUMN_CATEGORY TEXT, " +
                "$COLUMN_NOTE TEXT)"

        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addTransaction(transaction: Transaction) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_ID, transaction.ID)
            put(COLUMN_TITLE, transaction.title)
            put(COLUMN_TYPE, transaction.type)
            put(COLUMN_AMOUNT, transaction.amount) // Make sure this is Double
            put(COLUMN_CATEGORY, transaction.category)
            put(COLUMN_DATE, transaction.date)
            put(COLUMN_NOTE, transaction.note)
        }

        try {
            db.insertOrThrow(TABLE_NAME, null, contentValues)
        } catch (e: Exception) {
            e.printStackTrace()
            // Log the content values for debugging
            Log.e("Failed to insert values:", "$e")
        } finally {
            db.close()
        }
    }

    fun getAllTransactions(): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val idIndex = cursor.getColumnIndex(COLUMN_ID)
                val titleIndex = cursor.getColumnIndex(COLUMN_TITLE)
                val typeIndex = cursor.getColumnIndex(COLUMN_TYPE)
                val amountIndex = cursor.getColumnIndex(COLUMN_AMOUNT)
                val categoryIndex = cursor.getColumnIndex(COLUMN_CATEGORY)
                val dateIndex = cursor.getColumnIndex(COLUMN_DATE)
                val noteIndex = cursor.getColumnIndex(COLUMN_NOTE)

                val transaction = Transaction(
                    ID = if (idIndex >= 0) cursor.getString(idIndex) else "",
                    title = if (titleIndex >= 0) cursor.getString(titleIndex) else "",
                    type = if (typeIndex >= 0) cursor.getString(typeIndex) else "",
                    amount = if (amountIndex >= 0) cursor.getDouble(amountIndex) else 0.0,
                    category = if (categoryIndex >= 0) cursor.getString(categoryIndex) else "",
                    date = if (dateIndex >= 0) cursor.getString(dateIndex) else "",
                    note = if (noteIndex >= 0) cursor.getString(noteIndex) else ""
                )
                transactions.add(transaction)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return transactions.reversed()
    }

    fun updateTransaction(transaction: Transaction) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_TITLE, transaction.title)
            put(COLUMN_TYPE, transaction.type)
            put(COLUMN_AMOUNT, transaction.amount)
            put(COLUMN_CATEGORY, transaction.category)
            put(COLUMN_DATE, transaction.date)
            put(COLUMN_NOTE, transaction.note)
        }
        db.update(TABLE_NAME, contentValues, "$COLUMN_ID = ?", arrayOf(transaction.ID.toString()))
        db.close()
    }

    fun deleteTransaction(id: String) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    fun getTransactionsByType(type: String): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_TYPE = ?", arrayOf(type))

        if (cursor.moveToFirst()) {
            do {
                val idIndex = cursor.getColumnIndex(COLUMN_ID)
                val titleIndex = cursor.getColumnIndex(COLUMN_TITLE)
                val typeIndex = cursor.getColumnIndex(COLUMN_TYPE)
                val amountIndex = cursor.getColumnIndex(COLUMN_AMOUNT)
                val categoryIndex = cursor.getColumnIndex(COLUMN_CATEGORY)
                val dateIndex = cursor.getColumnIndex(COLUMN_DATE)
                val noteIndex = cursor.getColumnIndex(COLUMN_NOTE)

                val transaction = Transaction(
                    ID = if (idIndex >= 0) cursor.getString(idIndex) else "",
                    title = if (titleIndex >= 0) cursor.getString(titleIndex) else "",
                    type = if (typeIndex >= 0) cursor.getString(typeIndex) else "",
                    amount = if (amountIndex >= 0) cursor.getDouble(amountIndex) else 0.0,
                    category = if (categoryIndex >= 0) cursor.getString(categoryIndex) else "",
                    date = if (dateIndex >= 0) cursor.getString(dateIndex) else "",
                    note = if (noteIndex >= 0) cursor.getString(noteIndex) else ""
                )
                transactions.add(transaction)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return transactions.reversed()
    }

    fun getLastTransactionID(): String? {
        val db = this.readableDatabase
        var lastID: String? = null

        val cursor = db.rawQuery("SELECT $COLUMN_ID FROM $TABLE_NAME ORDER BY $COLUMN_ID DESC LIMIT 1", null)
        if (cursor.moveToFirst()) {
            lastID = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID))
        }

        cursor.close()
        db.close()
        return lastID
    }

    fun getTransactionByID(id: String): Transaction? {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = ?", arrayOf(id))

        var transaction: Transaction? = null

        if (cursor.moveToFirst()) {
            // Retrieve data for each field from the cursor
            val transactionID = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
            val type = cursor.getString(cursor.getColumnIndexOrThrow("type"))
            val amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"))
            val category = cursor.getString(cursor.getColumnIndexOrThrow("category"))
            val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
            val note = cursor.getString(cursor.getColumnIndexOrThrow("note"))

            // Construct the Transaction object with all fields
            transaction = Transaction(
                ID = transactionID,
                title = title,
                type = type,
                amount = amount,
                category = category,
                date = date,
                note = note
            )
        }

        cursor.close()
        db.close()
        return transaction
    }

}