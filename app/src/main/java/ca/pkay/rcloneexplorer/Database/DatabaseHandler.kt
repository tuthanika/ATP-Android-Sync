package ca.pkay.rcloneexplorer.Database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ca.pkay.rcloneexplorer.Items.Task

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DatabaseInfo.DATABASE_NAME, null, DatabaseInfo.DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(DatabaseInfo.SQL_CREATE_TABLES_TASKS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS task_table")
        onCreate(db)
    }

    fun taskFromCursor(cursor: Cursor): Task {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_ID))
        val task = Task(id)
        task.title = cursor.getString(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_TITLE))
        task.sourceRemoteId = cursor.getString(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_SOURCE_REMOTE_ID))
        task.sourceRemoteType = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_SOURCE_REMOTE_TYPE))
        task.sourcePath = cursor.getString(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_SOURCE_PATH))
        task.destRemoteId = cursor.getString(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_DEST_REMOTE_ID))
        task.destRemoteType = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_DEST_REMOTE_TYPE))
        task.destPath = cursor.getString(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_DEST_PATH))
        task.mode = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_MODE))
        task.md5sum = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_MD5SUM)) != 0
        task.wifionly = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_WIFI_ONLY)) != 0
        task.filterId = if (!cursor.isNull(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_FILTER_ID))) cursor.getLong(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_FILTER_ID)) else null
        task.deleteExcluded = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_DELETE_EXCLUDED)) != 0
        task.onFailFollowup = if (!cursor.isNull(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_ONFAIL_FOLLOWUP))) cursor.getLong(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_ONFAIL_FOLLOWUP)) else null
        task.onSuccessFollowup = if (!cursor.isNull(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_ONSUCCESS_FOLLOWUP))) cursor.getLong(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_ONSUCCESS_FOLLOWUP)) else null
        task.extraFlags = cursor.getString(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_EXTRA_FLAGS))
        return task
    }

    fun getTaskContentValues(task: Task): ContentValues {
        val values = ContentValues()
        values.put(Task.COLUMN_NAME_TITLE, task.title)
        values.put(Task.COLUMN_NAME_SOURCE_REMOTE_ID, task.sourceRemoteId)
        values.put(Task.COLUMN_NAME_SOURCE_REMOTE_TYPE, task.sourceRemoteType)
        values.put(Task.COLUMN_NAME_SOURCE_PATH, task.sourcePath)
        values.put(Task.COLUMN_NAME_DEST_REMOTE_ID, task.destRemoteId)
        values.put(Task.COLUMN_NAME_DEST_REMOTE_TYPE, task.destRemoteType)
        values.put(Task.COLUMN_NAME_DEST_PATH, task.destPath)
        values.put(Task.COLUMN_NAME_MODE, task.mode)
        values.put(Task.COLUMN_NAME_MD5SUM, if (task.md5sum) 1 else 0)
        values.put(Task.COLUMN_NAME_WIFI_ONLY, if (task.wifionly) 1 else 0)
        values.put(Task.COLUMN_NAME_FILTER_ID, task.filterId)
        values.put(Task.COLUMN_NAME_DELETE_EXCLUDED, if (task.deleteExcluded) 1 else 0)
        values.put(Task.COLUMN_NAME_ONFAIL_FOLLOWUP, task.onFailFollowup)
        values.put(Task.COLUMN_NAME_ONSUCCESS_FOLLOWUP, task.onSuccessFollowup)
        values.put(Task.COLUMN_NAME_EXTRA_FLAGS, task.extraFlags)
        return values
    }
}
