package com.example.myapplication

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout

class BackpackItems(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private val items: ImageView

    init {
        // 從XML文件中取得自定義的布局
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.component_backage_items, this, true)

        items = findViewById(R.id.Items)
    }

    fun setImageResource(resId: Int) {
        items.setImageResource(resId)

    }
}