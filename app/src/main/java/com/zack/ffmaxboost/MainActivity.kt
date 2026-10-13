package com.zack.ffmaxboost

import android.app.Activity
import android.os.Bundle
import android.os.BatteryManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.*
import kotlin.math.roundToInt

class MainActivity : Activity() {
    private lateinit var temp: TextView
    private lateinit var status: TextView
    private val handler=Handler(Looper.getMainLooper())

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        buildUi()
        updateTemp()
    }

    private fun tv(text:String,size:Float=15f):TextView =
        TextView(this).apply {
            this.text=text; setTextColor(Color.WHITE); textSize=size
            setPadding(0,10,0,10)
        }

    private fun buildUi() {
        val root=LinearLayout(this).apply {
            orientation=LinearLayout.VERTICAL; setPadding(28,35,28,28)
            setBackgroundColor(Color.rgb(8,10,16))
        }
        root.addView(tv("FFMAX BOOST",28f))
        root.addView(tv("TECNO Spark 30 Pro • Helio G100 • 8 GB RAM",13f))
        temp=tv("Suhu: membaca...",16f); root.addView(temp)
        status=tv("Pilih preset performa.",14f); root.addView(status)

        val modes= arrayOf("ULTRA STABLE","BALANCED","BATTERY FRIENDLY")
        modes.forEach { mode ->
            val btn=Button(this).apply { text=mode; setOnClickListener { applyMode(mode) } }
            root.addView(btn,LinearLayout.LayoutParams(-1,58).apply{setMargins(0,8,0,0)})
        }

        val game=Button(this).apply {
            text="BUKA FREE FIRE MAX"
            setOnClickListener { launchGame() }
        }
        root.addView(game,LinearLayout.LayoutParams(-1,58).apply{setMargins(0,18,0,0)})

        val info=tv(
            "Optimizer ini tidak memodifikasi APK/data game, recoil, aim, damage, hitbox, atau anti-cheat. " +
            "Android juga tidak mengizinkan aplikasi biasa mematikan proses aplikasi lain atau mengubah thermal limit secara paksa. " +
            "Untuk HP yang mudah panas, jangan memaksa FPS maksimum dan hindari bermain sambil charging.",
            12f
        )
        info.setTextColor(Color.LTGRAY); root.addView(info)
        setContentView(root)
    }

    private fun applyMode(mode:String) {
        val advice=when(mode) {
            "ULTRA STABLE" -> "Smooth/Low + FPS stabil. Prioritas suhu dan frame-time."
            "BALANCED" -> "Smooth + FPS tinggi jika suhu tetap aman."
            else -> "Smooth/Low + FPS konservatif untuk sesi panjang."
        }
        status.text="Preset aktif: $mode\n$advice"
    }

    private fun updateTemp() {
        try {
            val bm=getSystemService(BATTERY_SERVICE) as BatteryManager
            val intent=registerReceiver(null, android.content.IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val raw=intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,-1) ?: -1
            if(raw>0) temp.text="Suhu baterai: ${raw/10.0} °C"
            else temp.text="Suhu: tidak tersedia"
        } catch(e:Exception){ temp.text="Suhu: tidak tersedia" }
        handler.postDelayed({updateTemp()},5000)
    }

    private fun launchGame() {
        val pm=packageManager
        val intent=pm.getLaunchIntentForPackage("com.dts.freefireth")
            ?: pm.getLaunchIntentForPackage("com.dts.freefiremax")
        if(intent!=null) startActivity(intent)
        else Toast.makeText(this,"Free Fire MAX tidak ditemukan.",Toast.LENGTH_SHORT).show()
    }
}
