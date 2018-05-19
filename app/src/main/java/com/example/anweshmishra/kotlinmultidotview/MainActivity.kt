package com.example.anweshmishra.kotlinmultidotview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.multidotview.MultiDotView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MultiDotView.create(this)
    }
}
