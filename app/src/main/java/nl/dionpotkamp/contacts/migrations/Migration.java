package nl.dionpotkamp.contacts.migrations;

import android.database.sqlite.SQLiteDatabase;

public interface Migration {
    void up(SQLiteDatabase db);
    void down(SQLiteDatabase db);
}
