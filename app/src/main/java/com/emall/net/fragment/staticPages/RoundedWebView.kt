package com.emall.net.fragment.staticPages

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.webkit.WebView

class RoundedWebView : WebView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private lateinit var roundedRect: RectF

    private val cornerRadius = 30f.dpToPx(context)

    private val pathPaint = Path().apply {
        fillType = Path.FillType.INVERSE_WINDING
    }

    private val porterDuffPaint = Paint().apply {
        color = Color.TRANSPARENT
        style = Paint.Style.FILL
        isAntiAlias = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    override fun onSizeChanged(newWidth: Int, newHeight: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight)
        roundedRect = RectF(0f, scrollY.toFloat(), width.toFloat(), (scrollY + height).toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        pathPaint.reset()
        pathPaint.addRoundRect(roundedRect, cornerRadius, cornerRadius, Path.Direction.CW)
        canvas.drawPath(pathPaint, porterDuffPaint)
    }
}


fun Float.dpToPx(context: Context): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)
}
