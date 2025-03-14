package pl.wsei.pam.lab01.lab01

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import pl.wsei.pam.lab01.R

class Lab01Activity : AppCompatActivity() {
    lateinit var mLayout: LinearLayout
    lateinit var mTitle: TextView
    lateinit var mProgress: ProgressBar
    var mBoxes: MutableList<CheckBox> = mutableListOf()
    var mButtons: MutableList<Button> = mutableListOf()
    var progressValue = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab01)

        mLayout = findViewById(R.id.main)
        mTitle = TextView(this).also {
            it.text = "Laboratorium 1"
            it.textSize = 24f
            it.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(20, 20, 20, 20) }
            it.layoutParams = params
        }
        mLayout.addView(mTitle)

        // Progress bar setup
        mProgress = ProgressBar(
            this, null, androidx.appcompat.R.attr.progressBarStyle,
            androidx.appcompat.R.style.Widget_AppCompat_ProgressBar_Horizontal
        ).also {
            it.max = 100 // 100% scale
            it.progress = 0
        }
        mLayout.addView(mProgress)

        for (i in 1..6) {
            val row = LinearLayout(this).also {
                it.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                it.orientation = LinearLayout.HORIZONTAL
            }

            val checkBox = CheckBox(this).also {
                it.text = "Zadanie ${i}"
                it.isEnabled = false
            }
            mBoxes.add(checkBox)

            val button = Button(this).also {
                it.text = "Testuj"
                it.setOnClickListener { testTask(i - 1) }
            }
            mButtons.add(button)

            row.addView(checkBox)
            row.addView(button)
            mLayout.addView(row)
        }
    }

    private fun testTask(index: Int) {
        val passed = when (index) {
            0 -> task11(4, 6) in 0.666665..0.666667 && task11(7, -6) in -1.1666667..-1.1666665
            1 -> task12(7U, 6U) == "7 + 6 = 13" && task12(12U, 15U) == "12 + 15 = 27"
            2 -> task13(0.0, 5.4f) && !task13(7.0, 5.4f) && !task13(-6.0, -1.0f) && task13(6.0, 9.1f) && !task13(6.0, -1.0f) && task13(1.0, 1.1f)
            3 -> task14(-2, 5) == "-2 + 5 = 3" && task14(-2, -5) == "-2 - 5 = -7"
            4 -> task15("DOBRY") == 4 && task15("barDzo dobry") == 5 && task15("doStateczny") == 3 && task15("Dopuszczający") == 2 && task15("NIEDOSTATECZNY") == 1 && task15("XYZ") == -1
            5 -> task16(
                mapOf("A" to 2U, "B" to 4U, "C" to 3U),
                mapOf("A" to 1U, "B" to 2U)
            ) == 2U && task16(
                mapOf("A" to 2U, "B" to 4U, "C" to 3U),
                mapOf("F" to 1U, "G" to 2U)
            ) == 0U && task16(
                mapOf("A" to 23U, "B" to 47U, "C" to 30U),
                mapOf("A" to 1U, "B" to 2U, "C" to 4U)
            ) == 7U
            else -> false
        }

        if (passed) {
            if (!mBoxes[index].isChecked) {
                mBoxes[index].isChecked = true
                progressValue += (100f / 6).toInt() // Ensure progress reaches 100%
                mProgress.progress = progressValue
            }
        } else {
            showErrorPopup("Zadanie ${index + 1} jest błędne")
        }
    }

    private fun showErrorPopup(message: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Błąd")
        dialogBuilder.setMessage(message)
        dialogBuilder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        dialogBuilder.create().show()
    }
    // Wykonaj dzielenie niecałkowite parametru a przez b
    // Wynik zwróć po instrukcji return
    private fun task11(a: Int, b: Int): Double {
        return a.toDouble() / b
    }

    // Zdefiniuj funkcję, która zwraca łańcuch dla argumentów bez znaku (zawsze dodatnie) wg schematu
    private fun task12(a: UInt, b: UInt): String {
        return "$a + $b = ${a + b}"
    }

    // Zdefiniu funkcję, która zwraca wartość logiczną, jeśli parametr `a` jest nieujemny i mniejszy od `b`
    private fun task13(a: Double, b: Float): Boolean {
        return a >= 0 && a < b
    }

    // Zdefiniuj funkcję, która zwraca łańcuch dla argumentów całkowitych ze znakiem wg schematu
    private fun task14(a: Int, b: Int): String {
        return if (b < 0) "$a - ${Math.abs(b)} = ${a + b}" else "$a + $b = ${a + b}"
    }

    // Zdefiniuj funkcję zwracającą ocenę jako liczbę całkowitą na podstawie łańcucha z opisem słownym oceny.
    private fun task15(degree: String): Int {
        return when (degree.lowercase()) {
            "bardzo dobry" -> 5
            "dobry" -> 4
            "dostateczny" -> 3
            "dopuszczający" -> 2
            "niedostateczny" -> 1
            else -> -1
        }
    }

    // Zdefiniuj funkcję zwracającą liczbę możliwych do zbudowania egzemplarzy, które składają się z elementów umieszczonych w asset
    // Zmienna store jest magazynem wszystkich elementów
    // Przykład
    // store = mapOf("A" to 3, "B" to 4, "C" to 2)
    // asset = mapOf("A" to 1, "B" to 2)
    // var items = task16(store, asset)
    // println(items)	=> 2 ponieważ do zbudowania jednego egzemplarza potrzebne są 2 elementy "B" i jeden "A", a w magazynie mamy 2 "A" i 4 "B",
    // czyli do zbudowania trzeciego egzemplarza zabraknie elementów typu "B"
    fun task16(store: Map<String, UInt>, asset: Map<String, UInt>): UInt {
        return UInt.MAX_VALUE
    }

//    private fun task16(store: Map<String, UInt>, asset: Map<String, UInt>): UInt {
//        return asset.keys.map { key ->
//            store[key]?.div(asset[key] ?: 1U) ?: 0U
//        }.minOrNull() ?: 0U
//    }
}
