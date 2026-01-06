package co.com.mypt.fragments.CreatePackageScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.WorkingOutAdapter


class CreatePackageFragment : Fragment() {

    lateinit var recycler: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       var view=inflater.inflate(R.layout.fragment_create_package, container, false)
        recycler=view.findViewById(R.id.recycler)

        val imageList = listOf(
            R.drawable.oneday,
            R.drawable.with_buddy,
            R.drawable.with_group,
        )
        val selectedImageList = listOf(
            R.drawable.selectedone,
            R.drawable.withbuddy,
            R.drawable.selected_group,

            )
        val textList= listOf(getString(R.string.OneonOne), getString(R.string.WithBuddy), getString(R.string.WithGroup))
        val workingOutAdapter= WorkingOutAdapter(activity,imageList,textList,selectedImageList)

        recycler.adapter=workingOutAdapter

        return view
    }


}