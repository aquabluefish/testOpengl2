package com.example.testopengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import android.view.View

class CustomView : GLSurfaceView {
    constructor(cont: Context?) : super(cont) {}
    constructor(cont: Context?, attr: AttributeSet?) : super(cont, attr) {}

    init {
        this.setEGLContextClientVersion(2)
//        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.setRenderer(GLRenderer())
        this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY)
//        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

    override protected fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(widthSize, heightSize)
        Log.d("testOpengl", "w=$widthSize, h=$heightSize")
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val vvv = findViewById<View>(R.id.customView1)
        Log.d("ViewSizex : ", "w = "+vvv.getWidth()+"  h = "+vvv.getHeight())
        Log.d("ViewSizem : ", "w = "+getMeasuredWidth()+"  h = "+getMeasuredHeight())
    }


}