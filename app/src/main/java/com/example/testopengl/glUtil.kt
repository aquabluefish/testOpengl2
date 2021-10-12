package com.example.testopengl

import android.opengl.GLES20
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

    private const val DEFAULT_OFFSET = 0
    private const val INVALID = 0
    private const val FIRST_INDEX = 0

// Top Level 宣言による関数定義    ////

    fun ldShader(shaderType: Int, source: String?): Int {
        var shader = GLES20.glCreateShader(shaderType)
        if (shader != INVALID) {
            GLES20.glShaderSource(shader, source)
            GLES20.glCompileShader(shader)
            val compiled = IntArray(1)
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, DEFAULT_OFFSET)
            if (compiled[FIRST_INDEX] == INVALID) {
                Log.e(
                    "TestOpengl",
                    "ldShader:" + shaderType + ":" + GLES20.glGetShaderInfoLog(shader)
                )
                GLES20.glDeleteShader(shader)
                shader = INVALID
            }
        }
        return shader
    }

    fun convert(data: FloatArray): FloatBuffer {
        val bb: ByteBuffer = ByteBuffer.allocateDirect(data.size * 4)
        bb.order(ByteOrder.nativeOrder())
        val floatBuffer: FloatBuffer = bb.asFloatBuffer()
        floatBuffer.put(data)
        floatBuffer.position(0)
        return floatBuffer
    }

    fun convertx(vararg args: Float): FloatBuffer {
        return convert(args)
    }
