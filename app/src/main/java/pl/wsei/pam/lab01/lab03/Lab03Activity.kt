package pl.wsei.pam.lab01.lab03

import android.os.Bundle
import android.view.Gravity
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.gridlayout.widget.GridLayout
import pl.wsei.pam.lab01.R

class Lab03Activity : AppCompatActivity() {
    private lateinit var mBoard: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab03)

        // Odbieramy dane o rozmiarze planszy
        val size = intent.getIntArrayExtra("size") ?: intArrayOf(3, 3) // Domyślny rozmiar 3x3
        val rows = size[0]
        val columns = size[1]

        mBoard = findViewById(R.id.main)

        // Ustawiamy liczbę kolumn i wierszy w GridLayout
        mBoard.columnCount = columns
        mBoard.rowCount = rows

        // Generujemy przyciski
        for (row in 0 until rows) {
            for (col in 0 until columns) {
                val btn = ImageButton(this).also {
                    it.tag = "${row}x${col}"
                    val layoutParams = GridLayout.LayoutParams()
                    it.setImageResource(R.drawable.baseline_audiotrack_24) // Ikona przycisku
                    layoutParams.width = 0
                    layoutParams.height = 0
                    layoutParams.setGravity(Gravity.CENTER)
                    layoutParams.columnSpec = GridLayout.spec(col, 1, 1f)
                    layoutParams.rowSpec = GridLayout.spec(row, 1, 1f)
                    it.layoutParams = layoutParams
                    mBoard.addView(it)
                }
            }
        }
    }
}

