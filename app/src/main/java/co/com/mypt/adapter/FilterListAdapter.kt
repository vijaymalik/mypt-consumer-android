package co.com.mypt.adapter

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.activities.TrainersListActivity
import co.com.mypt.adapter.EditSetsAdapter.EditSetholder

class FilterListAdapter(var activity: TrainersListActivity, var filternames: ArrayList<String>): RecyclerView.Adapter<FilterListAdapter.Filter_Holder>(){
    var selectedType=""
    var lastClickTime = 0L
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Filter_Holder {
        var layoutInflater=LayoutInflater.from(activity).inflate(R.layout.filter_list_layout,null)
        return Filter_Holder(layoutInflater)
    }

    override fun onBindViewHolder(holder: Filter_Holder, position: Int) {
      holder.tvFiltername.setText(filternames[position])
        holder.relative.setTag(position)
        holder.relative.setOnClickListener {
            var j=it.tag
            val now = System.currentTimeMillis()
            if (now - lastClickTime < 300) return@setOnClickListener
            lastClickTime = now
            if (holder.tvFiltername.getText().toString().equals("Time Slot")){
                selectedType="timeslot"
            }
            else if (holder.tvFiltername.getText().toString().equals("Gender")){
                selectedType="Gender"

            }
            else if (holder.tvFiltername.getText().toString().equals("Language")){
                selectedType="Language"

            }else if (holder.tvFiltername.getText().toString().equals("Nationality")){
                selectedType="Nationality"

            }
            activity.filterBottomSHeet(selectedType)
            //activity.filterBottomSheetDialog!!.show()


        }
    }

    override fun getItemCount(): Int {
        return filternames.size
    }

    class Filter_Holder(view: View): RecyclerView.ViewHolder(view) {
        var tvFiltername=view.findViewById<TextView>(R.id.tvFiltername)
        var relative=view.findViewById<RelativeLayout>(R.id.relative)
    }

}
