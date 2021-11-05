package com.example.testopengl

class vertex: xylist {

    var prev: vertex? = null
    var ear: vertex? = null

    constructor(): super() {
    }

    constructor(prev: vertex?, ear: vertex?): super() {
        this.prev = prev
        this.ear = ear
    }

    constructor(x: Int, y: Int, next: vertex?) {
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
        while(xx != null) {
            xxx.x = xx.x
            xxx.y = xx.y
            if (xx.next === xy) {       // close xylist
                xxx.next = this
                this.prev = xxx
                break
            }
            else if (xx.next != null) {
                xxx.next = vertex(xxx, null)
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
}