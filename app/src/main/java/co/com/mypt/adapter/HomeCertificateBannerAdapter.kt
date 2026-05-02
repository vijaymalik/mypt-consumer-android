package co.com.mypt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import co.com.mypt.R
import co.com.mypt.model.BannerItem
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import java.util.Objects

class HomeCertificateBannerAdapter(
    private val context: Context,
    initialImages: List<BannerItem> = emptyList()
) : PagerAdapter() {
    private val images = mutableListOf<BannerItem>().apply {
        addAll(initialImages)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val mLayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView: View =
            mLayoutInflater.inflate(R.layout.adapter_home_certificate_banner_item, container, false)
        val imageView: ShapeableImageView = itemView.findViewById<View>(R.id.idIVImage) as ShapeableImageView
        val imageUrl = images[position].imageUrl
        imageUrl?.let {
            Glide.with(context).load(it).fitCenter().into(imageView)
        }
        Objects.requireNonNull(container).addView(itemView)
        return itemView
    }

    override fun getCount() = images.size

    override fun isViewFromObject(view: View, obj: Any) = view == obj

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as RelativeLayout)
    }

    fun updateData(newItems: List<BannerItem>) {
        images.clear()
        images.addAll(newItems)
        notifyDataSetChanged()
    }
}
