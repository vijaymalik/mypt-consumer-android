package co.com.mypt.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.RemindModel

class ReminderAdapter(
    var context: Context,
    var reminderModelList: ArrayList<RemindModel>,
    var remind_id: String
): RecyclerView.Adapter<ReminderAdapter.RemindHolder>() {
    private var selectedPosition = remind_id // Track selected position

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RemindHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.remin_me_layout, parent, false)
        return RemindHolder(view)
    }

    override fun onBindViewHolder(
        holder: RemindHolder,
        position: Int
    ) {
        var remindModel=reminderModelList[position]
        holder.checkname.setText(remindModel.remind_time)
        holder.checkname.isChecked = (remindModel.id == selectedPosition)
        holder.checkname.setTag(position)
        holder.checkname.setOnClickListener {
            var j=it.tag
            var remindModel=reminderModelList.get(j as Int)
            var intent=Intent("selectRemindId")
            if (selectedPosition == remindModel.id) {
                selectedPosition = ""
                intent.putExtra("remind_id", "")

            }
            else{
                // Select new checkbox
                selectedPosition = remindModel.id
                intent.putExtra("remind_id", remindModel.id)


            }
            notifyDataSetChanged()
            context.sendBroadcast(intent)

        }

    }

    override fun getItemCount(): Int {
        return reminderModelList.size
    }

    class RemindHolder(view: View): RecyclerView.ViewHolder(view) {
        var checkname=view.findViewById<CheckBox>(R.id.check5)

    }

}
