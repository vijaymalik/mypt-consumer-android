package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.ActivityAdapter.Activity_Holder
import co.com.mypt.model.FullChatModel

class FullchatAdapter(var applicationContext: Context?, var fullchartArrayList: ArrayList<FullChatModel>) : RecyclerView.Adapter<FullchatAdapter.FullchatHolder>() {
    class FullchatHolder(view: View):RecyclerView.ViewHolder(view) {
        var tvname=view.findViewById<TextView>(R.id.tvname)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FullchatAdapter.FullchatHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_list, parent, false)
        return FullchatHolder(view)
    }

    override fun onBindViewHolder(holder: FullchatAdapter.FullchatHolder, position: Int) {
       var fullChatModel=fullchartArrayList[position]
        holder.tvname.setText(fullChatModel.name)
    }

    override fun getItemCount(): Int {
       return fullchartArrayList.size
    }

}
