package nl.dionpotkamp.contacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import nl.dionpotkamp.contacts.adapters.ContactAdapter;
import nl.dionpotkamp.contacts.enums.SortDirection;
import nl.dionpotkamp.contacts.models.Contact;
import nl.dionpotkamp.contacts.models.Model;
import nl.dionpotkamp.contacts.utils.DBControl;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView recyclerview;
    SwipeRefreshLayout swipeRefreshLayout;
    public static DBControl dbControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbControl = new DBControl(this);
        setContentView(R.layout.activity_main);

        recyclerview = findViewById(R.id.contact_list);
        swipeRefreshLayout = findViewById(R.id.contact_swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);

        // Add swipe to delete and update
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new UpdateDeleteSwipe(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT));
        itemTouchHelper.attachToRecyclerView(recyclerview);

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

        ContactAdapter adapter = new ContactAdapter(Model.getAll(Contact.class));

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

        if (!prefs.getBoolean("onboardingDone", false)) {
            startActivity(new Intent(this, OnboardingActivity.class));
        }
    }

    class UpdateDeleteSwipe extends ItemTouchHelper.SimpleCallback {
        public UpdateDeleteSwipe(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            ContactAdapter adapter = (ContactAdapter) recyclerview.getAdapter();

            if (adapter == null)
                return;

            Contact contact = adapter.getContactAt(position);
            if (direction == ItemTouchHelper.RIGHT) {
                if (adapter.removeItem(position)) {
                    Snackbar.make(recyclerview, "Contact deleted", Snackbar.LENGTH_LONG)
                            .setAction("Undo", v -> {
                                contact.save();
                                adapter.addItem(contact);
                                refreshList();
                            }).show();
                } else {
                    Toast.makeText(MainActivity.this, "Could not delete contact", Toast.LENGTH_LONG).show();
                }

                refreshList();
            } else if (direction == ItemTouchHelper.LEFT) {
                Intent intent = new Intent(MainActivity.this, ContactCreateUpdate.class);
                intent.putExtra("id", contact.getId());
                startActivity(intent);
            }
        }

        // adapted from https://stackoverflow.com/a/33344173/10463118
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                View itemView = viewHolder.itemView;

                Paint p = new Paint();

                // Swiping to the right (delete)
                if (dX > 0) {
                    // Set background color to red
                    p.setARGB(155, 255, 0, 0);
                    // Draw background
                    c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                            (float) itemView.getBottom(), p);
                    // Draw delete icon
                    c.drawBitmap(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_delete), (float) itemView.getLeft() + 20, (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - 100) / 2, null);
                }
                // Swiping to the left (update)
                else if (dX < 0) {
                    // Set background color to orange
                    p.setARGB(155, 255, 165, 0);
                    // Draw background
                    c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                            (float) itemView.getRight(), (float) itemView.getBottom(), p);
                    // Draw edit icon
                    c.drawBitmap(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_edit), (float) itemView.getRight() - 150, (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - 100) / 2, null);
                }

                // Fade out the view as it is swiped out of the parent's bounds
                final float alpha = 1.0f - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                viewHolder.itemView.setAlpha(alpha);
                viewHolder.itemView.setTranslationX(dX);
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }
    }
}