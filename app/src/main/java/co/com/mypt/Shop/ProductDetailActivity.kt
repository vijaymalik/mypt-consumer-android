package co.com.mypt.Shop

import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.os.Bundle
import android.text.TextPaint
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import co.com.mypt.R
import co.com.mypt.adapter.ChooseColorAdapter
import co.com.mypt.adapter.FeatureListAdapter
import co.com.mypt.adapter.ProductimageSliderAdapter
import co.com.mypt.adapter.SimilarProductAdapter
import co.com.mypt.model.ColorShopModel
import co.com.mypt.model.FeatureListModel
import co.com.mypt.model.ImageModel
import co.com.mypt.model.SimilarProductModel
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class ProductDetailActivity : AppCompatActivity() {
    lateinit var viewPager: ViewPager
    lateinit var tvprice:TextView
    lateinit var tvcurrency:TextView
    lateinit var tvRealPrice:TextView
    lateinit var AddTocart:CardView
    lateinit var ChooseColorrecyclerView:RecyclerView
    lateinit var similarRecyclerView:RecyclerView
    private lateinit var adapter: ProductimageSliderAdapter
    private lateinit var chooseColoradapter: ChooseColorAdapter
    var imageArrayList :ArrayList<ImageModel> = ArrayList()
    var chooseColorList :ArrayList<ColorShopModel> = ArrayList()
    var similarProductList :ArrayList<SimilarProductModel> = ArrayList()
    lateinit var dots_indicator: DotsIndicator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_product_detail)
        viewPager=findViewById(R.id.idViewPager)
        dots_indicator = findViewById(R.id.dots_indicator)
        tvprice=findViewById<TextView>(R.id.tvprice)
        tvcurrency=findViewById<TextView>(R.id.tvcurrency)
        tvRealPrice=findViewById<TextView>(R.id.tvRealPrice)
        similarRecyclerView=findViewById(R.id.similarRecyclerView)
        ChooseColorrecyclerView=findViewById(R.id.ChooseColorrecyclerView)
        AddTocart=findViewById(R.id.AddTocart)
        for (i in 0..6) {
            var imageModel= ImageModel()
            imageModel.image= R.drawable.gymgirl

            imageArrayList.add(imageModel)
        }
        adapter = ProductimageSliderAdapter(this,imageArrayList)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 1
        dots_indicator.attachTo(viewPager)

        textShader(tvprice)
        textShader(tvcurrency)
        tvRealPrice.paintFlags = tvRealPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG


        for (i in 0..6) {
            var colorShopModel= ColorShopModel()
            colorShopModel.name="Black"
            chooseColorList.add(colorShopModel)
        }
        var choosecolorAdapter = ChooseColorAdapter(applicationContext, chooseColorList)
        ChooseColorrecyclerView.adapter = choosecolorAdapter
        for (i in 0..6) {
            var similarProductModel= SimilarProductModel()
            similarProductModel.price="299"
            similarProductList.add(similarProductModel)
        }
        var similarProductAdapter = SimilarProductAdapter(applicationContext, similarProductList)
        similarRecyclerView.adapter = similarProductAdapter

        AddTocart.setOnClickListener{
            var intent= Intent(applicationContext,CartActivity::class.java)
            startActivity(intent)
        }

    }
    private fun textShader(tv: TextView) {
        val paint: TextPaint = tv.paint
        val width = paint.measureText(tv.text.toString())

        val textShader: Shader = LinearGradient(
            0f, 0f, width, tv.textSize, intArrayOf(
                Color.parseColor("#FAFAFA"),
                Color.parseColor("#9EBCFF"),
            ), null, Shader.TileMode.CLAMP
        )
        tv.paint.setShader(textShader)
    }
}