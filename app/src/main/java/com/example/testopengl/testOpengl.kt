package com.example.testopengl

import android.os.Bundle
import android.app.Activity
import android.widget.Toast
import android.view.View
import android.util.Log

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
            Log.d("ViewSize : ", "w = "+vvv.getWidth()+"  h = "+vvv.getHeight())
        }
    }
}
