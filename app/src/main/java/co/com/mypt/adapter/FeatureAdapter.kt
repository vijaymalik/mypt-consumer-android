package co.com.mypt.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.text.TextPaint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.TrendingAdapter.TrendingHolder
import co.com.mypt.model.FeatureModel
import java.util.ArrayList

class FeatureAdapter(var applicationContext: Context?, var featureModelArrayList: ArrayList<FeatureModel>) :
    RecyclerView.Adapter<FeatureAdapter.FeatureHolder>() {
    class FeatureHolder (view: View):RecyclerView.ViewHolder(view){
        var tvprice=view.findViewById<TextView>(R.id.tvprice)
        var tvcurrency=view.findViewById<TextView>(R.id.tvcurrency)
        var tvRealPrice=view.findViewById<TextView>(R.id.tvRealPrice)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeatureAdapter.FeatureHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trending_list, parent, false)
        return FeatureHolder(view)
    }

    override fun onBindViewHolder(holder: FeatureAdapter.FeatureHolder, position: Int) {
        var trendingModel=featureModelArrayList[position]
        holder.tvprice.setText(trendingModel.price)
        textShader(holder.tvprice)
        textShader(holder.tvcurrency)
        holder.tvRealPrice.paintFlags = holder.tvRealPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    override fun getItemCount(): Int {
       return featureModelArrayList.size
    }
    private fun textShader(tv: TextView) {
        val paint: TextPaint = tv.paint
        val width = paint.measureText(tv.text.toString())

        val textShader: Shader = LinearGradient(
            0f, 0f, width, tv.textSize, intArrayOf(
                Color.parseColor("#FAFAFA"),
                Color.parseColor("#9EBCFF"),
            ), null, Shader.TileMode.CLAMP
        )
        tv.paint.setShader(textShader)
    }

}
