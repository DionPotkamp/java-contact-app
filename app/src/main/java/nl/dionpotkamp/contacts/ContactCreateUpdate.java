package nl.dionpotkamp.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import nl.dionpotkamp.contacts.models.Contact;

public class ContactCreateUpdate extends AppCompatActivity {

    private TextView textName;
    private TextView textPhoneNumber;
    private TextView textEmail;
    private TextView textAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_create_update);

        populateViews();
        checkIfUpdateContact();

        findViewById(R.id.save_contact_button).setOnClickListener(view -> {
            if (!formIsValid()) return;

            Contact contact = new Contact(
                    getIntent().getIntExtra("id", -1),
                    textName.getText().toString(),
                    textPhoneNumber.getText().toString(),
                    textEmail.getText().toString(),
                    textAddress.getText().toString()
            ).save();

            if (contact.getId() != -1) {
                Toast.makeText(this, "Contact saved", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Could not save contact", Toast.LENGTH_SHORT).show();
            }

        });

        findViewById(R.id.back_button).setOnClickListener(view -> finish());
    }

    private void populateViews() {
        textName = findViewById(R.id.textName);
        textPhoneNumber = findViewById(R.id.textPhoneNumber);
        textEmail = findViewById(R.id.textEmail);
        textAddress = findViewById(R.id.textAddress);
    }

    private boolean formIsValid() {
        boolean valid = true;

        String name = textName.getText().toString();
        String phoneNumber = textPhoneNumber.getText().toString();
        String email = textEmail.getText().toString();
        String address = textAddress.getText().toString();

        if (name.isEmpty()) {
            textName.setError("Name is required");
            valid = false;
        } else {
            textName.setError(null);
        }
        if (phoneNumber.isEmpty()) {
            textPhoneNumber.setError("Phone number is required");
            valid = false;
        } else if (!phoneNumber.matches("^(\\+\\d{1,3}[- ]?)?\\d{10}$")) {
            textPhoneNumber.setError("Phone number is invalid");
            valid = false;
        } else {
            textName.setError(null);
        }
        if (!email.isEmpty() && !email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            textEmail.setError("Email is invalid");
            valid = false;
        } else {
            textName.setError(null);
        }

        return valid;
    }

    private void checkIfUpdateContact() {
        int id = getIntent().getIntExtra("id", -1);
        if (id == -1)
            return;

        Contact contact = new Contact(id);

        if (contact.getId() == -1) {
            Toast.makeText(this, "Contact not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        TextView title = findViewById(R.id.title);
        title.setText("Update contact");

        textName.setText(contact.getName());
        textPhoneNumber.setText(contact.getPhoneNumber());
        textEmail.setText(contact.getEmail());
        textAddress.setText(contact.getAddress());
    }
}