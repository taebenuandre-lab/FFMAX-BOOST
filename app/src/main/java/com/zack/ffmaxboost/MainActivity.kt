package com.zack.ffmaxboost

import android.app.*
import android.os.*
import android.content.*
import android.graphics.Color
import android.view.*
import android.widget.*
import kotlin.math.roundToInt

class MainActivity: Activity(){
    private lateinit var temp:TextView
    private lateinit var advice:TextView
    private lateinit var bar:ProgressBar
    private val h=Handler(Looper.getMainLooper())

    override fun onCreate(b:Bundle?){super.onCreate(b);ui();tick()}

    fun text(s:String,z:Float=15f)=TextView(this).apply{
        text=s;textSize=z;setTextColor(Color.WHITE);setPadding(0,8,0,8)
    }

    private fun ui(){
        val root=LinearLayout(this).apply{
            orientation=LinearLayout.VERTICAL;setPadding(24,30,24,24);setBackgroundColor(Color.rgb(7,9,16))
        }
        root.addView(text("FFMAX BOOST V2",29f))
        root.addView(text("SPARK 30 PRO • HELIO G100 • 8 GB RAM",12f))
        temp=text("Suhu baterai: --",16f);root.addView(temp)
        bar=ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal)
        root.addView(bar,LinearLayout.LayoutParams(-1,8))
        advice=text("Pilih mode untuk menyiapkan sesi bermain.",14f);root.addView(advice)

        val modes=listOf(
            "COOL • PRIORITAS SUHU" to "Smooth/Low • FPS konservatif • cocok untuk HP yang cepat panas.",
            "STABLE • REKOMENDASI" to "Smooth/Low • FPS tinggi jika stabil • fokus frame-time konsisten.",
            "PERFORMANCE • SESI PENDEK" to "FPS tinggi • gunakan hanya saat suhu tetap aman."
        )
        modes.forEach{(name,desc)->
            val b=Button(this);b.text=name
            b.setOnClickListener{
                advice.text="$name\n$desc\n\nSebelum mulai: tutup aplikasi berat, aktifkan Game Mode, dan hindari charging."
                Toast.makeText(this,"Mode $name dipilih",Toast.LENGTH_SHORT).show()
            }
            root.addView(b,LinearLayout.LayoutParams(-1,56).apply{setMargins(0,8,0,0)})
        }
        val launch=Button(this);launch.text="BUKA FREE FIRE MAX"
        launch.setOnClickListener{openGame()};root.addView(launch,LinearLayout.LayoutParams(-1,58).apply{setMargins(0,18,0,0)})

        val note=text("Peringatan: aplikasi ini tidak mengubah thermal limit Android atau memaksa CPU/GPU. Jika suhu naik tinggi, turunkan FPS/grafik dan istirahatkan perangkat. Jangan bermain sambil charging jika perangkat panas.",12f)
        note.setTextColor(Color.LTGRAY);root.addView(note)
        setContentView(root)
    }

    private fun tick(){
        val i=registerReceiver(null,IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val t=i?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,-1)?:-1
        if(t>0){
            val c=t/10.0
            temp.text="Suhu baterai: $c °C"
            bar.progress=(c.coerceIn(25.0,50.0)-25).div(25).times(100).roundToInt()
            if(c>=43) advice.text="SUHU TINGGI ($c °C) • turunkan FPS/grafik dan istirahatkan HP."
            else if(c>=40) advice.text="Suhu mulai tinggi ($c °C) • prioritaskan mode COOL/STABLE."
        } else temp.text="Suhu baterai: tidak tersedia"
        h.postDelayed({tick()},3000)
    }

    private fun openGame(){
        val pm=packageManager
        val i=pm.getLaunchIntentForPackage("com.dts.freefiremax")
            ?:pm.getLaunchIntentForPackage("com.dts.freefireth")
        if(i!=null)startActivity(i)
        else Toast.makeText(this,"Free Fire MAX tidak ditemukan.",Toast.LENGTH_SHORT).show()
    }
}
