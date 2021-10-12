package com.example.testopengl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.egl.EGLConfig

class GLRenderer: GLSurfaceView.Renderer {
    lateinit var shape: Shape

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        shape = Shape()
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        // ビューポート設定
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(unused: GL10) {
        // 背景色を青色で塗潰す
        GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // 三角形を赤色で
        var vpos: FloatBuffer = convertx(0.0f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f)
        var fcol: FloatBuffer = convertx(1.0f, 0.0f, 0.0f, 1.0f)
        // 描画する
        shape.drawTriangle(fcol, vpos)

        // 三角形を黄色で
        vpos = convertx(0.0f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f)
        fcol = convertx(1.0f, 1.0f, 0.0f, 1.0f)
        // 描画する
        shape.drawTriangle(fcol, vpos)

        vpos = convertx(-0.6f, 0.3f,  -0.6f, -0.3f,  0.6f, -0.3f,  0.6f, 0.3f );
        fcol = convertx(0.0f, 0.0f, 1.0f, 0.3f);
        shape.drawTriangle(fcol, vpos);
    }
}
