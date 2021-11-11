package com.example.testopengl

import android.graphics.Point

open class xylist : Point, Cloneable {
    var next: xylist? = null

    constructor() {
        x = 0
        y = 0
    }

    constructor(x: Int, y: Int, next: xylist?) {
        this.x = x
        this.y = y
        this.next = next
    }

    constructor(vararg zz: Int) {
        var i = 0
        while (i < zz.size / 2 - 1) {
            next = xylist(zz[i * 2], zz[i * 2 + 1], next)
            i++
        }
        x = zz[i * 2]
        y = zz[i * 2 + 1]
    }

    public override fun clone(): xylist {
        return try {
            val result = super.clone() as xylist
            if (next != null) result.next = next!!.clone()
            result
        } catch (e: CloneNotSupportedException) {
            throw InternalError(e.toString())
        }
    }

    open fun reverse(): xylist? {
        var head: xylist?
        var temp: xylist?
        var next: xylist? = this
        head = null
        while (next != null) {
            temp = head
            head = next
            next = next.next
            head.next = temp
            if (next === this) break
        }
        return head
    }

    open fun zone(zone: xylist?): xylist? {
        var zone = zone
        var next: xylist? = this
        // zoneがnullならば生成する
        if (zone == null) {
            zone = xylist(next!!.x, next!!.y, next!!.x, next!!.y)
            next = next!!.next
        }
        // 左下、右上の座標を求めてsizeリストを拡張
        while (next != null) {
            if (zone!!.x > next.x) zone.x = next.x
            if (zone!!.y > next.y) zone.y = next.y
            if (zone!!.next!!.x < next.x) zone.next!!.x = next.x
            if (zone!!.next!!.y < next.y) zone.next!!.y = next.y
            next = next.next
            if (next === this) break
        }
        return zone
    }

    open fun zone(): xylist? {
        return zone(null)
    }

    // xylistの頂点数を返す（Closeしている場合はマイナス値）
    open fun size(): Int {
        var size = 0
        var next: xylist? = this
        while (next != null) {
            size++
            next = next.next
            if (next === this) {
                size *= -1
                break
            }
        }
        return size
    }

    // 最後の頂点を返す
    open fun last(): xylist? {
        var next: xylist? = this
        while (next != null && next.next != null) {
            next = next.next
        }
        return next
    }

    // xylistをClose状態にする
    // 最初と最後が一致していれば最後の点を削除して閉じるが、一致していなくてもそのまま閉じる
    open fun close(): Boolean {
        var next: xylist? = this
        while (next != null && next.next != null) {
            if (next.next!!.next == null) {
                if (next.next == this) {
                    next.next = this
                    return true
                }
                next.next!!.next = this
                return true
            }
            next = next.next
            if (next === this) return true
        }
        return false
    }

    // Close状態のxylistに、最初の点と同じ座標の最後の点を追加してOpen状態にする
    open fun open(): Boolean {
        var next: xylist? = this
        while (next != null) {
            if (next.next === this) {
                next.next = xylist(this.x, this.y)
                return true
            }
            next = next.next
        }
        return false
    }

    // Close処理後、冗長点削除（同一点削除も含む）
    open fun trim(): xylist? {
        var next: xylist? = this
        var head: xylist? = this
        var loop = 0
        trim0()
        if (size() > 0) close()
        var size = -size()
        if (size < 0) return null
        while (next != null) {
            when (ccw(next)) {
                0, -2, 2 -> {
                    if (next.next === this) head = this
                    next.next = next.next!!.next
                    size--
                    loop = 0
                }
                null -> return null
            }
            next = next.next
            loop++
            if (loop > size) break
        }
        return head
    }

    // 冗長点削除・連続同一点のみ削除
    open fun trim0(): xylist? {
        var next: xylist? = this
        while (next != null && next!!.next != null && next.next !== this) {
            if (next == next.next) {
                next.next = next.next!!.next
            }
            next = next!!.next
            if (next === this) break
        }
        return this
    }

    // 頂点一覧表示用文字列（最後に頂点数を表示・Close状態ならマイナス値）
    open fun dump(): String {
        var next: xylist? = this
        var str = ""
        while (next != null) {
            str += next.x.toString() + "," + next.y.toString() + " "
            if (next.next === this) break
            next = next.next
        }
        str += "(" + size().toString() + ") S=" + areaSign2().toString()
        return str
    }

    // 面積の２倍を返す（CCWなら正,CWなら負となる）
    fun areaSign2(): Double {
        var s: Double = 0.0
        var next = this
        while (next != null && next.next != null) {
            s += cross(next, next.next!!)
            if (next.next === this) break
            next = next.next!!
        }
        return s
    }

    companion object {
        var zero = xylist(0, 0, null)
    }

    open operator fun times(i: Int): Point {
        return Point(this.x * i, this.y * i)
    }


    // xylist[index]の取得
    operator fun get(index: Int): xylist? {
        var i = 0
        var next: xylist? = this
        while (next != null) {
            if (i == index) return next
            next = next.next
            if (next === this) return null
            i++
        }
        return next
    }

    // xylist[index] = xyの設定
    operator fun set(index: Int, xy: Point): xylist? {
        var i = 0
        var next: xylist? = this
        while (next != null) {
            if (i == index) {
                next.x = xy.x
                next.y = xy.y
                return next
            }
            next = next.next
            if (next === this) return null
            i++
        }
        return next
    }

//    operator fun plus(xxx: Point): Point {
//        return Point(this.x-xxx.x, this.y-xxx.y)
//    }
//
//    operator fun minus(xxx: xylist): Point {
//        return Point(this.x-xxx.x, this.y-xxx.y)
//    }

}
