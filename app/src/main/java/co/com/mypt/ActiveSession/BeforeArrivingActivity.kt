package co.com.mypt.ActiveSession

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.TextAppearanceSpan
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.Api.ApiURL
import co.com.mypt.Api.GetMethod
import co.com.mypt.Api.ResponseData
import co.com.mypt.ProgressDialog
import co.com.mypt.R
import co.com.mypt.adapter.CheckListAdapter
import co.com.mypt.adapter.TrainerAmenitiesAdapter
import co.com.mypt.model.ActivityModel
import co.com.mypt.model.CheckArrayModel
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.maps.android.PolyUtil
import org.json.JSONObject

class BeforeArrivingActivity : AppCompatActivity() , OnMapReadyCallback {
    lateinit var arrivingBottomSheetDialog:BottomSheetDialog
    var activitiesModelList :ArrayList<ActivityModel> = ArrayList()
    lateinit var recycler: RecyclerView
    lateinit var tvAddress: TextView
    lateinit var tvTrainerText: TextView
    lateinit var tvTime: TextView
    lateinit var tvTrainer_name: TextView
    lateinit var linearthings: LinearLayout
    lateinit var cardDialer: CardView
    lateinit var im: ImageView
    lateinit var tvTrainerSessionDetail: TextView
    lateinit var ed1: EditText
    lateinit var ed2: EditText
    lateinit var ed3: EditText
    lateinit var ed4: EditText
    private lateinit var type: String
    private lateinit var map: GoogleMap
    var trainer_lat=0.0
    var trainer_long=0.0
    var user_lat=0.0
    var user_long=0.0
    var trainerLatlng: LatLng? = null
    var userLatlng: LatLng? = null
    private var plannedPath :  List<LatLng> = mutableListOf()
    private var remainingPolyline: Polyline? = null
    lateinit var checkListBottomSheetDialog:BottomSheetDialog
    private lateinit var checkArrayList: ArrayList<CheckArrayModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_befor_arriving)
        checkListBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        recycler=findViewById(R.id.recycler)
        tvAddress=findViewById(R.id.tvAddress)
        cardDialer=findViewById(R.id.cardDialer)
        tvTrainerSessionDetail=findViewById(R.id.tvTrainerSessionDetail)
        ed1=findViewById(R.id.ed1)
        ed2=findViewById(R.id.ed2)
        ed3=findViewById(R.id.ed3)
        tvTime=findViewById(R.id.tvTime)
        linearthings=findViewById(R.id.linearthings)
        tvTrainerText=findViewById(R.id.tvTrainerText)
        tvTrainer_name=findViewById(R.id.tvTrainer_name)
        im=findViewById(R.id.im)
        ed4=findViewById(R.id.ed4)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        chekListAlert()
       // arrivingBottomsheet()

        tvTrainerSessionDetail.setOnClickListener {
         finish()

        }
        linearthings.setOnClickListener {
         checkListBottomSheetDialog.show()

        }

    }
       private fun chekListAlert() {
        val bottomSheet = layoutInflater.inflate(R.layout.checklist_bottomsheet, null)
        checkListBottomSheetDialog.setCancelable(true)
        checkListBottomSheetDialog.setContentView(bottomSheet)
        var recycler=bottomSheet.findViewById<RecyclerView>(R.id.recycler)
           checkArrayList = ArrayList()
           checkArrayList.add(CheckArrayModel("Space","Ensure you have a clear, spacious area to exercise and any obstacles to avoid injuries. "))
           checkArrayList.add(CheckArrayModel("Equipment Ready","Prepare all necessary workout equipment (e.g., dumbbells, resistance bands, yoga)"))
           checkArrayList.add(CheckArrayModel("Water Bottle","Stay hydrated! Have a filled water bottle nearby for easy access during breaks."))
           checkArrayList.add(CheckArrayModel("Workout Clothes","Wear comfortable workout clothes that allow free movement."))
           checkArrayList.add(CheckArrayModel("Health Considerations","Inform Christene of any aches or injuries that might affect today’s session."))
           var checklistAdapter= CheckListAdapter(this@BeforeArrivingActivity, checkArrayList)
           recycler.adapter = checklistAdapter
        val bottomSheetBehaviour = checkListBottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheetBehaviour!!)
        behavior.isFitToContents = true
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.maxHeight = (resources.displayMetrics.heightPixels * 0.7).toInt()  // Prevent full screen
        behavior.isHideable = true


        val window = checkListBottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
        checkListBottomSheetDialog.setContentView(bottomSheet)
           bottomSheetBehaviour?.setBackgroundResource(android.R.color.transparent)
    }
    fun arrivingBottomsheet() {
        arrivingBottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)

        val bottomSheet = layoutInflater.inflate(R.layout.before_arriving_bottomsheet, null)
        arrivingBottomSheetDialog.setContentView(bottomSheet)

        var recycler=bottomSheet.findViewById<RecyclerView>(R.id.recycler)
        var tvAddress=bottomSheet.findViewById<TextView>(R.id.tvAddress)
        var ed1=bottomSheet.findViewById<EditText>(R.id.ed1)
        var ed2=bottomSheet.findViewById<EditText>(R.id.ed2)
        var ed3=bottomSheet.findViewById<EditText>(R.id.ed3)
        var ed4=bottomSheet.findViewById<EditText>(R.id.ed4)
        val spannable = SpannableString("- 23B,Dubai Mall Street Road")
        val textAppearanceSpan = TextAppearanceSpan(
            applicationContext, R.style.TextAppearance_ManropeMedium
        )

        spannable.setSpan(
            textAppearanceSpan,
            0, // start at "23B"
            spannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

     //   tvAddress.text = TextUtils.concat("Arriving at your home ", spannable)




        val window = arrivingBottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation

        animateBottomSheet(arrivingBottomSheetDialog)

        arrivingBottomSheetDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        arrivingBottomSheetDialog.show()
    }
    private fun animateBottomSheet(bottomSheetDialog: BottomSheetDialog) {
        val window = bottomSheetDialog.window
        window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
    }

    private fun getTrainerTrack() {
        val progressDialog: Dialog = ProgressDialog.progressDialog(this,"")
        progressDialog.show()
        var api=""

        api= ApiURL.get_trainertime+getIntent().getStringExtra("bookingid")
        Log.e("trainerTackApi",""+api)

        GetMethod(api,applicationContext).startMethod(object : ResponseData {

            override fun response(data: String?) {
                progressDialog.dismiss()
                activitiesModelList.clear()
                Log.e("TrackTrainerDetailResponse",data.toString())
                try {
                    val jsonObj = JSONObject(data!!)
                    if (jsonObj.optBoolean("status")){
                        tvTrainer_name.setText(jsonObj.optJSONObject("data").optJSONObject("trainer").optString("name"))
                        tvTrainerText.setText("Things to prepare before "+jsonObj.optJSONObject("data").optJSONObject("trainer").optString("name")+" arrives")
                        type=jsonObj.optJSONObject("data").optJSONObject("booking").optString("type")
                        trainer_lat=jsonObj.optJSONObject("data").optJSONObject("trainer").optDouble("lat")
                        trainer_long=jsonObj.optJSONObject("data").optJSONObject("trainer").optDouble("long")

                        user_lat=jsonObj.optJSONObject("data").optJSONObject("booking").optDouble("lat")
                        user_long=jsonObj.optJSONObject("data").optJSONObject("booking").optDouble("long")
                        if(jsonObj.optJSONObject("data").optJSONObject("booking").optString("isVerified").equals("true")){
                            var intent= Intent(this@BeforeArrivingActivity, ProgressBarActivity::class.java)
                            intent.putExtra("bookingid",getIntent().getStringExtra("bookingid"))
                            startActivity(intent)
                            finish()
                        }
                        cardDialer.setOnClickListener {
                            var phoneNumber = jsonObj.optJSONObject("data").optJSONObject("trainer").optString("phone") // Replace with the desired phone number
                            var dialIntent = Intent(Intent.ACTION_DIAL)
                            dialIntent.data = Uri.parse("tel:$phoneNumber")
                            startActivity(dialIntent)

                        }
                        val spannable = SpannableString("-"+jsonObj.optJSONObject("data").optJSONObject("booking").optString("address"))
                        val textAppearanceSpan = TextAppearanceSpan(
                            applicationContext, R.style.TextAppearance_ManropeMedium
                        )

                        spannable.setSpan(
                            textAppearanceSpan,
                            0, // start at "23B"
                            spannable.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        tvAddress.text = TextUtils.concat("Arriving at your home ", spannable)
                        Glide.with(applicationContext).load(jsonObj.optJSONObject("data").optJSONObject("trainer").optString("profile")).into(im)
                        val otpinput = jsonObj.optJSONObject("data").optJSONObject("booking").optString("otp")
                        val charArray: CharArray = otpinput.toCharArray()
                        ed1.setText(""+charArray[0])
                        ed2.setText(""+charArray[1])
                        ed3.setText(""+charArray[2])
                        ed4.setText(""+charArray[3])
                        tvTime.setText(jsonObj.optJSONObject("data").optJSONObject("booking").optString("arrivingTime").replace("Mins",""))
                        for(i in 0 until jsonObj.optJSONObject("data").optJSONObject("trainer")!!.optJSONArray("tags").length())
                        {
                            var activityModel=ActivityModel()
                            activityModel.name= jsonObj.optJSONObject("data").optJSONObject("trainer")!!.optJSONArray("tags").get(i).toString()
                            activitiesModelList.add(activityModel)
                        }
                        var activityAdapter = TrainerAmenitiesAdapter(applicationContext, activitiesModelList)
                        recycler.adapter = activityAdapter

                        // Create custom icons
                        val userIcon = getResizedBitmapDescriptor(applicationContext, R.drawable.user_marker, 75, 80)
                        val trainerIcon =getResizedBitmapDescriptor(applicationContext, R.drawable.trainer_marker, 100, 100)

                        // Marker 1: Dubai (Red Icon)
                        userLatlng = LatLng(user_lat, user_long)
                        map.addMarker(
                            MarkerOptions()
                                .position(userLatlng!!)
                                .icon(userIcon)
                        )

                        // Marker 2: New Delhi (Blue Icon)
                        trainerLatlng = LatLng(trainer_lat, trainer_long)
                        map.addMarker(
                            MarkerOptions()
                                .position(trainerLatlng!!)
                                .title("New Delhi")
                                .icon(trainerIcon)
                        )

                        val bounds = LatLngBounds.builder().include(userLatlng!!).include(
                            trainerLatlng!!
                        ).build()
                        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                        getRoute()

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

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.map)
            )
            if (!success) {
                Log.e("MapStyle", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MapStyle", "Can't find style. Error: ", e)
        }

        getTrainerTrack()

    }
    fun getResizedBitmapDescriptor(context: Context, drawableResId: Int, width: Int, height: Int): BitmapDescriptor {
        val drawable = ContextCompat.getDrawable(context, drawableResId) ?: return BitmapDescriptorFactory.defaultMarker()
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
    private fun getRoute(){
        val url = "https://routes.googleapis.com/directions/v2:computeRoutes?key=AIzaSyBcjdk3tch99jhgrQx2miMW3xdRW9By8Vc"

        val jsonBody = JSONObject().apply {
            put("origin", JSONObject().apply {
                put("location", JSONObject().apply {
                    put("latLng", JSONObject().apply {
                        put("latitude", trainerLatlng?.latitude)
                        put("longitude", trainerLatlng?.longitude)
                    })
                })
            })
            put("destination", JSONObject().apply {
                put("location", JSONObject().apply {
                    put("latLng", JSONObject().apply {
                        put("latitude", userLatlng?.latitude)
                        put("longitude", userLatlng?.longitude)
                    })
                })
            })
            put("travelMode", "DRIVE")
        }

        val request = object : JsonObjectRequest(Method.POST,url,jsonBody,Response.Listener { response ->
            Log.d("routesResponse", "getRoute: $response")
            val routes = response.optJSONArray("routes")
            if (routes != null && routes.length() > 0){
                val route = routes.getJSONObject(0)
                val polylineObj = route.optJSONObject("polyline")
                val encodedPolyline = polylineObj?.optString("encodedPolyline")
                if (!encodedPolyline.isNullOrEmpty()) {
                    plannedPath = PolyUtil.decode(encodedPolyline)
                    //tvTime.text = route.optString("duration").replace("s","")

                   /* map?.addMarker(
                        MarkerOptions()
                            .position(trainerLatlng!!)
                    )
                    map?.animateCamera(CameraUpdateFactory.newLatLngZoom(trainerLatlng!!, 17.0f))*/
                    drawOrUpdateRemainingPolyline(plannedPath)
                }
            }
        },Response.ErrorListener {error ->
            Log.d("routesError", "getRoute: $error")
        }){
            override fun getHeaders(): MutableMap<String, String> {
                val header : MutableMap<String,String> = HashMap()
                header["Content-Type"] = "application/json"
                // IMPORTANT: request what fields you want in response
                header["X-Goog-FieldMask"] = "routes.duration,routes.distanceMeters,routes.polyline.encodedPolyline"
                return header
            }
        }

        Volley.newRequestQueue(this).add(request)


    }

    private fun drawOrUpdateRemainingPolyline(path: List<LatLng>) {
        if (remainingPolyline == null) {
            remainingPolyline = map?.addPolyline(
                PolylineOptions()
                    .addAll(path)
                    .width(6f)
                    .color(ContextCompat.getColor(this, R.color.orangecolor))
            )
        } else {
            remainingPolyline?.points = path
        }
    }


}