package com.movesense.samples.sensorsample

import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent



class MainActivity : AppCompatActivity() {

    private lateinit var snare: MediaPlayer
    private lateinit var bass: MediaPlayer
    private lateinit var hihat: MediaPlayer

    override fun onCreate (savedInstanceState: Bundle?) {
        super.onCreate (savedInstanceState)
        setContentView (R.layout.activity_main)

        snare = MediaPlayer.create (this, R.raw.snare)
        bass = MediaPlayer.create (this, R.raw.bass)
        hihat = MediaPlayer.create (this, R.raw.hihat)

        val snareButton = findViewById (R.id.snare) as Button
        val bassButton = findViewById (R.id.bass) as Button
        val hihatButton = findViewById (R.id.hihat) as Button

        //drums.put("Snare", "0C:8C:DC:2C:4A:8B");
        //drums.put("Hi-Hat", "0C:8C:DC:2B:53:28");
        //drums.put("Bass", "0C:8C:DC:2C:4A:B7");
        //drums.put("Drum", "0C:8C:DC:2C:4A:B9")
        //drums.put("Temp", "0C:8C:DC:2C:4A:D5")



        snareButton.setOnClickListener {
            //ConnectActivity.connectToDrum("0C:8C:DC:2C4A:8B")
            val intent = Intent(this@MainActivity, ConnectActivity::class.java)
            intent.putExtra("macAddress", "0C:8C:DC:2C:4A:8B")
            startActivity(intent)
        }

        bassButton.setOnClickListener {
            val intent = Intent(this@MainActivity, ConnectActivity::class.java)
            intent.putExtra("macAddress", "0C:8C:DC:2C:4A:B7")
            startActivity(intent)
        }

        hihatButton.setOnClickListener {
            val intent = Intent(this@MainActivity, ConnectActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy () {
        super.onDestroy ()
        snare.release ()
        bass.release ()
        hihat.release ()
    }
}
