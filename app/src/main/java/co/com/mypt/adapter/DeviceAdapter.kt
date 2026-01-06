package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.MealVerticalListAdapter.MealVerticalHolder
import co.com.mypt.model.DeviceModel
import java.util.ArrayList

class DeviceAdapter(var applicationContext: Context?, var deviceArrayList: ArrayList<DeviceModel>) :
    RecyclerView.Adapter<DeviceAdapter.DeviceHolder>() {
    class DeviceHolder(view: View):RecyclerView.ViewHolder(view) {
        var tvname=view.findViewById<TextView>(R.id.tvname)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceAdapter.DeviceHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.linked_device_list, parent, false)
        return DeviceHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceAdapter.DeviceHolder, position: Int) {
        var deviceModel=deviceArrayList[position]
        holder.tvname.setText(deviceModel.name)
    }

    override fun getItemCount(): Int {
       return deviceArrayList.size
    }

}
