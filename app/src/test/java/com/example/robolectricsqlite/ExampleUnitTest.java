package com.example.robolectricsqlite;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.VisibleForTesting;
import androidx.test.core.app.ApplicationProvider;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ExampleUnitTest {

  static class OpenHelper extends SQLiteOpenHelper {

    private boolean configured;
    private final Context context;
    private final String databaseName;

    OpenHelper(Context context, String databaseName) {
      super(context, databaseName, null, 1);
      this.context = context;
      this.databaseName = databaseName;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
      // Note that this is only called automatically by the SQLiteOpenHelper base class on Jelly
      // Bean and above.
      configured = true;
      Cursor cursor = db.rawQuery("PRAGMA locking_mode = EXCLUSIVE", new String[0]);
      cursor.close();
    }

    private void ensureConfigured(SQLiteDatabase db) {
      if (!configured) {
        onConfigure(db);
      }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      ensureConfigured(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      ensureConfigured(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      ensureConfigured(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
      ensureConfigured(db);
    }
  }

  @Test
  public void canRestartSqlite() {
    OpenHelper opener = new OpenHelper(ApplicationProvider.getApplicationContext(), "Testing-DB");
    SQLiteDatabase db = opener.getWritableDatabase();
    Cursor cursor = db.rawQuery("PRAGMA locking_mode = EXCLUSIVE", new String[0]);
    cursor.close();
    db.close();

    SQLiteDatabase db1 = opener.getWritableDatabase();
    cursor = db1.rawQuery("PRAGMA locking_mode = EXCLUSIVE", new String[0]);
    cursor.close();
    db1.close();
  }
}