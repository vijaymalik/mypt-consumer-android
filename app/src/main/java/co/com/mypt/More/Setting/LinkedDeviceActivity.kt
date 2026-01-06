package co.com.mypt.More.Setting

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.DeviceAdapter
import co.com.mypt.adapter.MealVerticalListAdapter
import co.com.mypt.model.DeviceModel
import co.com.mypt.model.MealListModel

class LinkedDeviceActivity : AppCompatActivity() {
    lateinit var deviceRecyler:RecyclerView
    lateinit var deviceAdapter:DeviceAdapter
    var deviceArrayList = ArrayList<DeviceModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_linked_device)
        deviceRecyler=findViewById(R.id.deviceRecyler)
        for (i in 0..4) {
            var deviceModel= DeviceModel()
            deviceModel.name="Joe’s Apple Watch"
            deviceArrayList.add(deviceModel)
        }
        deviceRecyler.adapter = DeviceAdapter(applicationContext,deviceArrayList)

    }
}