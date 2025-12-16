package com.example.crafteye8


    data class Product(
        var id: String = "",
        val name: String="",
        val gender:String="",
        val imageUrl:String="",
        val price: Double=0.0,
        val discountPrice: Double? = null, // İndirimli fiyat (yoksa null)
        val isDiscounted: Boolean = false, // İndirim etiketi için
        var isFavorite: Boolean = false,
        var isExpanded: Boolean = false,
        var features: String = "",
        var stock: Int = 0,
        var isActive: Boolean = true
    )

