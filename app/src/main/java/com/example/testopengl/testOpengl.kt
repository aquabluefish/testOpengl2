package com.example.testopengl

import android.os.Bundle
import android.app.Activity
import android.graphics.Point
import android.widget.Toast
import android.view.View
import android.util.Log
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.graphics.minus
import androidx.core.graphics.plus

class testOpengl : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        findViewById<View>(R.id.button1).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@testOpengl, "Hello World!", Toast.LENGTH_LONG).show()
            }
        })

        val vvv = findViewById<View>(R.id.customView1)
        vvv.getViewTreeObserver().addOnGlobalLayoutListener {
            Log.d("ViewSize : ", "w = " + vvv.getWidth() + "  h = " + vvv.getHeight())
        }

        var xxx = xylist(0, 0, 1, 0, 1, 1, 0, 1, 0, 0)
        var yyy = vertex(xxx)

        Log.d("testOpengl", "xxx=" + xxx.dump())
        Log.d("testOpengl", "yyy=" + yyy.dump())
        yyy = yyy.reverse() as vertex
        Log.d("testOpengl", "yyy=" + yyy.dump())

        var ppp = xylist(
            500,
            500,
            500,
            500,
            1500,
            500,
            1500,
            0,
            1500,
            -500,
            500,
            -500,
            500,
            -1500,
            -500,
            -1500,
            -500,
            -500,
            -2000,
            -500,
            -1500,
            -500,
            -500,
            500,
            -500,
            500,
            -500,
            1500,
            -1000,
            1500,
            500,
            1500,
            500,
            500,
            500,
            500
        )
        ppp = ppp.reverse()!!
        Log.d("testOpengl", "ppp0=" + ppp[0])
        Log.d("testOpengl", "ppp1=" + ppp[1])
        Log.d("testOpengl", "ppp2=" + ppp[2])
        Log.d("testOpengl", "ppp3=" + ppp[3])
//        ppp[4] = xylist(444,555)
        if (ppp is xylist) {
            Log.d("testOpengl", "ppp=" + ppp.dump())
            Log.d("testOpengl", "ccw(p0)=" + ccw(ppp[0]))
            Log.d("testOpengl", "ccw(p1)=" + ccw(ppp[1]))
            Log.d("testOpengl", "ccw(p2)=" + ccw(ppp[2]))
            Log.d("testOpengl", "ccw(p3)=" + ccw(ppp[3]))
            Log.d("testOpengl", "ccw(p4)=" + ccw(ppp[4]))
            Log.d("testOpengl", "ccw(p5)=" + ccw(ppp[5]))
            Log.d("testOpengl", "ccw(p6)=" + ccw(ppp[6]))
            Log.d("testOpengl", "ccw(p12)=" + ccw(ppp[12]))
            Log.d("testOpengl", "ppp.size=" + ppp.size())
            Log.d("testOpengl", "ppp=" + ppp.dump())
            ppp.close()
            Log.d("testOpengl", "pppc=" + ppp.dump())
            ppp.trim0()
            Log.d("testOpengl", "ppp0=" + ppp.dump())
            ppp = ppp.trim()!!
            Log.d("testOpengl", "pppx=" + ppp.dump())
            Log.d("testOpengl", "ppp.size=" + ppp.size())
            var qqq = vertex(ppp)
            Log.d("testOpengl", "qqq=" + qqq.dump())
            Log.d("testOpengl", "qqqx=" + qqq.dumpx())
            Log.d("testOpengl", "isIntersect(q0,q1)=" + isIntersect(qqq[0], qqq[1]))
            Log.d("testOpengl", "isIntersect(q0,q2)=" + isIntersect(qqq[0], qqq[2]))
            Log.d("testOpengl", "isIntersect(q0,q3)=" + isIntersect(qqq[0], qqq[3]))
            Log.d("testOpengl", "isIntersect(q0,q4)=" + isIntersect(qqq[0], qqq[4]))
            Log.d("testOpengl", "isIntersect(q0,q10)=" + isIntersect(qqq[0], qqq[10]))
            Log.d("testOpengl", "isIntersect(q0,q11)=" + isIntersect(qqq[0], qqq[11]))
            Log.d("testOpengl", "isIntersect(q0,q12)=" + isIntersect(qqq[0], qqq[12]))
        }

    }
}

