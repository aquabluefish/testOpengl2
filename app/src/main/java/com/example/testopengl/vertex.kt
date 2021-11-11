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

    // xylistからvertexを作成する
    // trim済みのCCWのxylistを渡すようにする（areaSign2で確認する）
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
            if (!pointInArea(median(p1, p2))) return false   //対角線が図形外ならダメ
            next = next.next as vertex
        } while (next != this)
        return true
    }

    fun earInit() {
        var next: vertex? = this
        while (next != null && next.next != null && next.prev != null) {
            next.ear = diagonal(next.prev!!, next.next!! as vertex)
            if (next.next === this) break
            next = next.next as vertex?
        }
    }

    fun EarInit() {
        var next: vertex? = this
        while (next != null && next.next != null && next.prev != null) {
            next.ear = Diagonal(next.prev!!, next.next!! as vertex)
            if (next.next === this) break
            next = next.next as vertex?
        }
    }

    // 凹多角形の三角形分割（おそらく破壊的な読み込みをする）
    fun triangulate() {
        var vertices = this
        var n = abs(size())
        var m = n
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
                    m = n--
                    break
                }
                else if (--m < 0) return    // earが見つからない異常時用ループ脱出
                v2 = v2.next as vertex
            } while (v2 != vertices)
        }
    }

    fun Triangulate() {
        var vertices = this
        var n = abs(size())
        var m = n
        EarInit()
        while (n > 3) {
            var v2 = vertices
            do {
                if (v2.ear) {
                    val v3 = v2.next as vertex
                    val v4 = v3.next as vertex
                    val v1 = v2.prev!!
                    val v0 = v1.prev!!
                    printDiagonal(v1, v3)
                    v1!!.ear = Diagonal(v0, v3)
                    v3!!.ear = Diagonal(v1, v4)
                    v1.next = v3
                    v3.prev = v1
                    vertices = v3
                    m = n--
                    break
                }
                else if (--m < 0) return    // earが見つからない異常時用ループ脱出
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
            var angle = angle(l1, l2)
            val cross = cross(l1, l2)
            if (cross < 0) angle *= -1
            result += angle
            next = next.next as vertex
        } while (next != this)
        return abs(result) >= 0.01f
    }

    /*---------------------------------------------------------------------
    Returns TRUE iff (a,b) is a proper internal or external
    diagonal of P, ignoring edges incident to a and b.
    ---------------------------------------------------------------------*/
    // 正しい内部或いは外部対角線であることをチェック（ccwのみ対応）
    private fun Diagonalie(a: Point, b: Point): Boolean {
        var c: vertex
        var c1: vertex

        // For each edge (c,c1) of P
        c = this
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
        } while (c !== this)
        return true
    }

    /*---------------------------------------------------------------------
    Returns TRUE iff (a,b) is a proper internal diagonal.
    ---------------------------------------------------------------------*/
    // 正しい内部対角線であることをチェック（ccwのみ対応）
    private fun Diagonal(a: vertex?, b: vertex?): Boolean {
        if (a == null || b == null) return false
        return InCone(a, b) && InCone(b, a) && Diagonalie(a, b)
    }

    private fun AreaPoly2(): Int {
        var sum = 0
        val p: vertex
        var a: vertex
        p = this // Fixed
        a = p.next as vertex  // Moving
        do {
            sum += Area2(p, a, a.next!!)
            a = a.next as vertex
        } while (a.next !== this)
        return sum
    }

}

/*---------------------------------------------------------------------
**** from triphilvaz
---------------------------------------------------------------------*/
private fun AreaSign(a: Point, b: Point, c: Point): Int {
    val area2: Double
    area2 = (b.x - a.x) * (c.y - a.y).toDouble() - (c.x - a.x) * (b.y - a.y).toDouble()
    // The area should be an integer
    if (area2 > 0.5) return 1
    else if (area2 < -0.5) return -1
    else return 0
}

fun abc(aaa: Int): Int {
    return if (aaa > 0.5) 1 else if (aaa < -0.5) -1 else 0
}

/*---------------------------------------------------------------------
Returns twice the signed area of the triangle determined by a,b,c.
The area is positive if a,b,c are oriented ccw, negative if cw,
and zero if the points are collinear.
---------------------------------------------------------------------*/
// abcの符号付面積の2倍を返す（ccwが正となる）
private fun Area2(a: Point, b: Point, c: Point): Int {
    return (b.x - a.x) * (c.y - a.y) - (c.x - a.x) * (b.y - a.y)
}


/*---------------------------------------------------------------------
Exclusive or: TRUE iff exactly one argument is true.
---------------------------------------------------------------------*/
private fun Xor(x: Boolean, y: Boolean): Boolean {
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
private fun IntersectProp(a: Point, b: Point, c: Point, d: Point): Boolean {
    /* Eliminate improper cases. */
    if (Collinear(a, b, c) ||
        Collinear(a, b, d) ||
        Collinear(c, d, a) ||
        Collinear(c, d, b)) return false
    return Xor(Left(a, b, c), Left(a, b, d)) &&
            Xor(Left(c, d, a), Left(c, d, b))
}

/*---------------------------------------------------------------------
Returns true iff c is strictly to the left of the directed
line through a to b.
---------------------------------------------------------------------*/
// ｃがセグメントa-bの左側であることをチェック
private fun Left(a: Point, b: Point, c: Point): Boolean {
    return AreaSign(a, b, c) > 0
}

private fun LeftOn(a: Point, b: Point, c: Point): Boolean {
    return AreaSign(a, b, c) >= 0
}

private fun Collinear(a: Point, b: Point, c: Point): Boolean {
    return AreaSign(a, b, c) === 0
}

/*---------------------------------------------------------------------
Returns TRUE iff point c lies on the closed segement ab.
First checks that c is collinear with a and b.
---------------------------------------------------------------------*/
// 点cがセグメントa-b上にあることをチェック
private fun Between(a: Point, b: Point, c: Point): Boolean {
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
private fun Intersect(a: Point, b: Point, c: Point, d: Point): Boolean {
    if (IntersectProp(a, b, c, d)) return true
    else if (Between(a, b, c)
        || Between(a, b, d)
        || Between(c, d, a)
        || Between(c, d, b)) return true
    else
        return false
}


/*---------------------------------------------------------------------
Returns TRUE iff the diagonal (a,b) is strictly internal to the
polygon in the neighborhood of the a endpoint.
---------------------------------------------------------------------*/
// 対角線が内側にあることをチェック
private fun InCone(a: vertex, b: vertex): Boolean {
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

