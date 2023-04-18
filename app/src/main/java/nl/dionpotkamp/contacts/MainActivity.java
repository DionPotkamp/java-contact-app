package nl.dionpotkamp.contacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import nl.dionpotkamp.contacts.adapters.ContactAdapter;
import nl.dionpotkamp.contacts.enums.SortDirection;
import nl.dionpotkamp.contacts.models.Contact;
import nl.dionpotkamp.contacts.utils.DBControl;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView recyclerview;
    SwipeRefreshLayout swipeRefreshLayout;
    public static DBControl dbControl;
    private boolean forceOnboarding = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbControl = new DBControl(this);
        setContentView(R.layout.activity_main);

        recyclerview = findViewById(R.id.contact_list);
        swipeRefreshLayout = findViewById(R.id.contact_swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);

        findViewById(R.id.add_contact_button).setOnClickListener(v ->
                startActivity(new Intent(this, ContactCreateUpdate.class)));
        findViewById(R.id.fab_sort_name).setOnClickListener(v -> {
            ContactAdapter.nameSort = ContactAdapter.nameSort == SortDirection.ASC ? SortDirection.DESC : SortDirection.ASC;
            refreshList();
        });

        findViewById(R.id.title)
            .setOnClickListener(v -> startActivity(new Intent(this, OnboardingActivity.class)));

        onboarding();
        setTitle();
    }

    @Override
    public void onRefresh() {
        refreshList();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void refreshList() {
        if (dbControl == null) {
            Toast.makeText(this, "Could not connect to database", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Refresh to retry", Toast.LENGTH_LONG).show();
            return;
        }

        ContactAdapter adapter = new ContactAdapter(new Contact(-1).getAll());

        recyclerview.setAdapter(adapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.scheduleLayoutAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences("contacts.sort", MODE_PRIVATE);
        ContactAdapter.nameSort = SortDirection.valueOf(prefs.getString("nameSort", "ASC"));

        refreshList();
        setTitle();
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = getSharedPreferences("contacts.sort", MODE_PRIVATE).edit();
        editor.putString("nameSort", ContactAdapter.nameSort.name());
        editor.apply();
    }

    private void setTitle() {
        SharedPreferences prefs = getSharedPreferences("contacts.onboarding", MODE_PRIVATE);
        String name = prefs.getString("name", "Your");

        if (!name.endsWith("s") && !name.equals("Your")) {
            name += "'s";
        } else if (name.endsWith("s") && !name.equals("Your")) {
            name += "'";
        }

        setTitle(name + " Contacts");
        ((TextView) findViewById(R.id.title))
                .setText(name + " Contacts");
    }

    private void onboarding() {
        SharedPreferences prefs = getSharedPreferences("contacts.onboarding", MODE_PRIVATE);
        System.out.println(prefs.getBoolean("onboardingDone", false));
        if (!prefs.getBoolean("onboardingDone", false) || forceOnboarding) {
            startActivity(new Intent(this, OnboardingActivity.class));
        }
    }

}