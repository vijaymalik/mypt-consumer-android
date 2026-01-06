package co.com.mypt.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.ActivityAdapter1.Activity_Holder
import co.com.mypt.model.AddExcerciseModel
import java.util.ArrayList

class AddExcerciseAdapter(var addexcerxiseModelList: ArrayList<AddExcerciseModel>,var  activity: Activity) :
    RecyclerView.Adapter<AddExcerciseAdapter.AddExcerciseHolder>() {
    class AddExcerciseHolder(view: View):RecyclerView.ViewHolder(view) {
        var tv=view.findViewById<TextView>(R.id.tv)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddExcerciseAdapter.AddExcerciseHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_excercise_list, parent, false)
        return AddExcerciseHolder(view)
    }

    override fun onBindViewHolder(holder: AddExcerciseAdapter.AddExcerciseHolder, position: Int) {
        var addExcerciseModel=addexcerxiseModelList[position]
       holder.tv.setText(addExcerciseModel.name)
    }

    override fun getItemCount(): Int {
        return addexcerxiseModelList.size
    }

}
