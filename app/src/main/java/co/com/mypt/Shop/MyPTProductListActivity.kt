package co.com.mypt.Shop

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import co.com.mypt.R
import co.com.mypt.adapter.CategoryFilterAdapter
import co.com.mypt.adapter.FeatureAdapter
import co.com.mypt.adapter.FeatureBottomListAdapter
import co.com.mypt.adapter.FeatureListAdapter
import co.com.mypt.adapter.ImageSliderAdapter
import co.com.mypt.adapter.NearUpcomingClassAdapter
import co.com.mypt.adapter.ProductimageSliderAdapter
import co.com.mypt.model.CategoryModel
import co.com.mypt.model.FeatureBottomListModel
import co.com.mypt.model.FeatureListModel
import co.com.mypt.model.FeatureModel
import co.com.mypt.model.ImageModel
import co.com.mypt.model.LookbyCategoryModel
import co.com.mypt.model.NearUpcomingCLassModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class MyPTProductListActivity : AppCompatActivity() {
    lateinit var FeatureRecycler:RecyclerView
    lateinit var bottomRecycler:RecyclerView
    lateinit var CategoryRecycle:RecyclerView
    var featureModelList :ArrayList<FeatureListModel> = ArrayList()
    var categoryModelList :ArrayList<CategoryModel> = ArrayList()
    var bottomModelList :ArrayList<FeatureBottomListModel> = ArrayList()
    lateinit var standard_bottom_sheet: LinearLayout
    lateinit var linearSort: LinearLayout
    lateinit var linearFilter: LinearLayout
    lateinit var relativeProductList: RelativeLayout
    lateinit var relative: RelativeLayout
    lateinit var tvPrice: TextView
    lateinit var tvcategory: TextView
    lateinit var tvrating: TextView
    lateinit var tvBrand: TextView
    lateinit var sortbyBottomSheetDialog:BottomSheetDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_ptproduct_list)
        FeatureRecycler=findViewById(R.id.FeatureRecycler)
        bottomRecycler=findViewById(R.id.bottomRecycler)
        linearSort=findViewById(R.id.linearSort)
        linearFilter=findViewById(R.id.linearFilter)
        CategoryRecycle=findViewById(R.id.CategoryRecycle)
        tvPrice=findViewById(R.id.tvPrice)
        tvcategory=findViewById(R.id.tvcategory)
        relativeProductList=findViewById(R.id.relativeProductList)
        relative=findViewById(R.id.relative)
        tvrating=findViewById(R.id.tvrating)
        tvBrand=findViewById(R.id.tvBrand)

        sortbyBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)

        for (i in 0..6) {
            var featureModel= FeatureListModel()
            featureModel.price="299"
            featureModelList.add(featureModel)
        }
        var featureAdapter = FeatureListAdapter(applicationContext, featureModelList)
        FeatureRecycler.adapter = featureAdapter

        for (i in 0..6) {
            var featureBottomListModel= FeatureBottomListModel()
            featureBottomListModel.price="299"
            bottomModelList.add(featureBottomListModel)
        }
        var featureBottomListAdapter = FeatureBottomListAdapter(applicationContext, bottomModelList)
        bottomRecycler.adapter = featureBottomListAdapter

        for (i in 0..6) {
            var categoryModel= CategoryModel()
            categoryModel.name="Fitness Equipment"
            categoryModelList.add(categoryModel)
        }
        var categoryFilterAdapter = CategoryFilterAdapter(applicationContext, categoryModelList)
        CategoryRecycle.adapter = categoryFilterAdapter
        createsortAlert()
        linearSort.setOnClickListener{
            sortbyBottomSheetDialog.show()
        }
        linearFilter.setOnClickListener{
            relativeProductList.visibility= View.GONE
            relative.visibility=View.VISIBLE
        }
        tvcategory.setOnClickListener{
            tvcategory.background = resources.getDrawable(R.drawable.category_filer_drawable)
            tvPrice.background = null
            tvBrand.background = null
            tvrating.background = null
        }
        tvPrice.setOnClickListener{
            tvPrice.background = resources.getDrawable(R.drawable.category_filer_drawable)
            tvcategory.background = null
            tvBrand.background = null
            tvrating.background = null
        }
         tvBrand.setOnClickListener{
            tvBrand.background = resources.getDrawable(R.drawable.category_filer_drawable)
            tvcategory.background = null
            tvrating.background = null
            tvPrice.background = null
        }
        tvrating.setOnClickListener{
            tvrating.background = resources.getDrawable(R.drawable.category_filer_drawable)
            tvcategory.background = null
            tvBrand.background = null
            tvPrice.background = null
        }


    }
    private fun createsortAlert() {
        val bottomSheet = layoutInflater.inflate(R.layout.sortby_bottomsheet, null)
        standard_bottom_sheet = bottomSheet.findViewById(R.id.standard_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(standard_bottom_sheet)
        val layoutParams = standard_bottom_sheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT  // Ensure full screen height
        standard_bottom_sheet.layoutParams = layoutParams
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO

        var check5 =bottomSheet.findViewById<CheckBox>(R.id.check5)

        sortbyBottomSheetDialog.setContentView(bottomSheet)

        val window = sortbyBottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
    }

    override fun onBackPressed() {

        if (relative.visibility==View.VISIBLE){
            relativeProductList.visibility=View.VISIBLE
            relative.visibility=View.GONE
        }else{
            super.onBackPressed()



        }
    }
}