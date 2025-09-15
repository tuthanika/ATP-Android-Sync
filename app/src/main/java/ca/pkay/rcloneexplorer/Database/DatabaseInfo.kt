package ca.pkay.rcloneexplorer.Database

object DatabaseInfo {
    const val DATABASE_NAME = "rclone_explorer.db"
    const val DATABASE_VERSION = 2

    const val SQL_CREATE_TABLES_TASKS = """
        CREATE TABLE IF NOT EXISTS task_table (
            task_id INTEGER PRIMARY KEY AUTOINCREMENT,
            task_title TEXT,
            task_source_remote_id TEXT,
            task_source_remote_type INTEGER,
            task_source_path TEXT,
            task_dest_remote_id TEXT,
            task_dest_remote_type INTEGER,
            task_dest_path TEXT,
            task_mode INTEGER,
            task_use_md5sum INTEGER,
            task_use_only_wifi INTEGER,
            task_filter_id INTEGER,
            task_delete_excluded INTEGER,
            task_onFailFollowupTask INTEGER,
            task_onSuccessFollowupTask INTEGER,
            task_extra_flags TEXT
        );
    """
}
