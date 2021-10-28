package com.example.testopengl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import java.lang.Math.tan
import java.lang.Math.toRadians
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.egl.EGLConfig

class GLRenderer: GLSurfaceView.Renderer {
    lateinit var shape: Shape

    // 行列格納用配列
    private val mVPMat = FloatArray(16)
    private val mPMat = FloatArray(16)
    private val mVMat = FloatArray(16)

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        shape = Shape()
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        // ビューポート設定
        GLES20.glViewport(0, 0, width, height)
        // 画面のアスペクト比を算出
        val aspect = width.toFloat() / height
        val near = 1f
        val far = 100f
        val fovy = 100f
        val scale = (near * tan(toRadians(fovy*0.5))).toFloat() * height /2000f
        val x = scale * aspect
        val y = scale
        // ビュー変換行列を作成
        Matrix.setLookAtM(mVMat, 0, 10.0f, 10.0f, -10.0f,
            0f, 0f, 0f, 0f, 1.0f, 0.0f)
        // 透視投影変換行列を作成
//        Matrix.frustumM(mPMat, 0, -x, x, -y, y, near, far)
//        Matrix.frustumM(mPMat, 0, -1f, 1f, -1f, 1f, near, far)
        Matrix.orthoM(mPMat, 0, -10f, 10f, -10f, 10f, near, far)
        // 透視投影変換・ビュー変換を乗じて頂点座標の変換行列を作成
        Matrix.multiplyMM(mVPMat, 0, mPMat, 0, mVMat, 0)
        Log.d("testOpengl", "w=$width, h=$height, aspect=$aspect, scale=$scale x=$x y=$y")
    }

    override fun onDrawFrame(unused: GL10) {
        // 背景色を青色で塗潰す
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // 四角形を青色で
        var vpos = convertx(-0.5f, 0.5f,  -0.5f, -0.5f,  0.5f, -0.5f,  0.5f, 0.5f );
        var fcol = convertx(0.0f, 0.0f, 1.0f, 0.3f);
        shape.drawTriangle(fcol, vpos, mVPMat,0.5f);

        // 三角形を黄色で
        vpos = convertx(0.0f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f)
        vpos = convertx(-0.5f, 0.5f,  -0.5f, -0.5f,  0.5f, -0.5f,  0.5f, 0.5f );
        fcol = convertx(1.0f, 1.0f, 0.0f, 1.0f)
        shape.drawTriangle(fcol, vpos, mVPMat,0.0f)

        // 三角形を赤色で
        vpos = convertx(0.0f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f)
        vpos = convertx(-0.5f, 0.5f,  -0.5f, -0.5f,  0.5f, -0.5f,  0.5f, 0.5f );
        fcol = convertx(1.0f, 0.0f, 0.0f, 1.0f)
        shape.drawTriangle(fcol, vpos, mVPMat,-0.5f)
    }
}
