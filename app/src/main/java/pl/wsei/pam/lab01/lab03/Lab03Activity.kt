package pl.wsei.pam.lab01.lab03

import MemoryBoardView
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.gridlayout.widget.GridLayout
import pl.wsei.pam.lab01.R
import java.util.*
import kotlin.concurrent.schedule

class Lab03Activity : AppCompatActivity() {
    private lateinit var mBoard: GridLayout
    private lateinit var mBoardModel: MemoryBoardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab03)

        // Odbieramy dane o rozmiarze planszy
        val size = intent.getIntArrayExtra("size") ?: intArrayOf(3, 3) // Domyślny rozmiar 3x3
        val rows = size[0]
        val cols = size[1]

        mBoard = findViewById(R.id.main)
        mBoard.columnCount = cols
        mBoard.rowCount = rows

        // Tworzymy model planszy, pass the current activity (this)
        mBoardModel = MemoryBoardView(mBoard, cols, rows, this) // Pass `this` as activity

        // Przywracamy stan gry po obrocie ekranu
        if (savedInstanceState != null) {
            val savedState = savedInstanceState.getIntArray("game_state")
            savedState?.let { mBoardModel.setState(it) }
        }

        // Obsługa zdarzeń gry
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntArray("game_state", mBoardModel.getState())
    }
}
