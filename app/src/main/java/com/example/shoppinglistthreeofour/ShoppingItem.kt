package com.example.shoppinglistthreeofour

data class ShoppingItem(
    val id: Int,
    var title: String,
    var description: String,
    var quantity: Int,
    var isEditing: Boolean = false
)
