package co.com.mypt.UpComingClasses

import android.os.Bundle
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import co.com.mypt.R
import com.bumptech.glide.Glide

class ResourcesDetailActivity : AppCompatActivity() {
    lateinit var tvTitlle:TextView
    lateinit var tvDate:TextView
    lateinit var tvRating:TextView
    lateinit var tvIntro:TextView
    lateinit var im:ImageView
    lateinit var imback:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_resources_detail)
        tvTitlle=findViewById(R.id.tvTitlle)
        tvDate=findViewById(R.id.tvDate)
        tvRating=findViewById(R.id.tvRating)
        im=findViewById(R.id.im)
        tvIntro=findViewById(R.id.tvIntro)
        imback=findViewById(R.id.imback)
        tvTitlle.setText(intent.getStringExtra("tittle"))
        tvDate.setText(intent.getStringExtra("date"))
        tvRating.setText(intent.getStringExtra("reading_time"))
        tvIntro.setText(Html.fromHtml(intent.getStringExtra("description")))
        Glide.with(this).load(intent.getStringExtra("image")).fitCenter().into(im)
        imback.setOnClickListener{
            finish()
        }
    }
}