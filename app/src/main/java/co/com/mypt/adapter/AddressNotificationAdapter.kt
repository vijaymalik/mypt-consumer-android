package co.com.mypt.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Notification.AcceptBookingActivity
import co.com.mypt.R
import co.com.mypt.model.BreakFastListModel

class AddressNotificationAdapter(
    var activity: AcceptBookingActivity,
    var addressArrayList: ArrayList<BreakFastListModel>,
    var address_id: String
) : RecyclerView.Adapter<AddressNotificationAdapter.AddressHolder>(){
    private var selectedPosition = 0 // Track selected position

    class AddressHolder(view: View): RecyclerView.ViewHolder(view) {
        var tvHome=view.findViewById<TextView>(R.id.tvHome)
        var tvname=view.findViewById<TextView>(R.id.tvname)
        var tvnumber=view.findViewById<TextView>(R.id.tvnumber)
        var tvlocation=view.findViewById<TextView>(R.id.tvlocation)
        var checkname=view.findViewById<CheckBox>(R.id.checkname)


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddressHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notification_address_list, parent, false)
        return AddressHolder(view)
    }

    override fun onBindViewHolder(
        holder: AddressHolder,
        position: Int
    ) {
        var addressModel=addressArrayList.get(position)
        val landmark = if (!addressModel.landmark.isNullOrEmpty()) "${addressModel.landmark}," else ""

        holder.tvlocation.text = (addressModel.building_name+","+addressModel.street+","+landmark+addressModel.city_name+addressModel.country_name).replace("null,","")
        holder.tvHome.text = addressModel.type
        holder.tvname.text = addressModel.name
        holder.tvnumber.text = addressModel.mobile_no.replace("null","")

        holder.checkname.isChecked = addressModel.id == address_id

        holder.checkname.tag = position
        holder.checkname.setOnClickListener {
            var j=it.tag
            var addressModel=addressArrayList.get(j as Int)
            var intent=Intent("notificationselectAddress")
            if (addressModel.id == address_id) {
                selectedPosition = -1
                address_id = ""
                intent.putExtra("address_id", "")
                intent.putExtra("address_name", "")
                android.util.Log.e("address_id",""+"addrblaess_id")
                intent.putExtra("typeselect", "uncheck")
            }
            else{
                // Select new checkbox
                selectedPosition = j
                address_id = addressModel.id
                intent.putExtra("address_id", addressModel.id)
                intent.putExtra("typeselect", "check")
                intent.putExtra("address_name",holder.tvlocation.text.toString())

                android.util.Log.e("address_id",""+addressModel.id)

            }
            notifyDataSetChanged()

            activity.sendBroadcast(intent)

        }

    }

    override fun getItemCount(): Int {
        return addressArrayList.size
    }

}
