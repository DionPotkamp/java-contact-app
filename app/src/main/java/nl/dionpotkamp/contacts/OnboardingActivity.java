package nl.dionpotkamp.contacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import nl.dionpotkamp.contacts.models.Contact;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        Button doneButton = findViewById(R.id.buttonDone);
        TextView name = findViewById(R.id.editTextName);
        SwitchCompat addDemo = findViewById(R.id.switchDemoContact);

        doneButton.setOnClickListener(v -> {
            SharedPreferences.Editor preferences = getSharedPreferences("contacts.onboarding", MODE_PRIVATE).edit();
            preferences.putBoolean("onboardingDone", true);
            preferences.putString("name", name.getText().toString());
            preferences.apply();

            if (addDemo.isChecked()) {
                new Contact("John Doe", "0612345678", "", "")
                        .save();
                new Contact("Widener University", "+16104994087", "http://widener.edu/", "Old Main, University Pl, Chester, PA 19013")
                        .save();
                new Contact("Dion Potkamp", "+31600000000", "dion@example.com", "Beekberg 777, 7777DD, Eindhoven, The Netherlands")
                        .save();
                new Contact("Google", "+1 650-253-0000", "google@google.com", "1600 Amphitheatre Pkwy, Mountain View, CA 94043, USA")
                        .save();
                new Contact("Facebook", "+1 650-543-4800", "", "1 Hacker Way, Menlo Park, CA 94025, USA")
                        .save();
            }
            finish();
        });
    }
}