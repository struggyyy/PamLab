package pl.wsei.pam.lab01.lab03

import MemoryBoardView
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.gridlayout.widget.GridLayout
import pl.wsei.pam.lab01.R
import java.util.*
import kotlin.concurrent.schedule

class Lab03Activity : AppCompatActivity() {
    private lateinit var mBoard: GridLayout
    private lateinit var mBoardModel: MemoryBoardView

    // 🔹 Sound effects
    lateinit var completionPlayer: MediaPlayer
    lateinit var negativePlayer: MediaPlayer

    // Sound toggle flag
    var isSound: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab03)

        // Set Toolbar as ActionBar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val size = intent.getIntArrayExtra("size") ?: intArrayOf(3, 3)
        val rows = size[0]
        val cols = size[1]

        mBoard = findViewById(R.id.main)
        mBoard.columnCount = cols
        mBoard.rowCount = rows

        // Pass Lab03Activity to MemoryBoardView
        mBoardModel = MemoryBoardView(mBoard, cols, rows, this)

        if (savedInstanceState != null) {
            val savedState = savedInstanceState.getIntArray("game_state")
            savedState?.let { mBoardModel.setState(it) }
            // Restore sound state if saved
            isSound = savedInstanceState.getBoolean("is_sound", true)
        }

        mBoardModel.setOnGameChangeListener { event ->
            runOnUiThread {
                when (event.state) {
                    GameStates.Matching, GameStates.Match -> {
                        event.tiles.forEach { it.revealed = true }
                    }
                    GameStates.NoMatch -> {
                        event.tiles.forEach { it.revealed = true }
                        Timer().schedule(2000) {
                            runOnUiThread {
                                event.tiles.forEach { it.revealed = false }
                            }
                        }
                    }
                    GameStates.Finished -> {
                        Toast.makeText(this, "Game finished!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.board_activity_menu, menu)
        val item = menu.findItem(R.id.board_activity_sound)
        item.setIcon(if (isSound) R.drawable.baseline_campaign_24 else R.drawable.baseline_cancel_24)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.board_activity_sound -> {
                isSound = !isSound
                item.setIcon(if (isSound) R.drawable.baseline_campaign_24 else R.drawable.baseline_cancel_24)
                Toast.makeText(this, if (isSound) "Sound On" else "Sound Off", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onResume() {
        super.onResume()
        // 🔹 Initialize sounds when the activity is active
        completionPlayer = MediaPlayer.create(applicationContext, R.raw.completion)
        negativePlayer = MediaPlayer.create(applicationContext, R.raw.negative_guitar)
    }

    override fun onPause() {
        super.onPause()
        // 🔹 Release media players to free up resources
        completionPlayer.release()
        negativePlayer.release()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntArray("game_state", mBoardModel.getState())
        // Save sound state
        outState.putBoolean("is_sound", isSound)
    }

    // Helper method that can be called from MemoryBoardView
    fun playCompletionSound() {
        if (isSound) {
            completionPlayer.start()
        }
    }

    // Helper method that can be called from MemoryBoardView
    fun playNegativeSound() {
        if (isSound) {
            negativePlayer.start()
        }
    }
}