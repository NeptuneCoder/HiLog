package com.hilog.hilog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.hilog.hiloglib.HiLog


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun onHiLogClick(view: View) {
        HiLog.i("9900")
    }

    fun startActivityB(view: View) {
        startActivity(Intent(this, BActivity::class.java))
    }


}