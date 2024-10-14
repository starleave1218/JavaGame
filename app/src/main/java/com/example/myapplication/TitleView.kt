package com.example.myapplication

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class TitleView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private val select: ImageView
    private val titleName: TextView

    init {
        // 從XML文件中取得自定義的布局
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.title_view, this, true)

        select = findViewById(R.id.select)
        titleName = findViewById(R.id.titleName)
    }

    fun setting(con: String) {
        titleName.text = con
    }

    fun visible(visible: Int) {
        select.visibility = visible
    }

    fun setTextColor(color: Int){
        titleName.setTextColor(color)
    }
}