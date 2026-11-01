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
// --- Añadidos para los pasos 3 y 4 ---
import android.media.MediaPlayer
import android.widget.TextView
import androidx.media3.common.Player

class laboratorio3 : AppCompatActivity() {
    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var estado: TextView          // paso 3
    private var alarma: MediaPlayer? = null        // paso 4
    private val canales = mapOf(
        "Minas Tirith" to R.raw.minas_tirith,
        "Isengard" to R.raw.isengard,
        "Rivendell" to R.raw.rivendell
    )

    // Paso 3: listener de los cambios de estado del buffer
    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING -> estado.text = "Cargando Búfer..."
                Player.STATE_READY -> estado.text = "Transmitiendo en vivo"
                Player.STATE_ENDED -> {
                    estado.text = "Conexión terminada"
                    sonarAlarma() // paso 4
                }
            }
        }
    }

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
        estado = findViewById(R.id.Estado)         // paso 3
        val spinnerCanales: Spinner = findViewById(R.id.Spinner)
        spinnerCanales.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val puestoSeleccionado = parent?.getItemAtPosition(position).toString()
                val rawResId = canales[puestoSeleccionado]
                if (rawResId != null) {
                    initializePlayer(rawResId)
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
    private fun initializePlayer(rawResId: Int) {
        if (player == null) {
            player = ExoPlayer.Builder(this).build()
            player?.addListener(playerListener)    // paso 3
        }

        // Vincula siempre el PlayerView al reproductor
        playerView.player = player


        val uri = Uri.parse("android.resource://$packageName/$rawResId")
        val mediaItem = MediaItem.fromUri(uri)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true
    }

    // Paso 4: alarma con MediaPlayer en paralelo
    private fun sonarAlarma() {
        liberarAlarma()
        alarma = MediaPlayer.create(this, R.raw.campanas_alarma)?.apply {
            setOnCompletionListener { liberarAlarma() }
            start()
        }
    }

    private fun liberarAlarma() {
        alarma?.release()
        alarma = null
    }

    // Paso 4: liberar la memoria de ambos motores
    override fun onDestroy() {
        super.onDestroy()
        player?.removeListener(playerListener)
        player?.release()
        player = null
        liberarAlarma()
    }
}
