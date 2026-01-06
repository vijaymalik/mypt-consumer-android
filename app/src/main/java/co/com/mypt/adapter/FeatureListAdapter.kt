package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.text.TextPaint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.Shop.ProductDetailActivity
import co.com.mypt.adapter.GlimpseofOurCLassesAdapter.GlimpseHolder
import co.com.mypt.model.FeatureListModel
import java.util.ArrayList

class FeatureListAdapter(
    var applicationContext: Context?,
    var featureModelList: ArrayList<FeatureListModel>
) : RecyclerView.Adapter<FeatureListAdapter.FeatureListHolder>() {
    class FeatureListHolder (view: View):RecyclerView.ViewHolder(view){
        var tvprice=view.findViewById<TextView>(R.id.tvprice)
        var tvcurrency=view.findViewById<TextView>(R.id.tvcurrency)
        var tvRealPrice=view.findViewById<TextView>(R.id.tvRealPrice)
        var frame=view.findViewById<FrameLayout>(R.id.frame)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeatureListAdapter.FeatureListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feature_list, parent, false)
        return FeatureListHolder(view)
    }

    override fun onBindViewHolder(holder: FeatureListAdapter.FeatureListHolder, position: Int) {
        var trendingModel=featureModelList[position]
        holder.tvprice.setText(trendingModel.price)
        textShader(holder.tvprice)
        textShader(holder.tvcurrency)
        holder.tvRealPrice.paintFlags = holder.tvRealPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        holder.frame.setTag(position)
        holder.frame.setOnClickListener{
            var h=it.tag
            var trendingModel=featureModelList.get(h as Int)
            var intent= Intent(applicationContext,ProductDetailActivity::class.java)
            applicationContext!!.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
      return featureModelList.size
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
