package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.CertificateModel
import com.bumptech.glide.Glide

class CertificateAdapter(
    var context: Context?,
    var certificateArrayList: ArrayList<CertificateModel>
) :
    RecyclerView.Adapter<CertificateAdapter.CertificateHolder>() {
    class CertificateHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivCertificate: ImageView = view.findViewById(R.id.ivCertificate)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CertificateHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.certificate_list, parent, false)
        return CertificateHolder(view)

    }

    override fun onBindViewHolder(holder: CertificateHolder, position: Int) {
        val certificateModel = certificateArrayList[position]

        context?.let {
            Glide.with(it).load(certificateModel.certificatePath).into(holder.ivCertificate)
        }

    }

    override fun getItemCount(): Int {
        return certificateArrayList.size
    }

}
