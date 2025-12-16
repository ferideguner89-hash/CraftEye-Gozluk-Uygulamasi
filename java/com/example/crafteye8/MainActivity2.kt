package com.example.crafteye8

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.crafteye8.databinding.ActivityMain2Binding



class MainActivity2 : AppCompatActivity() {

    private lateinit var tasarim2: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Edge-to-edge
        enableEdgeToEdge()

        // 2. Binding kur
        tasarim2 = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(tasarim2.root)

        // 3. BottomNav referansı
        val bottomNav = tasarim2.bottomNav

        // 4. İlk açılışta default fragment göster (isteğe bağlı)
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment2, A4Fragment())
            .commit()

        // 5. Bottom bar tıklanınca MANUEL fragment geçişi
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.fragment4 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment2, A4Fragment())
                        .commit()
                    true
                }

                R.id.a5Fragment3 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment2, A5Fragment())
                        .commit()
                    true
                }

                R.id.a6Fragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment2, A6Fragment())
                        .commit()
                    true
                }

                else -> false
            }
        }

        // 6. Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(tasarim2.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
