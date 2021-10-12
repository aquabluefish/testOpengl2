package com.example.testopengl

import android.opengl.GLES20
import android.util.Log
import java.nio.FloatBuffer

class Shape {
    private val shaderProg: Int

    val vShaderCode = """
        attribute  vec2 vpos;
        uniform mat4 pmat;
        void main() {
          gl_Position = pmat * vec4(vpos, 0.0, 1.0);
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

    fun drawTriangle(fcol: FloatBuffer?, vpos: FloatBuffer) {
        GLES20.glUseProgram(shaderProg) // シェーダプログラム使用開始
        val pmatx = GLES20.glGetUniformLocation(shaderProg, "pmat") // uniform変数pmatのindex取得
        GLES20.glUniformMatrix4fv(pmatx,1,false,identityMatrix,0)
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