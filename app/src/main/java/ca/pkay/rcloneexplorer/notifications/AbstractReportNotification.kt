package ca.pkay.rcloneexplorer.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.preference.PreferenceManager
import ca.pkay.rcloneexplorer.BroadcastReceivers.ClearReportBroadcastReciever
import ca.pkay.rcloneexplorer.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "notifications")


abstract class AbstractReportNotification (override var mContext: Context) : AbstractSyncNotification(mContext) {


    fun useReports(): Boolean {
        val mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        return mSharedPreferences.getBoolean(mContext.getString(R.string.pref_key_app_notification_reports), true)
    }


    fun setLastNotification(preference: Preferences.Key<Int>, id: Int) {
        runBlocking {
            mContext.dataStore.edit { settings ->
                settings[preference] =  id
            }
        }
    }

    fun cancelLastNotification(preference: Preferences.Key<Int>) {
        val prefMap = runBlocking { mContext.dataStore.data.first().asMap() }
        val notificationManager = NotificationManagerCompat.from(mContext)
        notificationManager.cancel((prefMap[preference] ?: 0) as Int)
    }

    fun getCollectedMessages(preference: Preferences.Key<String>): Int {
        val prefMap = runBlocking { mContext.dataStore.data.first().asMap() }
        val notificationContent = prefMap[preference].toString()
        return notificationContent.lines().size
    }

    fun addToReport(preference: Preferences.Key<String>, title: String, line: String) {
        val content = "$title: $line\n"
        val prefMap = runBlocking { mContext.dataStore.data.first().asMap() }
        runBlocking {
            mContext.dataStore.edit { settings ->
                val currentCounterValue: String = (prefMap[preference] ?: "") as String
                if(currentCounterValue.isEmpty()) {
                    settings[preference] = currentCounterValue + content
                } else {
                    settings[preference] =  content + currentCounterValue
                }
            }
        }
    }

    fun showReport(preference: Preferences.Key<String>,
                   notificationId: Int,
                   deleteAction: String,
                   iconid: Int,
                   titleResourceId: Int,
                   shorttextResourceId: Int,
                   title: String,
                   line: String) {
        val content = "$title: $line\n"

        val prefMap = runBlocking { mContext.dataStore.data.first().asMap() }
        runBlocking {
            mContext.dataStore.edit { settings ->
                val currentCounterValue: String = (prefMap[preference] ?: "") as String
                if(currentCounterValue.isEmpty()) {
                    settings[preference] = currentCounterValue + content
                } else {
                    settings[preference] =  content + currentCounterValue
                }
            }
        }
        val notificationContent: String = content + prefMap[preference].toString()



        val builder = NotificationCompat.Builder(mContext, ReportNotifications.CHANNEL_REPORT_ID)
            .setSmallIcon(iconid)
            .setContentTitle(mContext.getString(titleResourceId))
            .setContentText(mContext.getString(shorttextResourceId, notificationContent.lines().size-1))
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    notificationContent
                )
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setDeleteIntent(createDeleteIntent(deleteAction))

        val notificationManager = NotificationManagerCompat.from(mContext)
        notificationManager.cancel(notificationId)
        notificationManager.notify(notificationId, builder.build())
    }


    private fun createDeleteIntent(action: String): PendingIntent? {
        val intent = Intent(mContext, ClearReportBroadcastReciever::class.java)
        intent.action = action
        return PendingIntent.getBroadcast(
            mContext,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
    }

}