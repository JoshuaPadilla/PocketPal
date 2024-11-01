package com.example.pocketpal

data class Transaction(var ID: String,
                        var title: String,
                       var type: String,
                       var amount: Double,
                       var category: String,
                       var date: String,
                       var note: String)
