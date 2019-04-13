package com.movesense.samples.sensorsample

import android.content.BroadcastReceiver
import android.content.Context
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.IntentFilter
import android.widget.ImageView
import android.support.v4.content.LocalBroadcastManager




class MainActivity : AppCompatActivity() {

    private lateinit var snare: MediaPlayer
    private lateinit var bass: MediaPlayer
    private lateinit var hihat: MediaPlayer

    private var BASS_STATUS = -1
    private var SNARE_STATUS = -1
    private var HIHAT_STATUS = -1

    override fun onCreate (savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        snare = MediaPlayer.create(this, R.raw.snare)
        bass = MediaPlayer.create(this, R.raw.bass)
        hihat = MediaPlayer.create(this, R.raw.hihat)

        val snareButton = findViewById(R.id.snare) as ImageView
        val bassButton = findViewById(R.id.bass) as ImageView
        val hihatButton = findViewById(R.id.hihat) as ImageView

        //drums.put("Snare", "0C:8C:DC:2C:4A:8B");
        //drums.put("Hi-Hat", "0C:8C:DC:2B:53:28");
        //drums.put("Bass", "0C:8C:DC:2C:4A:B7");
        //drums.put("Drum", "0C:8C:DC:2C:4A:B9")
        //drums.put("Temp", "0C:8C:DC:2C:4A:D5")


        var filter = IntentFilter()
        filter.addAction("com.example.Broadcast")

        var broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {
                var mac : String = intent!!.getExtras().keySet().first()
                var code = intent.getExtras().getInt(mac)
                when(mac) {
                    "0C:8C:DC:2C:4A:8B" -> SNARE_STATUS = code
                    "0C:8C:DC:2B:53:28" -> HIHAT_STATUS = code
                    "0C:8C:DC:2C:4A:B7" -> BASS_STATUS = code
                    else -> return
                }
            }
        }
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.registerReceiver(broadcastReceiver, filter)

        snareButton.setOnClickListener {
            //ConnectActivity.connectToDrum("0C:8C:DC:2C4A:8B")
            val intent = Intent(this@MainActivity, ConnectService::class.java)
            intent.putExtra("macAddress", "0C:8C:DC:2C:4A:8B")
            startService(intent)
        }

        bassButton.setOnClickListener {
            val intent = Intent(this@MainActivity, ConnectService::class.java)
            intent.putExtra("macAddress", "0C:8C:DC:2C:4A:B7")
            startService(intent)
        }

        hihatButton.setOnClickListener {
            val intent = Intent(this@MainActivity, ConnectService::class.java)
            intent.putExtra("macAddress", "0C:8C:DC:2B:53:28")
            startService(intent)
        }
    }


    override fun onDestroy () {
        super.onDestroy ()
        snare.release ()
        bass.release ()
        hihat.release ()
    }
}

class ConnectReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        println("FOO")
    }
}
