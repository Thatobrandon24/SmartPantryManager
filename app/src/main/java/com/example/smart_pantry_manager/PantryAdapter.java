package com.example.smart_pantry_manager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PantryAdapter
        extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface PantryListener {
        void onEdit(PantryItem item);

        void onDelete(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final PantryListener listener;

    public PantryAdapter(PantryListener listener) {
        this.listener = listener;
    }

    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);

        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PantryViewHolder holder, int position) {

        PantryItem item = items.get(position);

        holder.name.setText(item.getName());

        String quantity = String.format(
                Locale.getDefault(),
                "%s",
                item.getQuantity()
        );

        // Display whole numbers without unnecessary decimal places.
        if (item.getQuantity() == Math.floor(item.getQuantity())) {
            quantity = String.format(
                    Locale.getDefault(),
                    "%.0f",
                    item.getQuantity()
            );
        }

        String details = "Quantity: " + quantity + " " + item.getUnit();

        String expiry = item.getExpiryDate();

        if (expiry != null && !expiry.trim().isEmpty()) {
            details += "\nExpires: " + expiry;
        }

        holder.details.setText(details);

        holder.editButton.setOnClickListener(
                view -> listener.onEdit(item));

        holder.deleteButton.setOnClickListener(
                view -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {

        final TextView name;
        final TextView details;
        final Button editButton;
        final Button deleteButton;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.txtIngredientName);
            details = itemView.findViewById(R.id.txtIngredientDetails);
            editButton = itemView.findViewById(R.id.btnEdit);
            deleteButton = itemView.findViewById(R.id.btnDelete);
        }
    }
}
