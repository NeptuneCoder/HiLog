package com.hilog.hilog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.hilog.hiloglib.HiLog
import kotlinx.coroutines.coroutineScope

class BActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun onHiLogClick(view: View) {
        HiLog.a("9900")
    }

    fun startActivityB(view: View) {
        startActivity(Intent(this, BActivity::class.java))
    }
}