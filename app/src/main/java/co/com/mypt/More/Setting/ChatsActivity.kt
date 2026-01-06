package co.com.mypt.More.Setting


import android.os.Bundle
import android.text.Html
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import co.com.mypt.R
import co.com.mypt.adapter.FullchatAdapter
import co.com.mypt.adapter.TypeWisechatAdapter
import co.com.mypt.model.FullChatModel
import co.com.mypt.model.TypeWiseChatModel


class ChatsActivity : AppCompatActivity() {
    lateinit var fullchatRecyclerview:RecyclerView
    lateinit var typeByChatRecyclerview:RecyclerView
    lateinit var headerLayout:LinearLayout
    lateinit var edsearch:EditText
    var fullchatArrayList :ArrayList<FullChatModel> = ArrayList()
    var typewisechartArrayList :ArrayList<TypeWiseChatModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        fullchatRecyclerview=findViewById(R.id.fullchatRecyclerview)
        typeByChatRecyclerview=findViewById(R.id.typeByChatRecyclerview)
        headerLayout=findViewById(R.id.headerLayout)
        edsearch=findViewById(R.id.edsearch)
        headerLayout.setOnClickListener{
            finish()
        }
        for (i in 0..6) {
            var typeWiseChatModel= TypeWiseChatModel()
            typeWiseChatModel.name="Clients"
            typewisechartArrayList.add(typeWiseChatModel)
        }
        var typeWiseChatAdapter = TypeWisechatAdapter(applicationContext, typewisechartArrayList)
        typeByChatRecyclerview.adapter = typeWiseChatAdapter

        for (i in 0..6) {
            var fullchatModel= FullChatModel()
            fullchatModel.name="Consultation for Diet Management Program"
            fullchatArrayList.add(fullchatModel)
        }
        var fullChatAdapter = FullchatAdapter(applicationContext, fullchatArrayList)
        fullchatRecyclerview.adapter = fullChatAdapter


        val text = "<font color=#959595>Search in </font> <font color=#FAFAFA>Past Chats</font>"
        edsearch.setHint(Html.fromHtml(text))
    }
}