package pl.wsei.pam.lab01.lab02

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.wsei.pam.lab01.R
import pl.wsei.pam.lab01.lab03.Lab03Activity

class Lab02Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab02)

        // Set click listener for all buttons
        findViewById<Button>(R.id.button1).setOnClickListener { handleButtonClick(it) }
        findViewById<Button>(R.id.button2).setOnClickListener { handleButtonClick(it) }
        findViewById<Button>(R.id.button3).setOnClickListener { handleButtonClick(it) }
        findViewById<Button>(R.id.button4).setOnClickListener { handleButtonClick(it) }
    }

    private fun handleButtonClick(v: View) {
        val tag: String? = v.tag as? String
        val tokens: List<String>? = tag?.split(" ")

        val rows = tokens?.getOrNull(0)?.toIntOrNull()
        val columns = tokens?.getOrNull(1)?.toIntOrNull()

        if (rows != null && columns != null) {
            // Tworzymy Intent i przekazujemy dane
            val intent = Intent(this, Lab03Activity::class.java)
            val size: IntArray = intArrayOf(rows, columns)
            intent.putExtra("size", size)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Invalid grid size", Toast.LENGTH_SHORT).show()
        }
    }
}
