package co.com.mypt.onBoarding.personalize

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.CommonFunctionMethods.Companion.disableEditText
import co.com.mypt.CommonFunctionMethods.Companion.enableEditText
import co.com.mypt.R
import co.com.mypt.adapter.WeightScaleAdapter
import co.com.mypt.onBoarding.weightClass.ScaleSliderLayoutManager
import co.com.mypt.onBoarding.weightClass.Screen
import co.com.mypt.onBoarding.weightClass.Screen.dpToPx
import co.com.mypt.onBoarding.weightClass.WeightRepo
import co.com.mypt.utils.SharedWeightViewModel
import kotlin.math.abs


class WeightFragment : Fragment(), ScaleSliderLayoutManager.MovementListener {

    private val viewModel: SharedWeightViewModel by activityViewModels()
    lateinit var tvlbs: TextView
    lateinit var tvkg: TextView
    lateinit var weight1: EditText
    lateinit var unitTextView: TextView
    lateinit var repo: WeightRepo
//    lateinit var weightRecyclerView: RecyclerView
    var isEditable = false
    lateinit var imEdit: FrameLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_weight, container, false)
        repo = WeightRepo()
        tvlbs = view.findViewById(R.id.tvlbs)
        tvkg = view.findViewById(R.id.tvkg)
        imEdit = view.findViewById(R.id.imEdit)
//        weightRecyclerView = view.findViewById(R.id.weight_recycler_view)
        weight1 = view.findViewById(R.id.weight)
        unitTextView = view.findViewById(R.id.unit)

        // Handling kg and lbs button clicks
        tvkg.setOnClickListener {
            tvkg.setTextColor(resources.getColor(R.color.black))
            tvlbs.setTextColor(resources.getColor(R.color.lightgreycolor))

            tvlbs.background = null
            tvkg.background = resources.getDrawable(R.drawable.feet_button)

            repo.setUnitToKg()  // Set unit to kg
            updateWeightLabels()  // Update the labels to kg
        }

        tvlbs.setOnClickListener {
            tvkg.setTextColor(resources.getColor(R.color.lightgreycolor))
            tvlbs.setTextColor(resources.getColor(R.color.black))

            tvlbs.background = resources.getDrawable(R.drawable.feet_button)
            tvkg.background = null

            repo.setUnitToLbs()  // Set unit to lbs
            updateWeightLabels()  // Update the labels to lbs
        }
        /*weightRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(@NonNull rv: RecyclerView, dx: Int, dy: Int) {
               // val lm = rv.getLayoutManager() as LinearLayoutManager?

                val centerX = rv.width / 2
                var closestPos = -1
                var minDistance = Int.Companion.MAX_VALUE

                for (i in 0..<rv.getChildCount()) {
                    val child = rv.getChildAt(i)
                    val childCenter = (child.getLeft() + child.getRight()) / 2
                    val distance = abs(centerX - childCenter)

                    if (distance < minDistance) {
                        minDistance = distance
                        closestPos = rv.getChildAdapterPosition(child)
                    }
                }

                if (closestPos != -1) {
                    updateCenterPosition(rv, closestPos)
                }
            }
        })*/


        imEdit.setOnClickListener {
            isEditable = !isEditable
            if (isEditable) {
               enableEditText(weight1, requireContext())
            } else {
               disableEditText(weight1, requireContext())
            }
        }


        return view
    }
    private  val centerHeight = 33
    private  val level1Height = 30
    private  val level2Height = 27
    private  val level3Height = 24
    private var shortSpokeHeight = 20
    fun updateCenterPosition(rv: RecyclerView, centerPos: Int) {
        val lm = rv.layoutManager as LinearLayoutManager
        val start = (centerPos - 3).coerceAtLeast(0)
        val end = (centerPos + 3).coerceAtMost((rv.adapter?.itemCount?:0) - 1)

        for (i in start..end) {
            val child = lm.findViewByPosition(i) ?: continue
            val holder = rv.getChildViewHolder(child) as WeightScaleAdapter.ViewHolder

            val diff = kotlin.math.abs(i - centerPos)
            val heightDp = when (diff) {
                0 -> centerHeight
                1 -> level1Height
                2 -> level2Height
                3 -> level3Height
                else -> shortSpokeHeight
            }

            // Apply height safely
            val lp = holder.spoke.layoutParams
            lp.height = Screen.dpToPx(context, heightDp)
            holder.spoke.layoutParams = lp

            holder.value.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (centerPos == i) 15f else 14f)

            holder.value.setTextColor(
                if (centerPos == i)
                   resources.getColor(R.color.available)
                else
                    resources.getColor(R.color.rulartextcolor)
            )
        }
    }


    private fun updateWeightLabels() {
        val adapter = WeightScaleAdapter(activity)
        adapter.setData(repo.getWeightLabels())  // Update the adapter with the new weight labels

       /* weightRecyclerView.adapter = adapter
        weightRecyclerView.scrollToPosition(repo.startWeight)*/  // Scroll to the start weight

        // Update the weight label text with the selected unit
        val weightLabel = repo.getWeightLabels()[repo.startWeight]
//        val htmlString = "<big><b>$weightLabel</b></big><small><font color=#959595>${if (repo.isKg) "kg" else "lbs"}</font></small>"
//        val spanned = HtmlCompat.fromHtml(htmlString, HtmlCompat.FROM_HTML_MODE_COMPACT)
        updateWeight(weightLabel)
    }

    fun updateWeight(weightLabel:String){
        val unit=if (repo.isKg) "kg" else "lbs"
        unitTextView.text = unit
        weight1.setText(weightLabel)
        weight1.setSelection(weight1.text.length)
        val wtlabel=weightLabel+unit
        viewModel.data.value = wtlabel.toString()
    }
    override fun onItemSelected(selectedIndex: Int) {
       /* val adapter = weightRecyclerView.adapter
        if (adapter is WeightScaleAdapter) {
            adapter.setSelectedPosition(selectedIndex)
        }*/
    }

    private fun update7Items(rv: RecyclerView) {
        val lm = rv.layoutManager as LinearLayoutManager
        val centerX = rv.width / 2

        val minHeight = dpToPx(requireContext(),24)
        val maxHeight = dpToPx(requireContext(),33)

        // Find center adapter position
        val centerPos = lm.findFirstVisibleItemPosition() +
                (lm.findLastVisibleItemPosition() - lm.findFirstVisibleItemPosition()) / 2

        for (pos in centerPos - 3..centerPos + 3) {
            if (pos < 0 || pos >= lm.itemCount) continue

            val child = lm.findViewByPosition(pos) ?: continue
            val spoke = child.findViewById<View>(R.id.unit_spoke)

            val childCenterX = (child.left + child.right) / 2
            val distance = kotlin.math.abs(centerX - childCenterX)

            val scale = 1f - (distance.toFloat() / centerX)
                .coerceIn(0f, 1f)

            val newHeight = (minHeight + (maxHeight - minHeight) * scale).toInt()

            val lp = spoke.layoutParams
            if (lp.height != newHeight) {
                lp.height = newHeight
                spoke.layoutParams = lp
            }
        }
    }


    override fun onItemChanged(selectedIndex: String) {
        val number: Double = selectedIndex.toDouble()
        val weightInt = number.toInt()
        val fractional: Int = number.toString().split(".")[1].toInt()

       /* val htmlString = if(fractional == 0)
            "<big><b>$weightInt</b></big><small><font color=#959595>${if (repo.isKg) "kg" else "lbs"}</font></small>"
        else
            "<big><b>$selectedIndex</b></big><small><font color=#959595>${if (repo.isKg) "kg" else "lbs"}</font></small>"
        val spanned = HtmlCompat.fromHtml(htmlString, HtmlCompat.FROM_HTML_MODE_COMPACT)*/
        updateWeight(selectedIndex)
    }


    override fun onStart() {
        super.onStart()

        /*val padding = Screen.getScreenWidth(activity) / 2
        weightRecyclerView.setPadding(padding, 0, padding, 0)

        val adapter = WeightScaleAdapter(activity)
        adapter.setData(repo.getWeightLabels()) // Set the initial data based on the default unit
        val layoutManager = ScaleSliderLayoutManager(activity, this,repo.getWeightLabels())
        weightRecyclerView.layoutManager = layoutManager
        weightRecyclerView.itemAnimator = DefaultItemAnimator()
        weightRecyclerView.adapter = adapter

        weightRecyclerView.scrollToPosition(repo.startWeight)*/
//        weightRecyclerView.addItemDecoration(FadeEdgeDecoration(requireContext()))

        val weightLabel = repo.getWeightLabels()[repo.startWeight]
        /*val htmlString = "<big><b>$weightLabel</b></big><small><font color=#959595>${if (repo.isKg) "kg" else "lbs"}</font></small>"
        val spanned = HtmlCompat.fromHtml(htmlString, HtmlCompat.FROM_HTML_MODE_COMPACT)*/
        updateWeight(weightLabel)
    }

}
