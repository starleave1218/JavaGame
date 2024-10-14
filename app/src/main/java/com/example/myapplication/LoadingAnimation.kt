package com.example.myapplication

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable


class LoadingAnimation constructor(private val context: Activity, private val animationName: String = "loading.json") {

    // 現有視圖中最底層的視圖
    private val rootView = context.window.decorView.findViewById<ViewGroup>(android.R.id.content)

    // 畫面的layout與設定layout的參數
    private val rLayout: LinearLayout = LinearLayout(context)


    // loading動畫container
    private val loadingContainer: RelativeLayout = RelativeLayout(context)

    // loading動畫與參數
    private val lottieAnimationView: LottieAnimationView = LottieAnimationView(context)
    private val lLayoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.MATCH_PARENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT
    )

    // loading文字
    private val loadingTextTV: TextView = TextView(context)

    init {
        initLayout()
        initLoadingImage()
        initLoadingText()
        rLayout.addView(loadingTextTV)
        loadingContainer.addView(lottieAnimationView)
    }

    private fun initLayout() {
        rLayout.orientation = LinearLayout.VERTICAL

        // 設定背景為黑色，alpha值0.8
        rLayout.setBackgroundColor(Color.BLACK)
        //rLayout.alpha = 0.8F
    }

    private fun initLoadingImage() {
        // 包住loading的container，主要是用來做定位
        val layoutParams = lLayoutParams
        layoutParams.setMargins(0, 130, 0, 0) // 調整動畫距離
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE) // 水平置中
        loadingContainer.layoutParams = layoutParams
        rLayout.addView(loadingContainer)

        // loading動畫主體
        lottieAnimationView.setAnimation(animationName)
        lottieAnimationView.layoutParams = lLayoutParams

        // 添加動畫監聽器
        lottieAnimationView.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                stop()
            }
        })
    }

    private fun initLoadingText() {
        // 加載自訂義字體
        val customTypeface = ResourcesCompat.getFont(context, R.font.yujiboku_regular)
        // 載入中文字
        loadingTextTV.text = "努力加載中..."
        loadingTextTV.setTextColor(ContextCompat.getColor(context, R.color.white))
        loadingTextTV.gravity = Gravity.CENTER
        loadingTextTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21F)

        // 設置自訂義字體
        customTypeface?.let {
            loadingTextTV.typeface = it
        }
    }

    fun start() {
        rootView.addView(rLayout)
        lottieAnimationView.repeatCount = LottieDrawable.INFINITE
        lottieAnimationView.playAnimation()
    }

    fun stop() {
        rootView.removeView(rLayout)
        lottieAnimationView.cancelAnimation()
    }
}