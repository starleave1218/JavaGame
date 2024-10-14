package com.example.myapplication

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class InfoView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private val imageView: ImageView
    private val textView: TextView
    private val button: Button

    init {
        // 從XML文件中取得自定義的布局
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.info_view, this, true)

        imageView = findViewById(R.id.Item)
        textView = findViewById(R.id.info)
        button = findViewById(R.id.sure)
    }

    fun setView(resId: Int, info: String, equip: String) {
        imageView.setImageResource(resId)
        textView.text = info
        button.text = equip
    }

    fun setClick(click: Boolean, focus: Boolean) {
        button.isClickable = click
        button.isFocusable = focus
    }
}