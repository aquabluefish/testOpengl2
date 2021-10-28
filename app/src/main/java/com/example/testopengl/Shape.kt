package com.example.testopengl

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.FloatBuffer
import java.lang.Math.*
import javax.microedition.khronos.opengles.GL10

class Shape {
    private val shaderProg: Int

    val vShaderCode = """
        attribute  vec2 vpos;
        uniform mat4 xmat;
        uniform mat4 rmat;
        uniform mat4 vpmat;
        uniform float zzz;
        void main() {
          gl_Position = vpmat * rmat * xmat * vec4(vpos, zzz, 1.0);
        }
    """
    val fShaderCode = """
        precision mediump float;
        uniform lowp vec4 fcol;
        void main() {
          gl_FragColor = fcol;
        }
    """

    val identityMatrix =  floatArrayOf(
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f
    )

    fun rotateX(degree: Float):FloatArray {
        val radian = toRadians(degree.toDouble())
        val sin = sin(radian).toFloat()
        val cos = cos(radian).toFloat()
        val m =  floatArrayOf(
            1.0f,   0.0f,   0.0f,   0.0f,
            0.0f,   +cos,   -sin,   0.0f,
            0.0f,   +sin,   +cos,   0.0f,
            0.0f,   0.0f,   0.0f,   1.0f
        )
        return m
    }

    fun rotateY(degree: Float):FloatArray {
        val radian = toRadians(degree.toDouble())
        val sin = sin(radian).toFloat()
        val cos = cos(radian).toFloat()
        val m =  floatArrayOf(
            +cos,   0.0f,   +sin,   0.0f,
            0.0f,   1.0f,   0.0f,   0.0f,
            -sin,   0.0f,   +cos,   0.0f,
            0.0f,   0.0f,   0.0f,   1.0f
        )
        return m
    }

    fun rotateZ(degree: Float):FloatArray {
        val radian = toRadians(degree.toDouble())
        val sin = sin(radian).toFloat()
        val cos = cos(radian).toFloat()
        val m =  floatArrayOf(
            +cos,   -sin,   0.0f,   0.0f,
            +sin,   +cos,   0.0f,   0.0f,
            0.0f,   0.0f,   1.0f,   0.0f,
            0.0f,   0.0f,   0.0f,   1.0f
        )
        return m
    }

    fun moveMat(xxxx: Float, yyyy: Float, zzzz: Float):FloatArray {
        val m =  floatArrayOf(
            1.0f,   0.0f,   0.0f,   xxxx,
            0.0f,   1.0f,   0.0f,   yyyy,
            0.0f,   0.0f,   1.0f,   zzzz,
            0.0f,   0.0f,   0.0f,   1.0f
        )
        return m
    }

    fun scaleMat(xxxx: Float, yyyy: Float, zzzz: Float):FloatArray {
        val m =  floatArrayOf(
            xxxx,   0.0f,   0.0f,   0.0f,
            0.0f,   yyyy,   0.0f,   0.0f,
            0.0f,   0.0f,   zzzz,   0.0f,
            0.0f,   0.0f,   0.0f,   1.0f
        )
        return m
    }

    init {
        val vShader = ldShader(GLES20.GL_VERTEX_SHADER, vShaderCode) // バーテックスシェーダコンパイル
        val fShader = ldShader(GLES20.GL_FRAGMENT_SHADER, fShaderCode) // フラグメントシェーダコンパイル
        shaderProg = GLES20.glCreateProgram() // シェーダプログラム生成
        GLES20.glAttachShader(shaderProg, vShader) // バーテックスシェーダを追加
        GLES20.glAttachShader(shaderProg, fShader) // フラグメントシェーダを追加
        GLES20.glLinkProgram(shaderProg) // シェーダプログラムをリンク
        GLES20.glDeleteShader(vShader) // シェーダオブジェクト開放
        GLES20.glDeleteShader(fShader)
    }

    fun drawTriangle(fcol: FloatBuffer?, vpos: FloatBuffer, vpmat: FloatArray, zzz: Float) {
        val angle = ((System.currentTimeMillis()*0.1)%360).toFloat()
        GLES20.glUseProgram(shaderProg) // シェーダプログラム使用開始

        val zzzx = GLES20.glGetUniformLocation(shaderProg, "zzz") // uniform変数zzzのindex取得
        GLES20.glUniform1f(zzzx,zzz)

        val rmatx = GLES20.glGetUniformLocation(shaderProg, "rmat") // uniform変数pmatのindex取得
        GLES20.glUniformMatrix4fv(rmatx,1,false,rotateZ(angle*0),0)

        val vpmatx = GLES20.glGetUniformLocation(shaderProg, "vpmat")
        GLES20.glUniformMatrix4fv(vpmatx, 1, false, vpmat,0)

        val xmatx = GLES20.glGetUniformLocation(shaderProg, "xmat")
        GLES20.glUniformMatrix4fv(xmatx, 1, false, scaleMat(10f,10f,10f),0)

        val fcolx = GLES20.glGetUniformLocation(shaderProg, "fcol") // uniform変数fcolのindex取得
        GLES20.glUniform4fv(fcolx, 1, fcol) // 色情報の場所と書式を設定

        val vposx = GLES20.glGetAttribLocation(shaderProg, "vpos") // attribute変数vposのindex取得
        GLES20.glEnableVertexAttribArray(vposx) // attribute変数のアクセスを有効に
        GLES20.glVertexAttribPointer(
            vposx,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            vpos
        ) // 頂点情報のindexと頂点毎のデータ数2を設定
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vpos.capacity() / 2) // 開始位置と頂点数3を指定して描画
        GLES20.glDisableVertexAttribArray(vposx) // attribute変数と頂点情報の対応付けを無効に
        GLES20.glUseProgram(0) // シェーダプログラム使用終了
    }
}