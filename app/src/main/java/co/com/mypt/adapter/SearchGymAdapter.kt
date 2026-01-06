package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.GymModel
import java.util.Locale

class SearchGymAdapter(
    var applicationContext: Context?,
    var gymModelList: ArrayList<GymModel>,
    var gym: String,
    var latitude: Double,
    var longitude: Double
) : RecyclerView.Adapter<SearchGymAdapter.SearchHolder>() {
    var filterArrayList: ArrayList<GymModel> = ArrayList()
    class SearchHolder(view: View):RecyclerView.ViewHolder(view) {
        var tvname=view.findViewById<TextView>(R.id.tvname)
        var tvLocation=view.findViewById<TextView>(R.id.tvLocation)
        var tvkm=view.findViewById<TextView>(R.id.tvKm)
    }
    init {
        filterArrayList.addAll(gymModelList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
        val view= LayoutInflater.from(applicationContext).inflate(R.layout.nearbygym_list,null)
        return SearchHolder(view)
    }

    override fun getItemCount(): Int {
       return gymModelList.size
    }

    override fun onBindViewHolder(holder: SearchHolder, position: Int) {
        val searchModel= gymModelList[position]

        holder.tvname.text = searchModel.name
        holder.tvLocation.text = searchModel.location
        holder.tvkm.text = searchModel.distance+"away"
    }
    fun filter(charText: String) {
        gymModelList.clear() //Clear the main ArrayList
        if (charText.isNotEmpty()) {
            for (model in filterArrayList) {
                if (model.name.lowercase(Locale.getDefault()).startsWith(charText.lowercase(Locale.getDefault())) ||
                    model.name.lowercase(Locale.getDefault()).contains(charText.lowercase(Locale.getDefault())) ) {
                    gymModelList.add(model)
                }
            }
        }
        notifyDataSetChanged()
    }
}
