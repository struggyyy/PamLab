package pl.wsei.pam.lab01.lab03

import android.widget.ImageButton

data class Tile(val button: ImageButton, var tileResource: Int, val deckResource: Int) {
    init {
        button.setImageResource(deckResource)
    }

    private var _revealed: Boolean = false
    var revealed: Boolean
        get() = _revealed
        set(value) {
            _revealed = value
            updateView()
        }

    fun updateView() {
        button.setImageResource(if (_revealed) tileResource else deckResource)
    }

    fun removeFromBoard() {
        button.post {
            button.setImageDrawable(null)
            button.background = null  // Remove any background
            button.isEnabled = false
            button.alpha = 0f  // Make completely transparent
        }
    }

    fun removeOnClickListener() {
        button.setOnClickListener(null)
    }
}
