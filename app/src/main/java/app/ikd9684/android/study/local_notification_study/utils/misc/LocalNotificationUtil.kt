package app.ikd9684.android.study.local_notification_study.utils.misc

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import org.joda.time.DateTime

class LocalNotificationUtil {

    companion object {
        private const val datetimePattern = "yyyy/MM/dd HH:mm:ss.SSSZZ"

        private var alarmManager: AlarmManager? = null

        private val pendingIntentMap = mutableMapOf<String, PendingIntent>()

        fun addAlarm(
            context: Context,
            name: String,
            requestCode: Int,
            triggerAtTime: DateTime,
            contentTitle: String,
            contentText: String,
            channelName: String,
            channelDescription: String
        ) {
            val id = "$name.$requestCode"
            val intent = Intent(context, AlarmReceiver::class.java)
            intent.putExtra(AlarmReceiver.EXTRA_CONTENT_TITLE, contentTitle)
            intent.putExtra(AlarmReceiver.EXTRA_CONTENT_TEXT, contentText)
            intent.putExtra(AlarmReceiver.EXTRA_CHANNEL_NAME, channelName)
            intent.putExtra(AlarmReceiver.EXTRA_CHANNEL_DESCRIPTION, channelDescription)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )

            pendingIntentMap[id]?.let { oldPendingIntent ->
                alarmManager?.run {
                    cancel(oldPendingIntent)
                }
                pendingIntentMap.remove(id)
            }
            pendingIntentMap[id] = pendingIntent

            alarmManager ?: run {
                alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            }

            // アラームをセットする
            alarmManager?.setExact(AlarmManager.RTC_WAKEUP, triggerAtTime.millis, pendingIntent)
            Log.d(
                "LocalNotificationUtil",
                "start id=$id, ${triggerAtTime.toString(datetimePattern)}"
            )
        }

        fun cancelAlarm(name: String, requestCode: Int) {
            val id = "$name.$requestCode"
            // アラームを解除する
            pendingIntentMap.remove(id)?.let { pendingIntent ->
                alarmManager?.cancel(pendingIntent)
                Log.d("LocalNotificationUtil", "cancel: id=$id")
            }
        }
    }
}
