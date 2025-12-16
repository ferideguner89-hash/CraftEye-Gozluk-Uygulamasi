package com.example.crafteye8

data class User(
    var id: String = "",       // Firebase Auth UID'si
    var name: String = "",      // Kullanıcı adı
    var email: String = "",     // Kullanıcı e-posta
    var isActive: Boolean = true // Hesap aktif/pasif durumu
)
