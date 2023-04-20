package nl.dionpotkamp.contacts.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Comparator;
import java.util.List;

import nl.dionpotkamp.contacts.ContactDetails;
import nl.dionpotkamp.contacts.R;
import nl.dionpotkamp.contacts.enums.SortDirection;
import nl.dionpotkamp.contacts.models.Contact;

/**
 * The adapter class for the RecyclerView, contains the data to render and update.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder> {
    private final List<Contact> contacts;
    public static SortDirection nameSort = SortDirection.ASC;

    public ContactAdapter(List<Contact> contacts) {
        this.contacts = contacts;
        sortByName(nameSort);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    // get the contact at the specified position
    public Contact getContactAt(int position) {
        return contacts.get(position);
    }

    /**
     * The data to render and update.
     */
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout. Inflating a layout means to render the layout as a View object.
        View contactView = inflater.inflate(R.layout.contact_list_item, parent, false);

        // Return a new holder instance
        return new ContactViewHolder(contactView);
    }

    /**
     * The ViewHolder describes an item view and data about its place within the RecyclerView.
     * Here is the data set to the view.
     */
    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        // Get the data model based on position & make sure we have the latest data
        Contact c = contacts.get(position);
        Contact contact = new Contact(c.getId());
        contacts.set(position, contact);

        holder.name.setText(contact.getName());
        holder.email.setText(contact.getEmail());
        holder.phoneNumber.setText(contact.getPhoneNumber());
        holder.address.setText(contact.getAddress());

        holder.rootLayout.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ContactDetails.class);
            intent.putExtra("id", contact.getId());
            v.getContext().startActivity(intent);
        });
    }

    public boolean removeItem(int position) {
        Contact contact = contacts.get(position);

        if (!contact.delete())
            return false;

        contacts.remove(position);
        notifyItemRemoved(position);

        return true;
    }

    public void addItem(Contact contact) {
        contacts.add(contact);
        notifyItemInserted(contacts.size() - 1);
    }

    public void sortByName(SortDirection direction) {
        Comparator<Contact> comparator = Comparator.comparing(Contact::getName);
        if (direction == SortDirection.DESC) {
            comparator = comparator.reversed();
        }
        contacts.sort(comparator);
        notifyDataSetChanged();
    }
}
