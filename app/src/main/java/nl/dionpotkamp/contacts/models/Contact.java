package nl.dionpotkamp.contacts.models;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import nl.dionpotkamp.contacts.migrations.ContactTable;

public class Contact extends Model {

    private String name;
    private String phoneNumber;
    private String email;
    private String address;

    /**
     * Creates a new Contact object with the given id.
     * Automatically calls get() to get the data from the database.
     */
    public Contact(int id) {
        this(id, "", "", "", "");
        if (id != -1)
            get();
    }

    /**
     * Creates a new Contact object with the given properties.
     * id is set to -1 initially and will be set by calling save().
     */
    public Contact(String name, String phoneNumber, String email, String address) {
        this(-1, name, phoneNumber, email, address);
    }

    /**
     * Creates a new Contact object with the given properties.
     *
     */
    public Contact(int id, String name, String phoneNumber, String email, String address) {
        super(ContactTable.TABLE_NAME, ContactTable.COLUMNS);

        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }


    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("name", name);
        values.put("phoneNumber", phoneNumber);
        values.put("email", email);
        values.put("address", address);

        return values;
    }

    @Override
    public void setValuesFromCursor(Cursor cursor) {
        if (cursor == null) return;
        if (cursor.getCount() == 0) return;
        if (cursor.getColumnCount() != dbColumns.length) return;
        if (cursor.isAfterLast()) return;
        if (cursor.isBeforeFirst()) cursor.moveToFirst();

        id = cursor.getInt(0);
        name = cursor.getString(1);
        phoneNumber = cursor.getString(2);
        email = cursor.getString(3);
        address = cursor.getString(4);
    }

    @NonNull
    @Override
    public Contact clone() {
        return new Contact(id, name, phoneNumber, email, address);
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("ID: %s Name: %s Phone: %s Email: %s Address: %s", id, name, phoneNumber, email, address.replace("\n", "\\n"));
    }
}
