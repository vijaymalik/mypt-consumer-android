package co.com.FCM

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import co.com.mypt.ActiveSession.BeforeArrivingActivity
import co.com.mypt.ActiveSession.ProgressBarActivity
import co.com.mypt.R
import co.com.mypt.activities.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private var notificationType = ""
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${message.from}")

        // Check if message contains a data payload.
        if (message.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${message.data}")
            Log.e("body",""+message.data.get("body"))
            val jObjResponse = JSONObject(message.data)
            notificationType=jObjResponse.optString("type")
            Log.e("notificationType",""+notificationType)

            if (notificationType.equals("session_start",true)){
                var intent = Intent(this, ProgressBarActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("bookingid", jObjResponse.optString("booking_id"))
                startActivity(intent)
            }
        }

        // Check if message contains a notification payload.
        message.notification?.let {
                Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(it.body.toString())
        }

        Log.d(TAG, "onMessageReceived: body ${message.data.get("body")}")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d(TAG, "onNewToken: refreshedToken : $token")
    }

    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val requestCode = 0
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = "fcm_default_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle("FCM Message")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

}