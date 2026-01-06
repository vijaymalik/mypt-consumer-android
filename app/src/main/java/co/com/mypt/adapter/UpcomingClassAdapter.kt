package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.text.TextPaint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.model.UpcomingClassModel
import co.com.mypt.R
import co.com.mypt.UpComingClasses.ClassDescriptionActivity
import com.bumptech.glide.Glide


class UpcomingClassAdapter(
    val context: Context?,
    val upcomingClassArraylist: ArrayList<UpcomingClassModel>,
    var latitude: Double,
    var longitude: Double
) : RecyclerView.Adapter<UpcomingClassAdapter.ViewHolder>() {
    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        val price : TextView = itemView.findViewById(R.id.price)
        val dateTime : TextView = itemView.findViewById(R.id.dateTime)
        val className : TextView = itemView.findViewById(R.id.className)
        val location : TextView = itemView.findViewById(R.id.location)

        val classImage : ImageView = itemView.findViewById(R.id.classImage)
        val relative : RelativeLayout = itemView.findViewById(R.id.relative)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.upcoming_class_adapter_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return upcomingClassArraylist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var upcomingModel=upcomingClassArraylist[position]
        var pricesplit=upcomingModel.price.split(".")
        val htmlString = "<big><b>${pricesplit[0]}</b></big><small> AED<br>Onwards</small>"
        val spanned = HtmlCompat.fromHtml(htmlString, HtmlCompat.FROM_HTML_MODE_COMPACT)
        holder.price.text = spanned
        val paint: TextPaint = holder.price.paint
        val width = paint.measureText(holder.price.text.toString())

        val textShader: Shader = LinearGradient(
            0f, 0f, width, holder.price.textSize, intArrayOf(
                Color.parseColor("#FAFAFA"),
                Color.parseColor("#9EBCFF"),
            ), null, Shader.TileMode.CLAMP
        )
        holder.relative.setTag(position)
        holder.price.paint.setShader(textShader)
        holder.className.setText(upcomingModel.cla_ss)
        holder.location.setText(upcomingModel.location)
        var datename=upcomingModel.time.split(",")
        holder.dateTime.setText(datename[0]+" - "+upcomingModel.start_end)
        Glide.with(context!!).load(upcomingModel.image).fitCenter().error(R.drawable.dummy_trainer).
        placeholder(R.drawable.dumbbell).into(holder.classImage)
        holder.relative.setOnClickListener{
            var j=it.tag
            var nearUpcomingCLassModel=upcomingClassArraylist[j as Int]
            var intent= Intent(context, ClassDescriptionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("schedule_id",nearUpcomingCLassModel.schedule_id)
            intent.putExtra("latitude",latitude)
            intent.putExtra("longitude",longitude)
            context?.startActivity(intent)
        }
    }

}
