package co.com.mypt.activities

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import co.com.mypt.Api.Constants
import co.com.mypt.R
import co.com.mypt.model.GetStoriesList
import com.bumptech.glide.Glide
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StoryActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var player: ExoPlayer
    private lateinit var storyImageView: ImageView
    private lateinit var categoryIcon: ImageView
    private lateinit var categoryName: TextView
    private lateinit var progressContainer: LinearLayout

    private var stories: List<GetStoriesList.Data.StoryList.Story> = emptyList()
    private var currentIndex = 0
    private var progressJob: Job? = null
    private lateinit var progressBars: List<ProgressBar>

    // Single listener for all videos
    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> {
                    startProgress(player.duration) { nextStory() }
                }

                Player.STATE_ENDED -> nextStory()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)

        playerView = findViewById(R.id.playerView)
        storyImageView = findViewById(R.id.imageView)
        categoryIcon = findViewById(R.id.categoryIcon)
        categoryName = findViewById(R.id.categoryName)
        progressContainer = findViewById(R.id.progressContainer)

        playerView.useController = false // hide default controls


        val storyData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(
                Constants.KEY_STORIES_DATA,
                GetStoriesList.Data.StoryList::class.java
            )
        } else {
            intent.getParcelableExtra(Constants.KEY_STORIES_DATA)
        }
        storyData?.let {
            Glide.with(this).load(it.category_icon).fitCenter().into(categoryIcon)
            categoryName.text = it.category_name ?: ""
            stories = it.stories?.filterNotNull() ?: emptyList()
        }


        // Initialize ExoPlayer
        player = ExoPlayer.Builder(this).build()
        playerView.player = player
        player.addListener(playerListener)

        // Initialize progress bars
        progressBars = stories.map {
            ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
                max = 100
                progress = 0
                progressDrawable = ContextCompat.getDrawable(
                    this@StoryActivity,
                    R.drawable.story_progress_drawable
                )
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        6f,
                        resources.displayMetrics
                    ).toInt(),
                    1f
                ).apply {
                    marginEnd = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        8f,
                        resources.displayMetrics
                    ).toInt()
                }
            }.also { progressContainer.addView(it) }
        }

        if (stories.isNotEmpty()) {
            showStory(stories[currentIndex])
        }

        // Tap left/right for navigation
        findViewById<View>(R.id.tapLeft).setOnClickListener { previousStory() }
        findViewById<View>(R.id.tapRight).setOnClickListener { nextStory() }

        // Long press anywhere to pause/resume
        findViewById<View>(R.id.storyContainer).setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> pauseStory()
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> resumeStory()
            }
            true
        }
    }

    private fun showStory(story: GetStoriesList.Data.StoryList.Story) {
        progressJob?.cancel()
        resetProgressBars()

        when (story.type?.lowercase()) {
            IMAGE -> {
                story.media_path?.let {
                    storyImageView.visibility = View.VISIBLE
                    playerView.visibility = View.GONE
                    player.stop()
                    player.clearMediaItems()
                    Glide.with(this).load(it).into(storyImageView)
                    startProgress(5000) { nextStory() }
                }
            }

            VIDEO -> {
                story.media_path?.let { url ->
                    storyImageView.visibility = View.GONE
                    playerView.visibility = View.VISIBLE
                    player.stop()
                    player.clearMediaItems()
                    val mediaItem = MediaItem.fromUri(url.toUri())
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.play()
                }
            }
        }
    }

    private fun startProgress(duration: Long, onComplete: () -> Unit) {
        val bar = progressBars[currentIndex]
        bar.progress = 0
        progressJob?.cancel()
        progressJob = lifecycleScope.launch {
            val step = (duration / 100).coerceAtLeast(10)
            for (i in 1..100) {
                delay(step)
                bar.progress = i
            }
            onComplete()
        }
    }

    private fun nextStory() {
        if (currentIndex < stories.size - 1) {
            currentIndex++
            showStory(stories[currentIndex])
        } else finish()
    }

    private fun previousStory() {
        if (currentIndex > 0) {
            currentIndex--
            showStory(stories[currentIndex])
        }
    }

    private fun resetProgressBars() {
        progressBars.forEachIndexed { index, bar ->
            bar.progress = if (index < currentIndex) 100 else 0
        }
    }

    private fun pauseStory() {
        progressJob?.cancel()
        if (player.isPlaying) player.pause()
    }

    private fun resumeStory() {
        if (!player.isPlaying) player.play()
    }

    override fun onPause() {
        super.onPause()
        pauseStory()
    }

    override fun onResume() {
        super.onResume()
        if (playerView.isVisible) player.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    companion object {
        const val IMAGE = "image"
        const val VIDEO = "video"
    }
}