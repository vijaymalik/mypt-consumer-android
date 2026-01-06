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
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.FeatureAdapter.FeatureHolder
import co.com.mypt.model.CartModel
import java.util.ArrayList

class CartAdapter(var context: Context?, var cartList: ArrayList<CartModel>) :
    RecyclerView.Adapter<CartAdapter.CartHolder>() {
    class CartHolder(view: View):RecyclerView.ViewHolder(view) {
        var tvprice=view.findViewById<TextView>(R.id.tvprice)
        var tvcurrency=view.findViewById<TextView>(R.id.tvcurrency)
        var tvRealPrice=view.findViewById<TextView>(R.id.tvRealPrice)
        var im_minus=view.findViewById<ImageView>(R.id.im_minus)
        var im_add=view.findViewById<ImageView>(R.id.im_add)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartAdapter.CartHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_list, parent, false)
        return CartHolder(view)
    }

    override fun onBindViewHolder(holder: CartAdapter.CartHolder, position: Int) {
        var trendingModel=cartList[position]
        holder.tvprice.setText(trendingModel.price)
        textShader(holder.tvprice)
        textShader(holder.tvcurrency)
        holder.tvRealPrice.paintFlags = holder.tvRealPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

    }

    override fun getItemCount(): Int {
        return cartList.size
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
