package ca.pkay.rcloneexplorer.notifications

import android.content.Context
import androidx.core.app.NotificationCompat
import ca.pkay.rcloneexplorer.BroadcastReceivers.DownloadCancelAction
import ca.pkay.rcloneexplorer.BroadcastReceivers.SyncCancelAction
import ca.pkay.rcloneexplorer.R
import ca.pkay.rcloneexplorer.Services.SyncService

class DownloadNotifications(override var mContext: Context) : AbstractSyncNotification(mContext) {


    companion object {
        private const val DOWNLOAD_FINISHED_GROUP = "ca.pkay.rcexplorer.DOWNLOAD_FINISHED_GROUP"
        private const val DOWNLOAD_FAILED_GROUP = "ca.pkay.rcexplorer.DOWNLOAD_FAILED_GROUP"
        const val PERSISTENT_NOTIFICATION_ID = 167
        private const val FAILED_DOWNLOAD_NOTIFICATION_ID = 138
        private const val DOWNLOAD_FINISHED_NOTIFICATION_ID = 80
        private const val CONNECTIVITY_CHANGE_NOTIFICATION_ID = 235
    }


    override val mChannelID = "ca.pkay.rcexplorer.DOWNLOAD_CHANNEL"
    override val mChannelName = "Downloads"
    override val mChannelDescriptionID = R.string.download_service_notification_description

    override val mNotificationIconRunning = R.drawable.ic_twotone_cloud_download_24
    override val mNotificationIconSuccess = R.drawable.ic_twotone_cloud_done_24
    override val mNotificationIconFailure = R.drawable.ic_twotone_cloud_error_24


    /**
     * Create initial Notification to be build for the service
     */
    fun createDownloadNotification(
        title: String?,
        bigTextArray: java.util.ArrayList<String?>?
    ): NotificationCompat.Builder? {
        return updateNotification(
            title,
            title,
            bigTextArray!!,
            0,
            SyncService::class.java,
            SyncCancelAction::class.java,
            mChannelID
        )
    }

    /**
     * Update Service notification
     */
    fun updateDownloadNotification(
        title: String?,
        content: String?,
        bigTextArray: java.util.ArrayList<String?>?,
        percent: Int
    ) {
        var builder = updateNotification(
            title,
            content,
            bigTextArray!!,
            percent,
            SyncService::class.java,
            DownloadCancelAction::class.java,
            mChannelID
        )
        show(builder, PERSISTENT_NOTIFICATION_ID)
    }


    fun showConnectivityChangedNotification() {
        val builder = NotificationCompat.Builder(mContext, mChannelID)
            .setSmallIcon(R.drawable.ic_twotone_cloud_error_24)
            .setContentTitle(mContext.getString(R.string.download_cancelled))
            .setContentText(mContext.getString(R.string.wifi_connections_isnt_available))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        show(builder, CONNECTIVITY_CHANGE_NOTIFICATION_ID)
    }

    fun showDownloadFinishedNotification(notificationID: Int, contentText: String) {
        createSummaryNotificationForFinished()
        val builder = NotificationCompat.Builder(mContext, mChannelID)
            .setSmallIcon(R.drawable.ic_twotone_cloud_done_24)
            .setContentTitle(mContext.getString(R.string.download_complete))
            .setContentText(contentText)
            .setGroup(DOWNLOAD_FINISHED_GROUP)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
        show(builder, notificationID)
    }

    fun createSummaryNotificationForFinished() {
        val builder = NotificationCompat.Builder(mContext, mChannelID)
            .setContentTitle(mContext.getString(R.string.download_complete)) //set content text to support devices running API level < 24
            .setContentText(mContext.getString(R.string.download_complete))
            .setSmallIcon(R.drawable.ic_twotone_cloud_done_24)
            .setGroup(DOWNLOAD_FINISHED_GROUP)
            .setGroupSummary(true)
            .setAutoCancel(true)
        show(builder, DOWNLOAD_FINISHED_NOTIFICATION_ID)
    }

    fun showDownloadFailedNotification(notificationId: Int, contentText: String) {
        createSummaryNotificationForFailed()
        val builder = NotificationCompat.Builder(mContext, mChannelID)
            .setSmallIcon(R.drawable.ic_twotone_cloud_error_24)
            .setContentTitle(mContext.getString(R.string.download_failed))
            .setContentText(contentText)
            .setGroup(DOWNLOAD_FAILED_GROUP)
            .setPriority(NotificationCompat.PRIORITY_LOW)
        show(builder, notificationId)
    }

    fun createSummaryNotificationForFailed() {
        val summaryNotification = NotificationCompat.Builder(mContext, mChannelID)
            .setContentTitle(mContext.getString(R.string.download_failed)) //set content text to support devices running API level < 24
            .setContentText(mContext.getString(R.string.download_failed))
            .setSmallIcon(R.drawable.ic_twotone_cloud_error_24)
            .setGroup(DOWNLOAD_FAILED_GROUP)
            .setGroupSummary(true)
            .setAutoCancel(true)
        show(summaryNotification, FAILED_DOWNLOAD_NOTIFICATION_ID)
    }
}