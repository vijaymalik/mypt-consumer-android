package co.com.mypt.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.text.TextPaint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.CertificateModel

class CertificateAdapter(var context: Context?, var certificateArrayList: ArrayList<CertificateModel>) :
    RecyclerView.Adapter<CertificateAdapter.CertificateHolder>() {
    class CertificateHolder (view: View):RecyclerView.ViewHolder(view){
        val certificationTv1 : TextView = view.findViewById(R.id.certificationTv1)
        val tvname : TextView = view.findViewById(R.id.tvname)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CertificateHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.certificate_list, parent, false)
        return CertificateHolder(view)

    }

    override fun onBindViewHolder(holder: CertificateHolder, position: Int) {
        val certificateModel=certificateArrayList[position]
        holder.certificationTv1.text = certificateModel.level.replace("Level","Lvl.")
        holder.tvname.text = certificateModel.name
        textShader(holder.certificationTv1)
        textShader(holder.tvname)
    }

    override fun getItemCount(): Int {
        return certificateArrayList.size
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
