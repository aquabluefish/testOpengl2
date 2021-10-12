package com.example.testopengl

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.View

class CustomView : GLSurfaceView {
    constructor(cont: Context?) : super(cont) {}
    constructor(cont: Context?, attr: AttributeSet?) : super(cont, attr) {}

    init {
        this.setEGLContextClientVersion(2)
        this.setRenderer(GLRenderer())
        this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY)
        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

    override protected fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(widthSize, heightSize)
    }
}