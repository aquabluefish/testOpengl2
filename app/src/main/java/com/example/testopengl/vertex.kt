package com.example.testopengl

import android.graphics.Point
import android.util.Log
import androidx.core.graphics.minus
import java.lang.Math.abs

class vertex : xylist {

    var prev: vertex? = null
    var ear: Boolean = false

    constructor() : super() {
    }

    constructor(prev: vertex?, ear: Boolean) : super() {
        this.prev = prev
        this.ear = ear
    }

    constructor(x: Int, y: Int, next: vertex?) : super() {
        this.x = x
        this.y = y
        this.next = next
    }

    constructor(vararg zz: Int) {
        var i = 0
        while (i < zz.size / 2 - 1) {
            next = vertex(zz[i * 2], zz[i * 2 + 1], next as vertex?)
            i++
        }
        x = zz[i * 2]
        y = zz[i * 2 + 1]
    }

    constructor(xy: xylist) {
        var xxx = this
        var xx: xylist? = xy
        while (xx != null) {
            xxx.x = xx.x
            xxx.y = xx.y
            if (xx.next === xy) {       // close xylist
                xxx.next = this
                this.prev = xxx
                break
            } else if (xx.next != null) {
                xxx.next = vertex(xxx, false)
                xxx = xxx.next as vertex
            }
            xx = xx.next
        }
    }

    // 頂点一覧表示用文字列（最後に頂点数を表示・Close状態ならマイナス値）
    // 逆方向（Prev方向）にdumpする
    fun dumpx(): String {
        var prevx: vertex? = this
        var str = ""
        while (prevx != null) {
            str += prevx.x.toString() + "," + prevx.y.toString() + " "
            if (prevx.prev === this) break
            prevx = prevx.prev
        }
        str += "(" + size().toString() + ")"
        return str
    }

    // Close状態のvertexに、最初の点と同じ座標の最後の点を追加してOpen状態にする
    override fun open(): Boolean {
        var next: vertex? = this
        while (next != null) {
            if (next.next === this) {
                next.next = vertex(this.x, this.y)
                this.prev = null
                return true
            }
            next = next.next as vertex?
        }
        return false
    }

    // 凹多角形の対角線チェック
    fun diagonal(p1: Point?, p2: Point?): Boolean {
        if (p1 == null || p2 == null) return false
        var next: vertex = this
        do {
            if ((next == p1 && next.next == p2) || (next == p2 && next.next == p1)) return false
            if (next != p1 && next.next != p1 && next != p2 && next.next != p2)
                if (isIntersect(p1, p2, next, next.next!!)) return false
            if (!pointInArea(median(p1,p2))) return false   //対角線が図形外ならダメ
            next = next.next as vertex
        } while (next != this)
        return true
    }

    fun earInit() {
        var next: vertex? = this
        while (next != null && next.next != null && next.prev != null) {
            next.ear = diagonal(next.prev!!, next.next!!)
            if (next.next === this) break
            next = next.next as vertex?
        }
    }

    // 凹多角形の三角形分割
    fun triangulate() {
        var vertices = this
        var nvertices = abs(size())
        var n = nvertices
        earInit()
        while (n > 3) {
            var v2 = vertices
            do {
                if (v2.ear) {
                    val v3 = v2.next as vertex
                    val v4 = v3.next as vertex
                    val v1 = v2.prev!!
                    val v0 = v1.prev!!
                    printDiagonal(v1, v3)
                    v1!!.ear = diagonal(v0, v3)
                    v3!!.ear = diagonal(v1, v4)
                    v1.next = v3
                    v3.prev = v1
                    vertices = v3
                    n--
                    break
                }
                v2 = v2.next as vertex
            } while (v2 != vertices)
        }
    }

    fun printDiagonal(p1: Point, p2: Point) {
        Log.d("testOpengl", "printDiagonal=" + p1 + p2)
    }

    //　凹多角形に対する点の内外判定（angleでacos使うのでちょっと遅いかも）
    fun pointInArea(target: Point?): Boolean {
        var result = 0.0f
        if (target == null) return false
        var next: vertex = this
        do {
            val l1 = next - target
            val l2 = next.next!! - target
            var angle = angle(l1,l2)
            val cross = cross(l1,l2)
            if (cross<0) angle *= -1
            result += angle
            next = next.next as vertex
        } while (next != this)
        return abs(result) >= 0.01f
    }


}