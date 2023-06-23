package ca.pkay.rcloneexplorer.notifications

import android.content.Context
import ca.pkay.rcloneexplorer.R

abstract class AbstractDualNotification(override var mContext: Context) : AbstractReportNotification(mContext) {


    abstract val mSuccessChannelID: String
    abstract val mSuccessChannelName: Int
    abstract val mSuccessChannelDescriptionID: Int
    abstract val OPERATION_SUCCESS_GROUP: String
    abstract val mNotificationIconSuccess: Int

    abstract val mFailureChannelID: String
    abstract val mFailureChannelName: Int
    abstract val mFailureChannelDescriptionID: Int
    abstract val OPERATION_FAILED_GROUP: String
    abstract val mNotificationIconFailure: Int

    abstract val mOngoingChannelID: String
    abstract val mOngoingChannelName: Int
    abstract val mOngoingChannelDescriptionID: Int
    abstract val mNotificationIconRunning: Int


    abstract val PERSISTENT_NOTIFICATION_ID_FOR_SYNC: Int



    init {
        setNotificationChannel(
            mSuccessChannelID,
            mSuccessChannelName,
            mSuccessChannelDescriptionID
        )

        setNotificationChannel(
            mFailureChannelID,
            mFailureChannelName,
            mFailureChannelDescriptionID
        )

        setNotificationChannel(
            mOngoingChannelID,
            mOngoingChannelName,
            mOngoingChannelDescriptionID
        )
    }



    fun showFailedNotificationOrReport(
        title: String,
        content: String?,
        notificationId: Int,
        taskid: Long
    ) {


        if(!useReports()){
            showFailedNotification(content, notificationId, taskid)
            return
        }
        if(mReportManager.getFailures()<=1) {
            showFailedNotification(content, notificationId, taskid)
            setLastNotification(notificationId)
            addToReport(title, content?: "")
        } else {
            mReportManager.cancelLastFailedNotification()
            mReportManager.showFailReport(title, content?: "")
        }
    }


    fun showSuccessNotificationOrReport(
        title: String,
        content: String?,
        notificationId: Int,
        taskid: Long
    ) {

        if(!useReports()){
            showSuccessNotification(title, content, notificationId)
            return
        }

        if(mReportManager.getSucesses()<=1) {
            showSuccessNotification(title, content, notificationId)
            mReportManager.lastSuccededNotification(notificationId)
            mReportManager.addToSuccessReport(title, content?: "")
        } else {
            mReportManager.cancelLastSuccededNotification()
            mReportManager.showSuccessReport(title, content?: "")
        }
    }




}