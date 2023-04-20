package nl.dionpotkamp.contacts;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.TextView;

import java.util.Objects;

import nl.dionpotkamp.contacts.databinding.ActivityContactDetailsBinding;
import nl.dionpotkamp.contacts.models.Contact;

public class ContactDetails extends AppCompatActivity {

    private ActivityContactDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityContactDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Contact contact = getContact();

        if (contact == null) {
            Snackbar.make(binding.getRoot(), "Contact not found", Snackbar.LENGTH_LONG).show();
            finish();
            return;
        }

        fillContactDetails(contact);

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(view -> {
            Intent editIntent = new Intent(this, ContactCreateUpdate.class);
            editIntent.putExtra("id", contact.getId());
            startActivity(editIntent);
        });
    }

    private Contact getContact() {
        int id = getIntent().getIntExtra("id", -1);
        if (id == -1) {
            return null;
        }

        Contact contact = new Contact(id);
        if (contact.getId() == -1) {
            return null;
        }

        return contact;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Contact contact = getContact();

        if (contact == null || contact.getId() == -1) {
            Snackbar.make(binding.getRoot(), "Contact not found", Snackbar.LENGTH_LONG).show();
            finish();
            return;
        }

        fillContactDetails(new Contact(getIntent().getIntExtra("id", -1)));
    }

    private void fillContactDetails(Contact contact) {
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        // Show the Up/Back button in the action bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(contact.getName());

        TextView email = findViewById(R.id.emailDetail);
        TextView phone = findViewById(R.id.phoneNumberDetail);
        TextView address = findViewById(R.id.addressDetail);

        email.setText(contact.getEmail());
        phone.setText(contact.getPhoneNumber());
        address.setText(contact.getAddress());

        TextView phoneLabel = findViewById(R.id.textViewPhoneDetail);
        TextView emailLabel = findViewById(R.id.textViewEmailDetail);
        TextView addressLabel = findViewById(R.id.textViewAddressDetail);
        boolean emailIsWebsite = contact.getEmail().startsWith("http");

        if (emailIsWebsite)
            emailLabel.setText(R.string.website);

        // Set click listeners to open the phone dialer, email app or maps app
        // Set long click listeners to copy the text to the clipboard
        phoneLabel.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(android.net.Uri.parse("tel:" + contact.getPhoneNumber()));
            startActivity(intent);
        });
        phoneLabel.setOnLongClickListener(view -> {
            copyToClipboard("Phone number", contact.getPhoneNumber());
            return true;
        });
        emailLabel.setOnClickListener(view -> {
            if (emailIsWebsite) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(android.net.Uri.parse(contact.getEmail()));
                startActivity(intent);
                return;
            }

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{contact.getEmail()});
            startActivity(Intent.createChooser(intent, "Send Email"));
        });
        emailLabel.setOnLongClickListener(view -> {
            if (emailIsWebsite) {
                copyToClipboard("Website", contact.getEmail());
                return true;
            }
            copyToClipboard("Email", contact.getEmail());
            return true;
        });
        addressLabel.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(android.net.Uri.parse("geo:0,0?q=" + contact.getAddress()));
            startActivity(intent);
        });
        addressLabel.setOnLongClickListener(view -> {
            copyToClipboard("Address", contact.getAddress());
            return true;
        });
    }

    /**
     * Copy text to clipboard
     *
     * @param label User-visible label for the clip data
     * @param text  Text to copy
     */
    private void copyToClipboard(String label, String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(android.content.Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
        Snackbar.make(binding.getRoot(), label + " copied to clipboard", Snackbar.LENGTH_LONG).show();
    }
}