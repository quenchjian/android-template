package me.quenchjian.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class TodoDBHelper @Inject constructor(
  @ApplicationContext context: Context,
  @Named("DatabaseName") name: String
) : SQLiteOpenHelper(context, name, null, 1) {

  override fun onCreate(db: SQLiteDatabase) {
    db.execSQL(TaskDAO.CREATE_SQL)
  }

  override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
  }
}
