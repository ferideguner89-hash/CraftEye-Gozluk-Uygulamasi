package com.example.crafteye8.model

data class Address(
    var title: String = "",
    var detail: String = "",
    var lat: Double? = null,
    var lng: Double? = null,
    var city: String? = null,         // YENİ
    var district: String? = null,     // YENİ
    var neighborhood: String? = null,  // YENİ
    var apartment: String? = null    // YENİ ALAN
)