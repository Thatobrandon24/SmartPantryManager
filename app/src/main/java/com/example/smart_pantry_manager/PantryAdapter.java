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

        String details = "The Quantity: " + item.getQuantity()
                + " " + item.getUnit();

        if (item.getExpiryDate() != null
                && !item.getExpiryDate().trim().isEmpty()) {
            details += "\nExpires: " + item.getExpiryDate();
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

        TextView name;
        TextView details;
        Button editButton;
        Button deleteButton;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.txtIngredientName);
            details = itemView.findViewById(R.id.txtIngredientDetails);
            editButton = itemView.findViewById(R.id.btnEdit);
            deleteButton = itemView.findViewById(R.id.btnDelete);
        }
    }
}
