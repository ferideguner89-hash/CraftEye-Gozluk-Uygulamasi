package com.example.crafteye8
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val userName: String = "",
    val totalPrice: Double = 0.0,
    val date: String = "",
    var items: List<Item> = emptyList()
): Parcelable

