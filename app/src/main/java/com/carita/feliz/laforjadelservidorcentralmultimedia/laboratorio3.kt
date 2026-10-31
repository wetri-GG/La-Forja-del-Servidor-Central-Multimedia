package com.carita.feliz.laforjadelservidorcentralmultimedia

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import android.net.Uri
import android.view.View
import android.widget.AdapterView




class laboratorio3 : AppCompatActivity() {
    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private val canales = mapOf(
        "Minas Tirith" to "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        "Isengard" to "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
        "Rivendell" to "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_laboratorio3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        playerView = findViewById(R.id.Player)
        val spinnerCanales: Spinner = findViewById(R.id.Spinner)
        spinnerCanales.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val puestoSeleccionado = parent?.getItemAtPosition(position).toString()
                val url = canales[puestoSeleccionado]
                if (!url.isNullOrEmpty()) {
                    initializePlayer(url)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            canales.keys.toList()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCanales.adapter = adapter

    }
    private fun initializePlayer(url: String) {
        if (player == null) {
            player = ExoPlayer.Builder(this).build()
        }

        // Vincula siempre el PlayerView al reproductor
        playerView.player = player

        val mediaItem = MediaItem.fromUri(Uri.parse(url))
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true
    }
}