import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.wsei.pam.lab01.R
import pl.wsei.pam.lab01.lab03.GameStates
import pl.wsei.pam.lab01.lab03.MemoryGameEvent
import pl.wsei.pam.lab01.lab03.MemoryGameLogic
import pl.wsei.pam.lab01.lab03.Tile
import java.util.*

class MemoryBoardView(
    private val gridLayout: androidx.gridlayout.widget.GridLayout,
    private val cols: Int,
    private val rows: Int,
    private val activity: AppCompatActivity
) {
    private val tiles: MutableMap<String, Tile> = mutableMapOf()
    private val icons: List<Int> = listOf(
        R.drawable.baseline_60_24,
        R.drawable.baseline_ac_unit_24,
        R.drawable.baseline_accessibility_new_24,
        R.drawable.baseline_adb_24,
        R.drawable.baseline_add_location_24,
        R.drawable.baseline_air_24,
        R.drawable.baseline_airport_shuttle_24,
        R.drawable.baseline_alarm_24,
        R.drawable.baseline_alternate_email_24,
        R.drawable.baseline_audiotrack_24,
        R.drawable.baseline_back_hand_24,
        R.drawable.baseline_bakery_dining_24,
        R.drawable.baseline_battery_full_24,
        R.drawable.baseline_bolt_24,
        R.drawable.baseline_cake_24,
        R.drawable.baseline_camera_alt_24,
        R.drawable.baseline_castle_24,
        R.drawable.baseline_rocket_launch_24,
    )

    private val deckResource: Int = R.drawable.baseline_album_24
    private var onGameChangeStateListener: (MemoryGameEvent) -> Unit = {}
    private val matchedPair: Stack<Tile> = Stack()
    private val logic: MemoryGameLogic = MemoryGameLogic(cols * rows / 2)

    init {
        val shuffledIcons = (icons.take(cols * rows / 2) + icons.take(cols * rows / 2)).shuffled()
        var index = 0

        val display = gridLayout.context.resources.displayMetrics
        val tileWidth = display.widthPixels / cols
        val tileHeight = display.heightPixels / rows

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val button = ImageButton(gridLayout.context).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = tileWidth
                        height = tileHeight
                    }
                    tag = "$i-$j"
                }
                gridLayout.addView(button)
                addTile(button, shuffledIcons[index])
                index++
            }
        }
    }

    private var isChecking = false  // Prevents clicking during ongoing animations

    private fun onClickTile(v: View) {
        if (isChecking) return  // Prevent interaction during ongoing match check

        val tile = tiles[v.tag] ?: return
        if (tile.revealed) return  // Prevent clicking already revealed tiles

        tile.revealed = true  // Reveal tile
        matchedPair.push(tile)

        if (matchedPair.size == 2) {  // When two tiles are flipped
            isChecking = true  // Disable further clicks

            val firstTile = matchedPair[0]
            val secondTile = matchedPair[1]

            if (firstTile == secondTile) {  // Prevent clicking the same tile twice
                matchedPair.pop()  // Remove the second accidental entry
                isChecking = false
                return
            }

            if (firstTile.tileResource == secondTile.tileResource) {  // If they match
                onGameChangeStateListener(MemoryGameEvent(matchedPair.toList(), GameStates.Match))

                Timer().schedule(object : TimerTask() {  // Delay before removing tiles
                    override fun run() {
                        activity.runOnUiThread {
                            firstTile.removeFromBoard()
                            secondTile.removeFromBoard()
                            tiles.remove(firstTile.button.tag.toString())
                            tiles.remove(secondTile.button.tag.toString())
                            matchedPair.clear()
                            isChecking = false  // Re-enable clicking

                            // **Check if all tiles are removed**
                            if (tiles.isEmpty()) {
                                Toast.makeText(activity, "You Win!", Toast.LENGTH_LONG).show()
                                gridLayout.removeAllViews()  // Remove all tiles
                                gridLayout.setBackgroundColor(android.graphics.Color.WHITE)  // Make screen white
                            }
                        }
                    }
                }, 1000)  // Delay removal by 1 second
            } else {  // If no match
                onGameChangeStateListener(MemoryGameEvent(matchedPair.toList(), GameStates.NoMatch))

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        activity.runOnUiThread {
                            firstTile.revealed = false
                            secondTile.revealed = false
                            firstTile.updateView()
                            secondTile.updateView()
                            matchedPair.clear()
                            isChecking = false  // Re-enable clicking
                        }
                    }
                }, 1000)  // Delay flipping back non-matching tiles
            }
        }
    }

    fun setOnGameChangeListener(listener: (MemoryGameEvent) -> Unit) {
        onGameChangeStateListener = listener
    }

    private fun addTile(button: ImageButton, resourceImage: Int) {
        button.setOnClickListener(::onClickTile)
        button.scaleType = ImageView.ScaleType.FIT_CENTER  // Use ImageView.ScaleType instead // Ensures proper scaling
        button.adjustViewBounds = true  // Makes sure it maintains aspect ratio
        tiles[button.tag.toString()] = Tile(button, resourceImage, deckResource)
    }

    fun getState(): IntArray {
        return tiles.values.map { if (it.revealed) it.tileResource else -1 }.toIntArray()
    }

    fun setState(state: IntArray) {
        tiles.values.forEachIndexed { index, tile ->
            tile.revealed = state[index] != -1
            tile.updateView()
        }
    }
}
