package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


class PlayerInfoLayout : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_info_layout)
        //啟用自定義的主題

        setTheme(R.style.AppTheme)

    }
}