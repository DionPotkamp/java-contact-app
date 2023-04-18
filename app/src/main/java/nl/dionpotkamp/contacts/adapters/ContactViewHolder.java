package nl.dionpotkamp.contacts.adapters;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import nl.dionpotkamp.contacts.R;

// Provide a direct reference to each of the views within a data item
// Used to cache the views within the item layout for fast access
public class ContactViewHolder extends RecyclerView.ViewHolder {
    // a variable for any view that will be used to render a row
    public TextView name;
    public TextView email;
    public TextView phoneNumber;
    public TextView address;

    public LinearLayout rootLayout;

    // Entire item row, and does the view lookups to find subview
    public ContactViewHolder(View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.nameListDetail);
        email = itemView.findViewById(R.id.emailListDetail);
        phoneNumber = itemView.findViewById(R.id.phoneNumberListDetail);
        address = itemView.findViewById(R.id.addressListDetail);
        rootLayout = itemView.findViewById(R.id.root_layout);
    }
}
