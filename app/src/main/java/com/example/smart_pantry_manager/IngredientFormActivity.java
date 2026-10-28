package com.example.smart_pantry_manager;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class IngredientFormActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;

    private EditText edtIngredientName;
    private EditText edtQuantity;
    private EditText edtUnit;
    private TextView txtExpiryDate;
    private TextView txtFormTitle;
    private Button btnSaveIngredient;

    private long ingredientId = -1L;
    private String expiryDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_form);

        databaseHelper = new DatabaseHelper(this);

        edtIngredientName = findViewById(R.id.edtIngredientName);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtUnit = findViewById(R.id.edtUnit);
        txtExpiryDate = findViewById(R.id.txtExpiryDate);
        txtFormTitle = findViewById(R.id.txtFormTitle);
        btnSaveIngredient = findViewById(R.id.btnSaveIngredient);

        Button btnChooseExpiry = findViewById(R.id.btnChooseExpiry);
        Button btnClearExpiry = findViewById(R.id.btnClearExpiry);
        Button btnCancelIngredient = findViewById(R.id.btnCancelIngredient);

        ingredientId = getIntent().getLongExtra("ingredient_id", -1L);

        if (ingredientId != -1L) {
            txtFormTitle.setText("Edit Ingredient");
            btnSaveIngredient.setText("Update Ingredient");
            loadIngredient();
        }

        btnChooseExpiry.setOnClickListener(view -> showDatePicker());

        btnClearExpiry.setOnClickListener(view -> {
            expiryDate = null;
            txtExpiryDate.setText("No expiry date selected");
        });

        btnSaveIngredient.setOnClickListener(view -> saveIngredient());

        btnCancelIngredient.setOnClickListener(view -> finish());
    }

    private void loadIngredient() {
        try (Cursor cursor = databaseHelper.getPantryItem(ingredientId)) {
            if (cursor != null && cursor.moveToFirst()) {
                edtIngredientName.setText(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("ingredient_name")));

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow("quantity"));

                edtQuantity.setText(
                        String.valueOf(quantity));

                edtUnit.setText(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("unit")));

                expiryDate = cursor.getString(
                        cursor.getColumnIndexOrThrow("expiration_date"));

                if (expiryDate == null || expiryDate.trim().isEmpty()) {
                    expiryDate = null;
                    txtExpiryDate.setText("No expiry date selected");
                } else {
                    txtExpiryDate.setText(expiryDate);
                }
            } else {
                Toast.makeText(
                        this,
                        "Ingredient could not be found.",
                        Toast.LENGTH_LONG
                ).show();
                finish();
            }
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        if (expiryDate != null && !expiryDate.isEmpty()) {
            try {
                String[] parts = expiryDate.split("-");
                calendar.set(
                        Integer.parseInt(parts[0]),
                        Integer.parseInt(parts[1]) - 1,
                        Integer.parseInt(parts[2]));
            } catch (Exception ignored) {
                // Use today's date if the stored date cannot be parsed.
            }
        }

        DatePickerDialog picker = new DatePickerDialog(
                this,
                (datePicker, year, month, day) -> {
                    expiryDate = String.format(
                            Locale.US,
                            "%04d-%02d-%02d",
                            year, month + 1, day);

                    txtExpiryDate.setText(expiryDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        picker.show();
    }

    // Validates the form and adds or updates the ingredient in the SQLite database
    private void saveIngredient() {
        String name = edtIngredientName.getText().toString().trim();
        String quantityText = edtQuantity.getText().toString().trim();
        String unit = edtUnit.getText().toString().trim();

        // Ensure that a value or id for the ingredient has been entered
        if (name.isEmpty()) {
            edtIngredientName.setError("Enter the ingredient name");
            edtIngredientName.requestFocus();
            return;
        }

        // Ensure that the quantity field is not empty.
        if (quantityText.isEmpty()) {
            edtQuantity.setError("Enter the quantity");
            edtQuantity.requestFocus();
            return;
        }

        // Ensure the measurement unit has been entered for the ingredient
        if (unit.isEmpty()) {
            edtUnit.setError("Enter the unit");
            edtUnit.requestFocus();
            return;
        }

        double quantity;

        // Convert the quantity text into a numeric value.
        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException exception) {
            // Display an error if the user does not enter number
            edtQuantity.setError("Enter a valid number");
            edtQuantity.requestFocus();
            return;
        }

        if (Double.isNaN(quantity)
                || Double.isInfinite(quantity)
                || quantity <= 0) {
            edtQuantity.setError("Quantity must be greater than zero");
            edtQuantity.requestFocus();
            return;
        }

        long result;

        // Insert a new record if no ingredient ID exists.
        if (ingredientId == -1L) {
            result = databaseHelper.addPantryItem(
                    name, quantity, unit, expiryDate);
        } else {
            result = databaseHelper.updatePantryItem(
                    ingredientId, name, quantity, unit, expiryDate);
        }

        if (result > 0) {
            Toast.makeText(
                    this,
                    ingredientId == -1L
                            ? "Ingredient saved."
                            : "Ingredient updated.",
                    Toast.LENGTH_SHORT
            ).show();

            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(
                    this,
                    "Could not save the ingredient. Check DatabaseHelper.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // it closes the database helper when the activity is closed down
    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}
