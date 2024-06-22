package com.example.newsaz.ui.details

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebView
import androidx.core.view.MotionEventCompat


class WebViewClass : WebView {
    constructor(context: Context?) : super(context!!)

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    )

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    )

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //Check is required to prevent crash
//        if (MotionEventCompat.findPointerIndex(event, 0) == -1) {
//            return super.onTouchEvent(event)
//        }
        var pointerIndex = -1
        for (i in 0 until event.pointerCount) {
            if (event.getPointerId(i) == 0) {
                pointerIndex = i
                break
            }
        }
        if (pointerIndex == -1) {
            return super.onTouchEvent(event)
        }

        if (event.pointerCount >= 2) {
            requestDisallowInterceptTouchEvent(true)
        } else {
            requestDisallowInterceptTouchEvent(false)
        }

        return super.onTouchEvent(event)
    }

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
        requestDisallowInterceptTouchEvent(true)
    }
}