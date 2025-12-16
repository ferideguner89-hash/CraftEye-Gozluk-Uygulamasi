package com.example.crafteye8

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    val name: String = "",
    val price: Double = 0.0,
    val count: Int = 0
): Parcelable
