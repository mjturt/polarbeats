package com.movesense.samples.sensorsample

import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.ImageView


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

        val snareButton = findViewById (R.id.snare) as ImageView
        val bassButton = findViewById (R.id.bass) as ImageView
        val hihatButton = findViewById (R.id.hihat) as ImageView

        snareButton.setOnClickListener {
            //ConnectActivity.connectToDrum("0C:8C:DC:2C4A:8B")
            val intent = Intent(this@MainActivity, ConnectActivity::class.java)
            startActivity(intent)
        }

        bassButton.setOnClickListener {
            val intent = Intent(this@MainActivity, ConnectActivity::class.java)
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
