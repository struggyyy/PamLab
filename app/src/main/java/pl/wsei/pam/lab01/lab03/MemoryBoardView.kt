import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.wsei.pam.lab01.R
import pl.wsei.pam.lab01.lab03.GameStates
import pl.wsei.pam.lab01.lab03.Lab03Activity
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

    // Sound effects - keeping the MediaPlayer instances for compatibility
    private val completionPlayer: MediaPlayer = MediaPlayer.create(activity, R.raw.completion)
    private val negativePlayer: MediaPlayer = MediaPlayer.create(activity, R.raw.negative_guitar)

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

    fun setOnGameChangeListener(listener: (MemoryGameEvent) -> Unit) {
        onGameChangeStateListener = listener
    }

    private fun addTile(button: ImageButton, resourceImage: Int) {
        button.setOnClickListener(::onClickTile)
        button.scaleType = ImageView.ScaleType.FIT_CENTER
        button.adjustViewBounds = true
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