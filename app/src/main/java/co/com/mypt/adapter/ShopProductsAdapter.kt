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
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.model.ShopProductsModel
import co.com.mypt.R


class ShopProductsAdapter(val context: Context?, val shopProductsArraylist: ArrayList<ShopProductsModel>) :
    RecyclerView.Adapter<ShopProductsAdapter.ViewHolder>() {
    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {

        val actualPrice: TextView = itemView.findViewById(R.id.actualPrice)
        val oldPrice: TextView = itemView.findViewById(R.id.oldPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shop_products_adapter_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val htmlString = "<big><b>200</b></big><small> AED</small>"
        val spanned = HtmlCompat.fromHtml(htmlString, HtmlCompat.FROM_HTML_MODE_COMPACT)
        holder.actualPrice.text = spanned

        val paint: TextPaint = holder.actualPrice.paint
        val width = paint.measureText(holder.actualPrice.text.toString())

        val textShader: Shader = LinearGradient(
            0f, 0f, width, holder.actualPrice.textSize, intArrayOf(
                Color.parseColor("#FAFAFA"),
                Color.parseColor("#9EBCFF"),
            ), null, Shader.TileMode.CLAMP
        )
        holder.actualPrice.paint.setShader(textShader)

        holder.oldPrice.paintFlags = holder.oldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

}
