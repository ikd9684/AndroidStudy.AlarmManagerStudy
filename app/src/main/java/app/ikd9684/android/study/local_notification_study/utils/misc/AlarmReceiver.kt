package app.ikd9684.android.study.local_notification_study.utils.misc

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import app.ikd9684.android.study.local_notification_study.R
import app.ikd9684.android.study.local_notification_study.activities_fragments.MainActivity

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = 0
        const val PRIMARY_CHANNEL_ID = "primary_notification_channel"

        private const val ROOT = "app.ikd9684.android.study.local_notification_study.utils.misc."
        const val EXTRA_CONTENT_TITLE = "$ROOT.EXTRA_CONTENT_TITLE"
        const val EXTRA_CONTENT_TEXT = "$ROOT.EXTRA_CONTENT_TEXT"
        const val EXTRA_CHANNEL_NAME = "$ROOT.EXTRA_CHANNEL_NAME"
        const val EXTRA_CHANNEL_DESCRIPTION = "$ROOT.EXTRA_CHANNEL_DESCRIPTION"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "Received intent : $intent")

        val contentTitle = intent.getStringExtra(EXTRA_CONTENT_TITLE) ?: "title:-"
        val contentText = intent.getStringExtra(EXTRA_CONTENT_TEXT) ?: "text:-"
        val channelName = intent.getStringExtra(EXTRA_CHANNEL_NAME) ?: ""
        val channelDescription = intent.getStringExtra(EXTRA_CHANNEL_DESCRIPTION) ?: ""

        deliverNotification(context, contentTitle, contentText, channelName, channelDescription)
    }

    private fun deliverNotification(
        context: Context,
        contentTitle: String,
        contentText: String,
        channelName: String,
        channelDescription: String
    ) {
        val contentIntent = Intent(context, MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder = NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_directions_bus_24)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setContentIntent(contentPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        (context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.let { notificationManager ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(
                    PRIMARY_CHANNEL_ID,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.RED
                notificationChannel.enableVibration(true)
                notificationChannel.description = channelDescription
                notificationManager.createNotificationChannel(notificationChannel)
            }

            notificationManager.notify(NOTIFICATION_ID, builder.build())
        }
    }
}
