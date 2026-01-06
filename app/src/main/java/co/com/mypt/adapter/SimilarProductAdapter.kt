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
import co.com.mypt.adapter.ChooseColorAdapter.ChooseColorHolder
import co.com.mypt.model.SimilarProductModel
import java.util.ArrayList

class SimilarProductAdapter(var context: Context?, var similarProductList: ArrayList<SimilarProductModel>) :
    RecyclerView.Adapter<SimilarProductAdapter.SimilarProductHolder>() {
    class SimilarProductHolder(view: View):RecyclerView.ViewHolder(view) {
        var tvprice=view.findViewById<TextView>(R.id.tvprice)
        var tvcurrency=view.findViewById<TextView>(R.id.tvcurrency)
        var tvRealPrice=view.findViewById<TextView>(R.id.tvRealPrice)
        var frame=view.findViewById<FrameLayout>(R.id.frame)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SimilarProductAdapter.SimilarProductHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feature_list, parent, false)
        return SimilarProductHolder(view)
    }

    override fun onBindViewHolder(
        holder: SimilarProductAdapter.SimilarProductHolder,
        position: Int
    ) {
       var similarProductModel=similarProductList[position]
        holder.tvprice.setText(similarProductModel.price)
        textShader(holder.tvprice)
        textShader(holder.tvcurrency)
        holder.tvRealPrice.paintFlags = holder.tvRealPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        holder.frame.setTag(position)
        holder.frame.setOnClickListener{
            var h=it.tag
            var trendingModel=similarProductList.get(h as Int)
            var intent= Intent(context, ProductDetailActivity::class.java)
            context!!.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
       return similarProductList.size
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
