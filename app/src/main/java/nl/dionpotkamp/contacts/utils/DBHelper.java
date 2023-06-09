package nl.dionpotkamp.contacts.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Hashtable;

import nl.dionpotkamp.contacts.migrations.Migration;
import nl.dionpotkamp.contacts.migrations.ContactTable;

// adapted from https://www.geeksforgeeks.org/how-to-create-and-add-data-to-sqlite-database-in-android/
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "ContactsApp.sqlite";
    private static final int DB_VERSION = 1;

    Hashtable<Integer, Migration> migrations;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        migrations = new Hashtable<>();
        migrations.put(1, new ContactTable());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (int i = 1; i <= migrations.size(); i++) {
            migrations.get(i).up(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = 1; i <= migrations.size(); i++) {
            migrations.get(i).down(db);
        }

        onCreate(db);
    }
}
