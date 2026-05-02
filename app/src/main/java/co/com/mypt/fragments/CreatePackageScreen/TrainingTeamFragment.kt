package co.com.mypt.fragments.CreatePackageScreen

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.addCallback
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import co.com.mypt.R
import co.com.mypt.activities.BestPlanTotalSessionWrapperActivity
import co.com.mypt.activities.TrainersListActivity
import co.com.mypt.utils.FullScreenVideoView


class TrainingTeamFragment : Fragment(R.layout.fragment_training_team) {

    private lateinit var videoView: FullScreenVideoView
    private lateinit var progressBar: ProgressBar
    private lateinit var back: ImageView
    private var videoUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videoView = view.findViewById(R.id.videoView)
        progressBar = view.findViewById(R.id.p_Bar)
        back = view.findViewById(R.id.back)
        val llBtnSelectTrainerView: LinearLayout = view.findViewById(R.id.llBtnSelectTrainerView)

        videoUri =
            "android.resource://${requireContext().packageName}/${R.raw.training_team}".toUri()

        videoView.setVideoURI(videoUri)

//        videoView.setOnPreparedListener { mediaPlayer ->
//            mediaPlayer.isLooping = true
//
//        }
        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
            mp.setVolume(0f, 0f)
            mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
            val videoRatio = mp.videoWidth / mp.videoHeight.toFloat()
            val screenRatio = videoView.width / videoView.height.toFloat()
            val scaleX = videoRatio / screenRatio
            if (scaleX >= 1f) {
                videoView.scaleX = scaleX
            } else {
                videoView.scaleY = 1f / scaleX
            }
        }

        progressBar.setProgress(50, true)

        llBtnSelectTrainerView.setOnClickListener {
            arguments?.let { args ->
                if(args.getBoolean("isGuestHome",false)){
                    val intent = Intent(requireContext(), BestPlanTotalSessionWrapperActivity::class.java)
                    intent.putExtra("trainer_id", args.getString("trainer_id"))
                    intent.putExtra("address_id", args.getString("address_id"))
                    if (args.getString("studio_id").isNullOrEmpty().not()) {
                        intent.putExtra("studio_id", args.getString("studio_id"))
                    }
                    intent.putExtra("type", "")
                    intent.putExtra("long", args.getDouble("longitude"))
                    intent.putExtra("lat", args.getDouble("latitude"))
                    requireContext().startActivity(intent)
                }else {
                val intent = Intent(requireContext(), TrainersListActivity::class.java)
                intent.putExtra("address_id", args.getString("address_id"))
                intent.putExtra("longitude", args.getDouble("longitude"))
                intent.putExtra("latitude", args.getDouble("latitude"))
                if (args.getString("studio_id").isNullOrEmpty().not()) {
                    intent.putExtra("studio_id", args.getString("studio_id"))
                }
                requireContext().startActivity(intent)
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner
        ) {
            requireActivity().finish()
        }

        back.setOnClickListener {
            requireActivity().finish()
        }
    }

    override fun onStart() {
        super.onStart()
        videoView.start()
    }

    override fun onStop() {
        super.onStop()
        videoView.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // release resources
        videoView.stopPlayback()
    }
}