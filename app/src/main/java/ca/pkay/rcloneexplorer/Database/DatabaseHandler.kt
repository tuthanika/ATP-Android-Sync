package ca.pkay.rcloneexplorer.Database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ca.pkay.rcloneexplorer.Items.Filter
import ca.pkay.rcloneexplorer.Items.Task

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "rcloneexplorer.db"
        const val DATABASE_VERSION = 1

        const val TABLE_TASKS = "tasks"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"

        // use source/dest instead of local/remote in DB schema
        const val COLUMN_SOURCE_REMOTE_ID = "source_remote_id"
        const val COLUMN_SOURCE_REMOTE_TYPE = "source_remote_type"
        const val COLUMN_SOURCE_PATH = "source_path"

        const val COLUMN_DEST_REMOTE_ID = "dest_remote_id"
        const val COLUMN_DEST_REMOTE_TYPE = "dest_remote_type"
        const val COLUMN_DEST_PATH = "dest_path"

        const val COLUMN_MODE = "mode"
        const val COLUMN_EXTRA_FLAGS = "extra_flags"
        const val COLUMN_WIFI_ONLY = "wifi_only"
        const val COLUMN_MD5SUM = "md5sum"
        const val COLUMN_DELETE_EXCLUDED = "delete_excluded"
        const val COLUMN_FILTER_ID = "filter_id"
        const val COLUMN_ONFAIL = "on_fail_followup"
        const val COLUMN_ONSUCCESS = "on_success_followup"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTasks = """
            CREATE TABLE IF NOT EXISTS $TABLE_TASKS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT,
                $COLUMN_SOURCE_REMOTE_ID TEXT,
                $COLUMN_SOURCE_REMOTE_TYPE INTEGER,
                $COLUMN_SOURCE_PATH TEXT,
                $COLUMN_DEST_REMOTE_ID TEXT,
                $COLUMN_DEST_REMOTE_TYPE INTEGER,
                $COLUMN_DEST_PATH TEXT,
                $COLUMN_MODE INTEGER,
                $COLUMN_EXTRA_FLAGS TEXT,
                $COLUMN_WIFI_ONLY INTEGER DEFAULT 0,
                $COLUMN_MD5SUM INTEGER DEFAULT 0,
                $COLUMN_DELETE_EXCLUDED INTEGER DEFAULT 0,
                $COLUMN_FILTER_ID INTEGER,
                $COLUMN_ONFAIL INTEGER DEFAULT -1,
                $COLUMN_ONSUCCESS INTEGER DEFAULT -1
            );
        """.trimIndent()
        db.execSQL(createTasks)

        val createFilters = """
            CREATE TABLE IF NOT EXISTS filters (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT,
                rules TEXT
            );
        """.trimIndent()
        db.execSQL(createFilters)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // For a clean schema install we drop and recreate. If you need migrations, implement here.
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        db.execSQL("DROP TABLE IF EXISTS filters")
        onCreate(db)
    }

    // Create a task
    fun createTask(task: Task): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, task.title)
            put(COLUMN_SOURCE_REMOTE_ID, task.remoteId) // map remoteId -> source_remote_id
            put(COLUMN_SOURCE_REMOTE_TYPE, task.remoteType)
            put(COLUMN_SOURCE_PATH, task.remotePath)
            put(COLUMN_DEST_REMOTE_ID, task.remoteId) // best-effort mapping to existing Task structure
            put(COLUMN_DEST_REMOTE_TYPE, task.remoteType)
            put(COLUMN_DEST_PATH, task.localPath)
            put(COLUMN_MODE, task.direction)
            put(COLUMN_EXTRA_FLAGS, "") // Task may not have property yet
            put(COLUMN_WIFI_ONLY, if (task.wifionly) 1 else 0)
            put(COLUMN_MD5SUM, if (task.md5sum) 1 else 0)
            put(COLUMN_DELETE_EXCLUDED, if (task.deleteExcluded) 1 else 0)
            put(COLUMN_FILTER_ID, task.filterId ?: -1)
            put(COLUMN_ONFAIL, task.onFailFollowup)
            put(COLUMN_ONSUCCESS, task.onSuccessFollowup)
        }
        val id = db.insert(TABLE_TASKS, null, values)
        db.close()
        return id
    }

    // Update a task
    fun updateTask(task: Task): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, task.title)
            put(COLUMN_SOURCE_REMOTE_ID, task.remoteId)
            put(COLUMN_SOURCE_REMOTE_TYPE, task.remoteType)
            put(COLUMN_SOURCE_PATH, task.remotePath)
            put(COLUMN_DEST_REMOTE_ID, task.remoteId)
            put(COLUMN_DEST_REMOTE_TYPE, task.remoteType)
            put(COLUMN_DEST_PATH, task.localPath)
            put(COLUMN_MODE, task.direction)
            put(COLUMN_EXTRA_FLAGS, "")
            put(COLUMN_WIFI_ONLY, if (task.wifionly) 1 else 0)
            put(COLUMN_MD5SUM, if (task.md5sum) 1 else 0)
            put(COLUMN_DELETE_EXCLUDED, if (task.deleteExcluded) 1 else 0)
            put(COLUMN_FILTER_ID, task.filterId ?: -1)
            put(COLUMN_ONFAIL, task.onFailFollowup)
            put(COLUMN_ONSUCCESS, task.onSuccessFollowup)
        }
        val rows = db.update(TABLE_TASKS, values, "$COLUMN_ID = ?", arrayOf(task.id.toString()))
        db.close()
        return rows
    }

    // Get task by id
    fun getTask(id: Long): Task? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TASKS,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        var task: Task? = null
        if (cursor != null && cursor.moveToFirst()) {
            task = Task(id)
            task.title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            task.remoteId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SOURCE_REMOTE_ID))
            task.remoteType = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SOURCE_REMOTE_TYPE))
            task.remotePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SOURCE_PATH))
            task.localPath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEST_PATH))
            task.direction = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MODE))
            task.wifionly = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WIFI_ONLY)) == 1
            task.md5sum = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MD5SUM)) == 1
            task.deleteExcluded = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DELETE_EXCLUDED)) == 1
            val filt = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FILTER_ID))
            task.filterId = if (filt >= 0) filt.toLong() else null
            task.onFailFollowup = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ONFAIL)).toLong()
            task.onSuccessFollowup = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ONSUCCESS)).toLong()
        }
        cursor?.close()
        db.close()
        return task
    }

    // All tasks (simple list)
    val allTasks: List<Task>
        get() {
            val list = mutableListOf<Task>()
            val db = readableDatabase
            val cursor = db.query(TABLE_TASKS, null, null, null, null, null, "$COLUMN_TITLE ASC")
            cursor.use {
                while (it.moveToNext()) {
                    val id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID))
                    val t = Task(id)
                    t.title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE))
                    list.add(t)
                }
            }
            db.close()
            return list
        }

    // Filters
    val allFilters: List<Filter>
        get() {
            val list = mutableListOf<Filter>()
            val db = readableDatabase
            val cursor = db.query("filters", null, null, null, null, null, "title ASC")
            cursor.use {
                while (it.moveToNext()) {
                    val id = it.getLong(it.getColumnIndexOrThrow("id"))
                    val title = it.getString(it.getColumnIndexOrThrow("title"))
                    val rules = it.getString(it.getColumnIndexOrThrow("rules"))
                    val filter = Filter(id)
                    filter.title = title
                    filter.rules = rules
                    list.add(filter)
                }
            }
            db.close()
            return list
        }

    fun deleteFilter(id: Long) {
        val db = writableDatabase
        db.delete("filters", "id = ?", arrayOf(id.toString()))
        db.close()
    }
}
