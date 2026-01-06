package co.com.mypt.Shop

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.CartAdapter
import co.com.mypt.adapter.ChooseColorAdapter
import co.com.mypt.model.CartModel
import co.com.mypt.model.ColorShopModel
import co.com.mypt.model.SimilarProductModel

class CartActivity : AppCompatActivity() {
    lateinit var cartRecyclerView:RecyclerView
    lateinit var relative:RelativeLayout
    lateinit var headerLayout:LinearLayout
    var cartList :ArrayList<CartModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        cartRecyclerView=findViewById(R.id.cartRecyclerView)
        relative=findViewById(R.id.relative)
        headerLayout=findViewById(R.id.headerLayout)
        relative.setOnClickListener{

        }
        headerLayout.setOnClickListener{
            finish()
        }
        for (i in 0..2) {
            var cartModel= CartModel()
            cartModel.price="299"
            cartList.add(cartModel)
        }
        var cartAdapter = CartAdapter(applicationContext, cartList)
        cartRecyclerView.adapter = cartAdapter
    }
}