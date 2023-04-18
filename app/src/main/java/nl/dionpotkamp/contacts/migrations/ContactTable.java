package nl.dionpotkamp.contacts.migrations;

import android.database.sqlite.SQLiteDatabase;

public class ContactTable implements Migration {
    public static final String TABLE_NAME = "contacts";
    public static final String[] COLUMNS = new String[]{"id", "name", "phoneNumber", "email", "address"};
    @Override
    public void up(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE contacts (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phoneNumber TEXT, email TEXT, address TEXT)");
    }

    @Override
    public void down(SQLiteDatabase db) {
        db.execSQL("DROP TABLE contacts");
    }
}
