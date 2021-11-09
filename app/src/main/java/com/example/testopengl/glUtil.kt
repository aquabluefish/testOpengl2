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

/*---------------------------------------------------------------------
**** from triphilvaz
---------------------------------------------------------------------*/
fun AreaSign(a: Point, b: Point, c: Point): Int {
    val area2: Double
    area2 = (b.x - a.x) * (c.y - a.y) as Double -
            (c.x - a.x) * (b.y - a.y) as Double
    // The area should be an integer
    return if (area2 > 0.5) 1 else if (area2 < -0.5) -1 else 0
}

/*---------------------------------------------------------------------
Exclusive or: TRUE iff exactly one argument is true.
---------------------------------------------------------------------*/
fun Xor(x: Boolean, y: Boolean): Boolean {
    // The arguments are negated to ensure that they are 0/1 values
    // (Idea due to Michael Baldwin)
    return !x xor !y
}

/*---------------------------------------------------------------------
Returns true iff ab properly intersects cd: they share
a point interior to both segments.  The properness of the
intersection is ensured by using strict leftness.
---------------------------------------------------------------------*/
// セグメントa-b,c-dが正しく交差していることをチェック
fun IntersectProp(a: Point, b: Point, c: Point, d: Point): Boolean {
    /* Eliminate improper cases. */
    if (Collinear(a, b, c) ||
        Collinear(a, b, d) ||
        Collinear(c, d, a) ||
        Collinear(c, d, b)) return false
    return Xor(Left(a, b, c), Left(a, b, d)) && Xor(
        Left(c, d, a),
        Left(c, d, b)
    )
}

/*---------------------------------------------------------------------
Returns true iff c is strictly to the left of the directed
line through a to b.
---------------------------------------------------------------------*/
// ｃがセグメントa-bの左側であることをチェック
fun Left(a: Point, b: Point, c: Point): Boolean {
    return AreaSign(a, b, c) > 0
}

fun LeftOn(a: Point, b: Point, c: Point): Boolean {
    return AreaSign(a, b, c) >= 0
}

fun Collinear(a: Point, b: Point, c: Point): Boolean {
    return AreaSign(a, b, c) === 0
}

/*---------------------------------------------------------------------
Returns TRUE iff point c lies on the closed segement ab.
First checks that c is collinear with a and b.
---------------------------------------------------------------------*/
// 点cがセグメントa-b上にあることをチェック
fun Between(a: Point, b: Point, c: Point): Boolean {
    // tPointi ba, ca;   not used
    if (!Collinear(a, b, c)) return false

    // If ab not vertical, check betweenness on x; else on y
    if (a.x !== b.x)
        return a.x <= c.x && c.x <= b.x ||
            a.x >= c.x && c.x >= b.x
    else
        return a.y <= c.y && c.y <= b.y ||
            a.y >= c.y && c.y >= b.y
}

/*---------------------------------------------------------------------
Returns TRUE iff segments ab and cd intersect, properly or improperly.
---------------------------------------------------------------------*/
// セグメントa-b,c-dの交差チェック
fun Intersect(a: Point, b: Point, c: Point, d: Point): Boolean {
    if (IntersectProp(a, b, c, d)) return true
    else if (Between(a, b, c)
        || Between(a, b, d)
        || Between(c, d, a)
        || Between(c, d, b)) return true
    else
        return false
}

/*---------------------------------------------------------------------
Returns TRUE iff (a,b) is a proper internal or external
diagonal of P, ignoring edges incident to a and b.
---------------------------------------------------------------------*/
// 正しい内部或いは外部対角線であることをチェック
fun Diagonalie(a: Point, b: Point, vertices: vertex): Boolean {
    var c: vertex
    var c1: vertex

    // For each edge (c,c1) of P
    c = vertices
    do {
        c1 = c.next as vertex
        // Skip edges incident to a or b
        if (c !== a && c1 !== a && c !== b && c1 !== b
            && Intersect(
                a,
                b,
                c,
                c1
            )) return false
        c = c.next as vertex
    } while (c !== vertices)
    return true
}

/*---------------------------------------------------------------------
Returns TRUE iff the diagonal (a,b) is strictly internal to the
polygon in the neighborhood of the a endpoint.
---------------------------------------------------------------------*/
// 対角線が内側にあることをチェック
fun InCone(a: vertex, b: vertex): Boolean {
    val a0: vertex
    val a1: vertex // a0,a,a1 are consecutive vertices
    a1 = a.next as vertex
    a0 = a.prev!!

    // If a is a convex vertex ...
    if (LeftOn(a, a1, a0))
        return Left(a, b, a0) && Left(b, a, a1)
    // Else a is reflex:
    return !(LeftOn(a, b, a1) && LeftOn(b, a, a0))
}

/*---------------------------------------------------------------------
Returns TRUE iff (a,b) is a proper internal diagonal.
---------------------------------------------------------------------*/
// 正しい内部対角線であることをチェック
fun Diagonal(a: vertex, b: vertex, vertices: vertex): Boolean {
    return InCone(a, b) && InCone(b, a) && Diagonalie(a, b, vertices)
}
