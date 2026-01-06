package co.com.mypt.onBoarding.personalize

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.PreferenceAdapter
import co.com.mypt.onBoarding.PersonalizedActivity2


class PreferenceFragment : Fragment() {
    var recycler: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view=inflater.inflate(R.layout.fragment_preference, container, false)
        recycler=view.findViewById(R.id.recycler)

        val imageList = listOf(
            R.drawable.bymyself,
            R.drawable.personal,
            R.drawable.group,
            R.drawable.dontknow,
        )
        val selectedImageList = listOf(
            R.drawable.selectedbymyself,
            R.drawable.selectedpersonal,
            R.drawable.selectedgroup,
            R.drawable.selecteddontknow,

        )
        var textList= listOf("By Myself","Personal Trainer","Group Session","Dont Know Yet")
        var preferenceAdapter= PreferenceAdapter(activity,imageList,textList,selectedImageList)

        recycler?.adapter=preferenceAdapter
        return view
    }

    override fun onResume() {
        super.onResume()
        if(isVisible){
            if((context as PersonalizedActivity2).selectedCount == 0){
                val imageList = listOf(
                    R.drawable.bymyself,
                    R.drawable.personal,
                    R.drawable.group,
                    R.drawable.dontknow,
                )
                val selectedImageList = listOf(
                    R.drawable.selectedbymyself,
                    R.drawable.selectedpersonal,
                    R.drawable.selectedgroup,
                    R.drawable.selecteddontknow,

                    )
                var textList= listOf("By Myself","Personal Trainer","Group Session","Dont Know Yet")
                var preferenceAdapter= PreferenceAdapter(activity,imageList,textList,selectedImageList)

                recycler?.adapter=preferenceAdapter
            }
        }
    }
}