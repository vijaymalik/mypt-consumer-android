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
import co.com.mypt.adapter.ActivityAdapter.Activity_Holder
import co.com.mypt.model.FeatureBottomListModel
import java.util.ArrayList

class FeatureBottomListAdapter(
    var applicationContext: Context?,
    var bottomModelList: ArrayList<FeatureBottomListModel>
) : RecyclerView.Adapter<FeatureBottomListAdapter.FeatureBottomHolder>() {
    class FeatureBottomHolder (view: View):RecyclerView.ViewHolder(view){
        var tvprice=view.findViewById<TextView>(R.id.tvprice)
        var tvcurrency=view.findViewById<TextView>(R.id.tvcurrency)
        var tvRealPrice=view.findViewById<TextView>(R.id.tvRealPrice)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeatureBottomListAdapter.FeatureBottomHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feature_list, parent, false)
        return FeatureBottomHolder(view)
    }

    override fun onBindViewHolder(
        holder: FeatureBottomListAdapter.FeatureBottomHolder,
        position: Int
    ) {
        var trendingModel=bottomModelList[position]
        holder.tvprice.setText(trendingModel.price)
        textShader(holder.tvprice)
        textShader(holder.tvcurrency)
        holder.tvRealPrice.paintFlags = holder.tvRealPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    override fun getItemCount(): Int {
        return bottomModelList.size
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
