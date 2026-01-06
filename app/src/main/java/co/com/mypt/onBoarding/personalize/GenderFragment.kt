package co.com.mypt.onBoarding.personalize

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.GenderAdapter
import co.com.mypt.onBoarding.PersonalizedActivity2
import co.com.mypt.utils.SharedGenderViewModel


class GenderFragment : Fragment() {
    var recycler: RecyclerView? = null
    private val viewModel: SharedGenderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gender, container, false)
        recycler = view.findViewById(R.id.recycler)

        val imageList = listOf(
            R.drawable.image_gender_male_unselected,
            R.drawable.image_gender_female_unselected,
            R.drawable.image_gender_other_unselected
        )
//        listOf(
//            R.drawable.male_,
//            R.drawable.female_,
//            R.drawable.genderother,
//        )
        val selectedImageList = listOf(
            R.drawable.image_gender_male_selected,
            R.drawable.image_gender_female_selected,
            R.drawable.image_gender_other_selected
        )
//        listOf(
//            R.drawable.selectedmale,
//            R.drawable.selectedfemale,
//            R.drawable.selectedgenderother,
//        )
        val iconList = listOf(
            R.drawable.im_male,
            R.drawable.im_female,
            R.drawable.im_group,

            )
        val textList =
            listOf(getString(R.string.male), getString(R.string.female), getString(R.string.others))
        val genderAdapter =
            GenderAdapter(activity, imageList, textList, selectedImageList, iconList)

        recycler?.adapter = genderAdapter

        viewModel.data.observe(context as PersonalizedActivity2) { data ->
            val genderAdapter =
                GenderAdapter(activity, imageList, textList, selectedImageList, iconList)
            recycler?.adapter = genderAdapter
        }
        return view
    }


}