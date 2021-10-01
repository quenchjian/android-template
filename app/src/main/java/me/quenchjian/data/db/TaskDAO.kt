package me.quenchjian.data.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import me.quenchjian.data.TaskRepository
import me.quenchjian.model.Task
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskDAO @Inject constructor(private val dbHelper: TodoDBHelper) : TaskRepository {

  companion object {
    const val TABLE_NAME = "Tasks"
    const val COL_ID = "id"
    const val COL_TITLE = "title"
    const val COL_DESC = "description"
    const val COL_COMPLETE = "complete"
    const val CREATE_SQL = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
        "$COL_ID TEXT PRIMARY KEY, " +
        "$COL_TITLE TEXT, " +
        "$COL_DESC TEXT, " +
        "$COL_COMPLETE INTEGER);"
  }

  override suspend fun load(id: String): Task? {
    val sql = "SELECT * FROM $TABLE_NAME WHERE $COL_ID = ?"
    return dbHelper.readableDatabase.rawQuery(sql, arrayOf(id)).use { cursor ->
      if (cursor.moveToNext()) cursor.toTask() else null
    }
  }

  override suspend fun load(): List<Task> {
    val sql = "SELECT * FROM $TABLE_NAME"
    return dbHelper.readableDatabase.rawQuery(sql, null).use { cursor ->
      val tasks = mutableListOf<Task>()
      while (cursor.moveToNext()) {
        tasks.add(cursor.toTask())
      }
      tasks.toList()
    }
  }

  override suspend fun save(task: Task) {
    val db = dbHelper.writableDatabase
    try {
      db.beginTransaction()
      db.insertOrThrow(TABLE_NAME, null, task.toContentValue())
      db.setTransactionSuccessful()
    } finally {
      db.endTransaction()
    }
  }

  override suspend fun saveOrUpdate(task: Task) {
    val db = dbHelper.writableDatabase
    try {
      db.beginTransaction()
      val result = db.insertWithOnConflict(
        TABLE_NAME,
        null,
        task.toContentValue(),
        SQLiteDatabase.CONFLICT_IGNORE
      )
      if (result == -1L) {
        db.update(TABLE_NAME, task.toContentValue(), "$COL_ID = ?", arrayOf(task.id))
      }
      db.setTransactionSuccessful()
    } finally {
      db.endTransaction()
    }
  }

  override suspend fun delete(id: String) {
    val db = dbHelper.writableDatabase
    try {
      db.beginTransaction()
      db.delete(TABLE_NAME, "$COL_ID = ?", arrayOf(id))
      db.setTransactionSuccessful()
    } finally {
      db.endTransaction()
    }
  }

  override suspend fun delete(taskIds: List<String>) {
    val db = dbHelper.writableDatabase
    try {
      val args = List(taskIds.size) { "?" }.joinToString()
      db.beginTransaction()
      db.execSQL("DELETE FROM $TABLE_NAME WHERE $COL_ID IN ($args)", taskIds.toTypedArray())
      db.setTransactionSuccessful()
    } finally {
      db.endTransaction()
    }
  }

  override suspend fun deleteCompleted() {
    val db = dbHelper.writableDatabase
    try {
      db.beginTransaction()
      db.execSQL("DELETE FROM $TABLE_NAME WHERE $COL_COMPLETE = 1")
      db.setTransactionSuccessful()
    } finally {
      db.endTransaction()
    }
  }

  override suspend fun sync(tasks: List<Task>, clear: Boolean) {
    val db = dbHelper.writableDatabase
    val stmt = db.compileStatement("INSERT OR REPLACE INTO $TABLE_NAME VALUES (?,?,?,?)")
    try {
      db.beginTransaction()
      if (clear) {
        db.execSQL("DELETE FROM $TABLE_NAME;")
      }
      for (task in tasks) {
        stmt.clearBindings()
        stmt.bindString(1, task.id)
        stmt.bindString(2, task.title)
        stmt.bindString(3, task.description)
        stmt.bindLong(4, task.isCompleted.toLong())
        stmt.execute()
      }
      db.setTransactionSuccessful()
    } finally {
      db.endTransaction()
    }
  }

  @SuppressLint("Range")
  private fun Cursor.toTask(): Task {
    return Task(
      getString(getColumnIndex(COL_ID)),
      getString(getColumnIndex(COL_TITLE)),
      getString(getColumnIndex(COL_DESC)),
      getInt(getColumnIndex(COL_COMPLETE)) == 1
    )
  }

  private fun Task.toContentValue(): ContentValues {
    return ContentValues().apply {
      put(COL_ID, id)
      put(COL_TITLE, title)
      put(COL_DESC, description)
      put(COL_COMPLETE, isCompleted.toLong())
    }
  }

  private fun Boolean.toLong(): Long = if (this) 1 else 0
}
