package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.model.StreakModel
import java.util.ArrayList

class MyStreakAdapter(var context: Context?,var streakArrayList: ArrayList<StreakModel>) :
    RecyclerView.Adapter<MyStreakAdapter.MyStreakAdapterHolder>() {
    class MyStreakAdapterHolder (view: View):RecyclerView.ViewHolder(view){
        val card : CardView = view.findViewById(R.id.card)


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyStreakAdapter.MyStreakAdapterHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.streak_layout, parent, false)
        return MyStreakAdapterHolder(view)
    }

    override fun onBindViewHolder(holder: MyStreakAdapter.MyStreakAdapterHolder, position: Int) {

    }

    override fun getItemCount(): Int {
       return 30
    }

}
