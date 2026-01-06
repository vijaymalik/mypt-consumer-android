package co.com.mypt.adapter


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.model.JoinModel
import com.android.volley.VolleyError
import org.json.JSONObject

class JoinAdapter(
    var joinList: ArrayList<JoinModel>,
    var activity: Context
):RecyclerView.Adapter<JoinAdapter.JoinHolder>() {
    class JoinHolder(var view: View):RecyclerView.ViewHolder(view) {
        var tvname=view.findViewById<TextView>(R.id.tvname)
        var tvgender=view.findViewById<TextView>(R.id.tvgender)
        var tvage=view.findViewById<TextView>(R.id.tvage)
        var relative=view.findViewById<RelativeLayout>(R.id.relative)
        var imEdit=view.findViewById<ImageView>(R.id.imEdit)
        var imDelete=view.findViewById<ImageView>(R.id.imDelete)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JoinHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.joining_list, parent, false)
        return JoinHolder(view)

    }

    override fun getItemCount(): Int {
        return joinList.size
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: JoinHolder, position: Int) {
      var joinModel=joinList[position]
        holder.relative.tag = position
        holder.imEdit.tag = position
        holder.imDelete.tag = position

        holder.tvname.text = joinModel.name
        holder.tvage.text = joinModel.age
        if (joinModel.self.equals("true")){
            holder.imEdit.visibility=View.GONE
            holder.imDelete.visibility=View.GONE
        }else{
            holder.imEdit.visibility=View.VISIBLE
            holder.imDelete.visibility=View.VISIBLE
        }
        if (joinModel.gender.equals("null")){
            holder.tvgender.setText("")
            holder.tvgender.setBackgroundDrawable(null);
        }else{
            holder.tvgender.text = joinModel.gender
            holder.tvgender.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.joining_rectangle));
        }
        if (joinModel.age.equals("") || joinModel.age.equals("null")){
            holder.tvage.setText("")
            holder.tvage.setBackgroundDrawable(null);

        }else{
            holder.tvage.text = joinModel.age
            holder.tvage.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.joining_rectangle));
        }

        holder.imEdit.setOnClickListener{
            var j=it.tag as Int
            var joinModel=joinList[j]
            var intent= Intent("editMember")
            intent.putExtra("name",joinModel.name)
            intent.putExtra("age",joinModel.age)
            intent.putExtra("gender",joinModel.gender)
            intent.putExtra("id",joinModel.id)
            intent.putExtra("typeselect","edit")
            activity.sendBroadcast(intent)


        }
        holder.imDelete.setOnClickListener{
            var j=it.tag as Int
            var joinModel=joinList[j]
            deleteMember(joinModel.id)
        }
    }


    private fun deleteMember(id: String) {
        val progressDialog: Dialog = ProgressDialog.progressDialog(activity,"")
        progressDialog.show()
        Log.e("apiDeleteMember",ApiURL.deletemember+id)
        GetMethod(ApiURL.deletemember+id
            ,activity).startMethod(object :
            ResponseData {
            override fun response(data: kotlin.String?) {
                progressDialog.dismiss()

                Log.e("DeleteMemberResponse",data.toString())
                try {
                    val resp = JSONObject(data!!)
                    if(resp.optBoolean("status")){
                        var intent= Intent("deleteMember")
                        Toast.makeText(activity,resp.optString("msg"),Toast.LENGTH_LONG).show()
                        activity.sendBroadcast(intent)
                    }


                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun error(error: VolleyError?) {
                progressDialog.dismiss()
                error!!.printStackTrace()
            }

        })

    }


}
