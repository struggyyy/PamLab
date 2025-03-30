import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.gridlayout.widget.GridLayout.LayoutParams
import pl.wsei.pam.lab01.R
import pl.wsei.pam.lab01.lab03.GameStates
import pl.wsei.pam.lab01.lab03.Lab03Activity
import pl.wsei.pam.lab01.lab03.MemoryGameEvent
import pl.wsei.pam.lab01.lab03.MemoryGameLogic
import pl.wsei.pam.lab01.lab03.Tile
import java.util.*
import androidx.gridlayout.widget.GridLayout


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
        R.drawable.baseline_rocket_launch_24
    )

    private val deckResource: Int = R.drawable.baseline_album_24
    private var onGameChangeStateListener: (MemoryGameEvent) -> Unit = {}
    private val matchedPair: Stack<Tile> = Stack()
    private val logic: MemoryGameLogic = MemoryGameLogic(cols * rows / 2)

    // Store the original icon arrangement for state restoration
    private val boardIcons: List<Int>

    init {
        // Create a deterministic arrangement of icons based on a consistent seed
        // This ensures the icons stay in the same positions when recreating the board
        val random = Random(1234) // Use a fixed seed for consistent shuffling
        boardIcons = (icons.take(cols * rows / 2) + icons.take(cols * rows / 2)).shuffled(random)

        createBoard()
    }

    private fun createBoard() {
        var index = 0

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val button = ImageButton(gridLayout.context).apply {
                    layoutParams = LayoutParams().apply {
                        width = 0
                        height = 0
                        columnSpec = GridLayout.spec(j, 1f)
                        rowSpec = GridLayout.spec(i, 1f)
                    }
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    adjustViewBounds = true
                    tag = "$i-$j"
                }
                gridLayout.addView(button)
                addTile(button, boardIcons[index])
                index++
            }
        }
    }

    // Rest of your code remains the same


    private var isChecking = false

    private fun onClickTile(v: View) {
        if (isChecking) return

        val tile = tiles[v.tag] ?: return
        if (tile.revealed) return

        tile.revealed = true
        matchedPair.push(tile)

        if (matchedPair.size == 2) {
            isChecking = true

            val firstTile = matchedPair[0]
            val secondTile = matchedPair[1]

            if (firstTile == secondTile) {
                matchedPair.pop()
                isChecking = false
                return
            }

            if (firstTile.tileResource == secondTile.tileResource) {
                onGameChangeStateListener(MemoryGameEvent(matchedPair.toList(), GameStates.Match))

                // Play sound if enabled using the activity method
                (activity as? Lab03Activity)?.playCompletionSound()

                animatePairedButton(firstTile.button) {
                    animatePairedButton(secondTile.button) {
                        tiles.remove(firstTile.button.tag.toString())
                        tiles.remove(secondTile.button.tag.toString())
                        matchedPair.clear()
                        isChecking = false

                        if (tiles.isEmpty()) {
                            Toast.makeText(activity, "You Win!", Toast.LENGTH_LONG).show()
                            gridLayout.removeAllViews()
                            gridLayout.setBackgroundColor(android.graphics.Color.WHITE)
                        }
                    }
                }
            } else {
                onGameChangeStateListener(MemoryGameEvent(matchedPair.toList(), GameStates.NoMatch))

                // Play sound if enabled using the activity method
                (activity as? Lab03Activity)?.playNegativeSound()

                animateMismatchedButtons(firstTile.button, secondTile.button) {
                    firstTile.revealed = false
                    secondTile.revealed = false
                    firstTile.updateView()
                    secondTile.updateView()
                    matchedPair.clear()
                    isChecking = false
                }
            }
        }
    }

    fun isGameFinished(): Boolean {
        return tiles.isEmpty()
    }

    fun setOnGameChangeListener(listener: (MemoryGameEvent) -> Unit) {
        onGameChangeStateListener = listener
    }

    private fun addTile(button: ImageButton, resourceImage: Int) {
        button.setOnClickListener(::onClickTile)
        tiles[button.tag.toString()] = Tile(button, resourceImage, deckResource)
    }

    fun getState(): IntArray {
        // Create a state array with one position for each possible tile
        val state = IntArray(rows * cols) { -1 }

        // Fill in values for existing tiles
        tiles.values.forEachIndexed { index, tile ->
            // Store the resource ID for revealed tiles, -1 for hidden tiles
            state[index] = if (tile.revealed) tile.tileResource else -1
        }

        // For removed tiles (matched pairs), we need to mark them differently
        // Calculate which indices are missing from the tiles map
        val allPositions = (0 until rows * cols).toSet()
        val existingPositions = tiles.keys.map { key ->
            val (row, col) = key.split("-").map { it.toInt() }
            row * cols + col
        }.toSet()

        val removedPositions = allPositions - existingPositions

        // Mark removed tiles with a special value (-2)
        removedPositions.forEach { pos ->
            state[pos] = -2
        }

        return state
    }

    fun setState(state: IntArray) {
        var index = 0

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val position = "$i-$j"
                val value = state[index++]

                when (value) {
                    -1 -> {
                        // Hidden tile
                        tiles[position]?.let { tile ->
                            tile.revealed = false
                            tile.updateView()
                        }
                    }
                    -2 -> {
                        // Removed tile (matched pair)
                        // Find the button for this position and make it invisible
                        val tag = "$i-$j"
                        gridLayout.findViewWithTag<ImageButton>(tag)?.let { button ->
                            // Completely remove the visual presence of the button
                            button.setImageDrawable(null)
                            button.background = null
                            button.isEnabled = false
                            button.alpha = 0f
                        }

                        // Also remove it from tiles map if it exists
                        tiles.remove(position)
                    }
                    else -> {
                        // Revealed tile
                        tiles[position]?.let { tile ->
                            tile.revealed = true
                            tile.updateView()
                        }
                    }
                }
            }
        }
    }

    private fun animatePairedButton(button: ImageButton, action: Runnable) {
        val set = AnimatorSet()
        button.pivotX = button.width / 2f
        button.pivotY = button.height / 2f

        val rotation = ObjectAnimator.ofFloat(button, "rotation", 1080f)
        val scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 4f)
        val scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 4f)
        val fade = ObjectAnimator.ofFloat(button, "alpha", 1f, 0f)

        set.duration = 1000
        set.interpolator = DecelerateInterpolator()
        set.playTogether(rotation, scaleX, scaleY, fade)
        set.play(fade)
        set.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator) {
                button.scaleX = 1f
                button.scaleY = 1f
                button.alpha = 0.0f
                action.run()
            }
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        set.start()
    }

    private fun animateMismatchedButtons(first: ImageButton, second: ImageButton, action: Runnable) {
        val shakeAngle = 10f
        val rotate1 = ObjectAnimator.ofFloat(first, "rotation", -shakeAngle, shakeAngle)
        val rotate2 = ObjectAnimator.ofFloat(second, "rotation", -shakeAngle, shakeAngle)
        rotate1.repeatCount = 3
        rotate2.repeatCount = 3
        rotate1.duration = 200
        rotate2.duration = 200
        val set = AnimatorSet()
        set.playTogether(rotate1, rotate2)
        set.interpolator = DecelerateInterpolator()
        set.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator) {
                first.rotation = 0f
                second.rotation = 0f
                action.run()
            }
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        set.start()
    }
}