package co.com.mypt.onBoarding.personalize

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import co.com.calculateheight.MyScaleView
import co.com.mypt.CommonFunctionMethods.Companion.disableEditText
import co.com.mypt.CommonFunctionMethods.Companion.enableEditText
import co.com.mypt.CommonFunctionMethods.Companion.listenKeyboardHeight
import co.com.mypt.R
import co.com.mypt.rulerHeight.CenterWaveScaleViewFeet
import co.com.mypt.rulerHeight.onViewUpdateListenerFeet
import co.com.mypt.utils.SharedHeightViewModel
import onViewUpdateListener
import kotlin.math.roundToInt


// TODO: Rename parameter arguments, choose names that match
class HeightFragment : Fragment() {

    private val viewModel: SharedHeightViewModel by activityViewModels()

    lateinit var tvcms: TextView
    lateinit var tvfeet: TextView
//    lateinit var tvError: TextView
    lateinit var txtValue: EditText
    lateinit var imEdit: FrameLayout
//    lateinit var rootLayout: LinearLayout

    //    lateinit var  unitTextView: TextView
    var isEditable = false
    var cm = "70"
    var ft = "5"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_height, container, false)
        tvcms = view.findViewById(R.id.tvcms)
        tvfeet = view.findViewById(R.id.tvfeet)
        imEdit = view.findViewById(R.id.imEdit)
//        rootLayout = view.findViewById(R.id.linear)

        val myScaleView = view.findViewById<MyScaleView>(R.id.myScaleViewcms)
        val myScaleViewfeet = view.findViewById<CenterWaveScaleViewFeet>(R.id.myScaleViewfeet)
        txtValue = view.findViewById(R.id.txt_height)
//        tvError = view.findViewById(R.id.txt_height)
//        unitTextView =view.findViewById(R.id.unit)
        imEdit.setOnClickListener {
            isEditable = !isEditable
            if (isEditable) {
                enableEditText(txtValue, requireContext())
            } else {
                disableEditText(txtValue, requireContext())
            }
        }

        myScaleView.initializeStartingPoint(70.1F)
        myScaleView.setUpdateListener(object : onViewUpdateListener {
            override fun onViewUpdate(value: Float) {
                println("======  $value")
                var  value=if (value>.1) value.minus(.1).toFloat() else value
                 value = (value * 10f).roundToInt().toFloat() / 10f

                println("======2  $value")
//                val htmlString = "<big><b>$value</b></big><small><font color=#959595>cm</font></small>"
//                val spanned = HtmlCompat.fromHtml(htmlString, HtmlCompat.FROM_HTML_MODE_COMPACT)


//                unitTextView.text="cm"
                val finalHeight = "$value" + "cm"
                txtValue.setText(finalHeight)
                cm = finalHeight
                viewModel.data.value = txtValue.text.toString()
            }
        })

        myScaleViewfeet.initializeStartingPoint(5.1F)

//        ft = "<big><b>$ft</b></big><small><font color=#959595>ft</font></small>"
//        val spanned = HtmlCompat.fromHtml(ft, HtmlCompat.FROM_HTML_MODE_COMPACT)
        val finalHeight = "$ft ft"
        txtValue.setText(finalHeight)
        viewModel.data.value = txtValue.text.toString()

        //txtValue.text = "${Html.fromHtml("<font><big>$ft</big></font><font><small>ft</small></font>", HtmlCompat.FROM_HTML_MODE_LEGACY)}"
        myScaleViewfeet.setUpdateListenerfeet(object : onViewUpdateListenerFeet {
            override fun onViewUpdate(valueL: Float) {
                val value=if (valueL>.1) valueL.minus(.1).toFloat() else valueL
                val feet = value.toInt() // Extract integer part as feet
                val inches = ((value - feet) * 12).toInt() // Convert decimal part to inches
                println("==== $feet")
                ft = if (inches == 0) "$feet ft"
                else "$feet ft $inches in"
                txtValue.setText(ft)
                viewModel.data.value = txtValue.text.toString()
            }
        })

        txtValue.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) { }

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) { }

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString().trim()

                if (text.isEmpty()) return


//                if (!isValid) {
//                    tvError.error = "Format should be like: 5 ft 8 in"
//                } else {
//                    tvError.error = null
//                }
                viewModel.data.value = text
            }
        })

        tvfeet.setOnClickListener {
            tvfeet.setTextColor(resources.getColor(R.color.black))
            tvcms.setTextColor(resources.getColor(R.color.smallTextcolor))

//            val spannedText = HtmlCompat.fromHtml(ft, HtmlCompat.FROM_HTML_MODE_COMPACT)
            val finalHeight = if (ft.contains("ft")) ft else "$ft" + "ft"
            txtValue.setText(finalHeight)
            viewModel.data.value = txtValue.text.toString()

            myScaleViewfeet.visibility = View.VISIBLE
            myScaleView.visibility = View.GONE
            tvcms.background = null
            tvfeet.background = resources.getDrawable(R.drawable.feet_button)

        }
        tvcms.setOnClickListener {
//            val htmlStringCm = "<big><b>$cm</b></big><small><font color=#959595>cm</font></small>"
//            val spannedCM = HtmlCompat.fromHtml(htmlStringCm, HtmlCompat.FROM_HTML_MODE_COMPACT)
            val finalHeight = if (cm.contains("cm")) cm else "$cm" + "cm"

            txtValue.setText(finalHeight)

            viewModel.data.value = txtValue.text.toString()

            tvfeet.setTextColor(resources.getColor(R.color.smallTextcolor))
            tvcms.setTextColor(resources.getColor(R.color.black))
            myScaleViewfeet.visibility = View.GONE
            myScaleView.visibility = View.VISIBLE
            tvcms.background = resources.getDrawable(R.drawable.feet_button)
            tvfeet.background = null
        }

        return view
    }


    fun setLinearLayoutEnd(linearLayout: LinearLayout, targetViewId: Int?) {
        val parent = linearLayout.parent as ConstraintLayout
        val constraintSet = ConstraintSet()
        constraintSet.clone(parent)

        if (targetViewId != null) {
            // Constrain end to another view
            constraintSet.connect(
                linearLayout.id,
                ConstraintSet.END,
                targetViewId,
                ConstraintSet.START,
                0 // optional margin
            )
        } else {
            // Constrain end to parent
            constraintSet.connect(
                linearLayout.id,
                ConstraintSet.END,
                parent.id,
                ConstraintSet.END,
                0 // optional margin
            )
        }

        constraintSet.applyTo(parent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       val  heightTextView = view.findViewById<LinearLayout>(R.id.heightTextView)
       val  scalerView = view.findViewById<LinearLayout>(R.id.scalerView)
       val  verticalGlow = view.findViewById<ImageView>(R.id.verticalGlow)

            ViewCompat.setOnApplyWindowInsetsListener(requireView()) { _, insets ->
                if (insets.isVisible(WindowInsetsCompat.Type.ime())) {
                    //setLinearLayoutEnd(heightTextView, R.id.scalerView)
                    enableEditText(txtValue, requireContext())
                    setHeightTextEndTo(heightTextView, false)
                    scalerView.visibility= View.GONE
                    verticalGlow.visibility= View.GONE
                    val parentLayout = txtValue.parent as? View
                    parentLayout?.let { parent ->
                        val params = parent.layoutParams as? ViewGroup.MarginLayoutParams
                        params?.let {
                            it.bottomMargin = 150
                            parent.layoutParams = it
                        }
                    }
                } else {
                    disableEditText(txtValue, requireContext())
                    setHeightTextEndTo(heightTextView, true)
                    scalerView.visibility= View.VISIBLE
                    verticalGlow.visibility= View.VISIBLE
//                    setLinearLayoutEnd(heightTextView, null)
                    val parentLayout = txtValue.parent as? View
                    parentLayout?.let { parent ->
                        val params = parent.layoutParams as? ViewGroup.MarginLayoutParams
                        params?.let {
                            it.bottomMargin = 0
                            parent.layoutParams = it
                        }
                    }
                }
                insets
            }

    }
    fun setHeightTextEndTo(
        heightTextView: LinearLayout,
        endToScaler: Boolean
    ) {
        val params = heightTextView.layoutParams as ConstraintLayout.LayoutParams

        if (endToScaler) {
            // END → START of scalerView
            params.endToStart = R.id.scalerView
            params.endToEnd = ConstraintLayout.LayoutParams.UNSET
        } else {
            // END → END of parent
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            params.endToStart = ConstraintLayout.LayoutParams.UNSET
        }

        heightTextView.layoutParams = params
    }

}