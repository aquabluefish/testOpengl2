package com.example.testopengl

import android.graphics.Point
import android.opengl.GLES20
import android.util.Log
import androidx.core.graphics.minus
import androidx.core.graphics.plus
import java.lang.Math.acos
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.abs
import kotlin.math.sqrt


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

private operator fun Point.times(i: Int): Point {
    return Point(this.x * i, this.y * i)
}

private operator fun Point.div(i: Int): Point {
    return Point((this.x / i).toInt(), (this.y / i).toInt())
}

fun norm(xy: Point): Int {
    return xy.x * xy.x + xy.y * xy.y
}

fun dnorm(xy: Point): Double {
    return (xy.x * xy.x + xy.y * xy.y).toDouble()
}

fun dot(p0: Point, p1: Point): Int {
    return p0.x * p1.x + p0.y * p1.y
}

fun cross(p0: Point, p1: Point): Int {
    return p0.x * p1.y - p0.y * p1.x
}

fun angle(p0: Point, p1: Point): Float {
    return acos(dot(p0, p1) / (sqrt(dnorm(p0)) * sqrt(dnorm(p1)))).toFloat()
}

// 中点を求める
fun median(p0: Point, p1: Point): Point {
    return (p0 + p1) / 2
}

//　逆時計回りチェック
fun ccw(p0: Point, p1: Point, p2: Point): Int {
    val EPS = 0
    var a = Point(p1.x - p0.x, p1.y - p0.y)
    var b = Point(p2.x - p0.x, p2.y - p0.y)
    if (cross(a, b) > EPS) return 1              // 点p2は線分p0-p1の反時計方向
    else if (cross(a, b) < EPS) return -1        // 点p2は線分p0-p1の時計方向
    else if (dot(a, b) < EPS) return 2           // 点p2は線分p0-p1の手前
    else if (norm(a) + EPS < norm(b)) return -2   // 点p2は線分p0-p1の先
    return 0                                    // 点p2は線分p0-p1上
}

fun ccw(xy: xylist?): Int? {
    if (xy != null && xy.next != null && xy.next!!.next != null) {
        return ccw(xy, xy.next!!, xy.next!!.next!!)
    }
    return null
}

// 四角形p1-p2-p3-p4と線分p5-p6の交差チェック
fun isIntersect(p1: Point, p2: Point, p3: Point, p4: Point, p5: Point, p6: Point): Boolean {
    return isIntersect(p1, p2, p5, p6) || isIntersect(p2, p3, p5, p6) ||
            isIntersect(p3, p4, p5, p6) || isIntersect(p4, p1, p5, p6)
}

// 線分p1-p2と線分p3-p4の交差チェック
fun isIntersect(p1: Point, p2: Point, p3: Point, p4: Point): Boolean {
    return ccw(p1, p2, p3) * ccw(p1, p2, p4) <= 0 && ccw(p3, p4, p1) * ccw(p3, p4, p2) <= 0
}

//　線分l1、l2の交差チェック
fun isIntersect(l1: xylist?, l2: xylist?): Boolean {
    if (l1 != null && l1.next != null && l2 != null && l2.next != null) {
        return isIntersect(l1, l1.next!!, l2, l2.next!!)
    }
    return false
}

// 線分p1-p2と線分p3-p4の交点
fun crossPoint(p1: Point, p2: Point, p3: Point, p4: Point): Point {
    var d1 = cross(p2 - p1, p4 - p3)
    var d2 = cross(p2 - p1, p4 - p1)
    if (abs(d1) == 0 && abs(d2) == 0) return p3
    return p3 + (p4 - p1) * (d2 / d1)
}

// 線分l1、l2の交点
fun crossPoint(l1: xylist?, l2: xylist?): Point? {
    if (l1 != null && l1.next != null && l2 != null && l2.next != null) {
        return crossPoint(l1, l1.next!!, l2, l2.next!!)
    }
    return null
}

// 線分p1-p2と線分p3-p4の直交判定
fun isOrthogonal(p1: Point, p2: Point, p3: Point, p4: Point): Boolean {
    return dot(p2 - p1, p4 - p3) == 0
}

// 線分l1、l2の直交判定
fun isOrthogonal(l1: xylist?, l2: xylist?): Boolean {
    if (l1 != null && l1.next != null && l2 != null && l2.next != null) {
        return isOrthogonal(l1, l1.next!!, l2, l2.next!!)
    }
    return false
}

// 線分p1-p2と線分p3-p4の平行判定
fun isParallel(p1: Point, p2: Point, p3: Point, p4: Point): Boolean {
    return cross(p2 - p1, p4 - p3) == 0
}

// 線分l1、l2の平行判定
fun isParallel(l1: xylist?, l2: xylist?): Boolean {
    if (l1 != null && l1.next != null && l2 != null && l2.next != null) {
        return isParallel(l1, l1.next!!, l2, l2.next!!)
    }
    return false
}

