package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.GymEquipmentModel
import java.util.ArrayList

class GymEquipmentAdapter(
    var context: Context,
    var gymOfferModelList: ArrayList<GymEquipmentModel>,
    var check: String
) : RecyclerView.Adapter<GymEquipmentAdapter.GymEquipmentHolder>() {
    class GymEquipmentHolder (view: View):RecyclerView.ViewHolder(view){
        var tv=view.findViewById<TextView>(R.id.tv)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GymEquipmentHolder {
        var view=LayoutInflater.from(context).inflate(R.layout.equipment_layout, null)

        return GymEquipmentHolder(view)
    }

    override fun getItemCount(): Int {
        if (check.equals("limited")){
            if (gymOfferModelList.size>4){
                return 4
            }else{
                return gymOfferModelList.size
            }
        }else{
            return gymOfferModelList.size
        }

    }

    override fun onBindViewHolder(holder: GymEquipmentHolder, position: Int) {
        var gymEquipmentModel=gymOfferModelList.get(position)
        holder.tv.setText(gymEquipmentModel.name)
    }

}
