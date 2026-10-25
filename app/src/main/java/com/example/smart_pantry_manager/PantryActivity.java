package com.example.smart_pantry_manager;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PantryActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private PantryAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        databaseHelper = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerPantry);
        emptyMessage = findViewById(R.id.txtEmptyPantry);
        Button addButton = findViewById(R.id.btnAddIngredient);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PantryAdapter(new PantryAdapter.PantryListener() {
            @Override
            public void onEdit(PantryItem item) {
                Intent intent = new Intent(
                        PantryActivity.this,
                        IngredientFormActivity.class);

                intent.putExtra("ingredient_id", item.getId());
                startActivity(intent);
            }

            @Override
            public void onDelete(PantryItem item) {
                confirmDelete(item);
            }
        });

        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(
                    PantryActivity.this,
                    IngredientFormActivity.class);

            startActivity(intent);
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (databaseHelper != null && adapter != null) {
            loadPantryItems();
        }
    }

    private void loadPantryItems() {

        List<PantryItem> items = new ArrayList<>();

        try (Cursor cursor = databaseHelper.getAllPantryItems()) {
            if (cursor != null) {
                while (cursor.moveToNext()) {

                    long id = cursor.getLong(
                            cursor.getColumnIndexOrThrow("_id"));

                    String name = cursor.getString(
                            cursor.getColumnIndexOrThrow("ingredient_name"));

                    double quantity = cursor.getDouble(
                            cursor.getColumnIndexOrThrow("quantity"));

                    String unit = cursor.getString(
                            cursor.getColumnIndexOrThrow("unit"));

                    String expiry = cursor.getString(
                            cursor.getColumnIndexOrThrow("expiration_date"));

                    items.add(new PantryItem(
                            id, name, quantity, unit, expiry));
                }
            }
        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Unable to load pantry items: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
            return;
        }


        adapter.setItems(items);

        boolean isEmpty = items.isEmpty();

        emptyMessage.setVisibility(
                isEmpty ? View.VISIBLE : View.GONE);

        recyclerView.setVisibility(
                isEmpty ? View.GONE : View.VISIBLE);
    }

    private void confirmDelete(PantryItem item) {

        new AlertDialog.Builder(this)
                .setTitle("Delete the ingredient")
                .setMessage("Remove " + item.getName()
                        + " from your pantry list?")
                .setPositiveButton("Delete",
                        (DialogInterface dialog, int which) -> {

                            int rowsDeleted =
                                    databaseHelper.deletePantryItem(
                                            item.getId());

                            if (rowsDeleted > 0) {
                                Toast.makeText(
                                        this,
                                        "Ingredient has been deleted",
                                        Toast.LENGTH_SHORT).show();

                                loadPantryItems();
                            } else {
                                Toast.makeText(
                                        this,
                                        "It could not delete ingredient",
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
            databaseHelper = null;
        }
        super.onDestroy();
    }
}
