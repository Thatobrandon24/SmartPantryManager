package com.example.smart_pantry_manager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailsActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    private TextView recipeName;
    private TextView preparationTime;
    private TextView ingredientsText;
    private TextView instructionsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        recipeName = findViewById(R.id.txtDetailRecipeName);
        preparationTime = findViewById(R.id.txtDetailPreparationTime);
        ingredientsText = findViewById(R.id.txtDetailIngredients);
        instructionsText = findViewById(R.id.txtDetailInstructions);

        Button backButton = findViewById(R.id.btnBackToRecipes);

        databaseHelper = new DatabaseHelper(this);

        backButton.setOnClickListener(view -> finish());

        long recipeId = getIntent().getLongExtra("recipe_id", -1L);

        if (recipeId == -1L) {
            Toast.makeText(
                    this,
                    "No recipe was selected.",
                    Toast.LENGTH_SHORT
            ).show();
            finish();
            return;
        }

        loadRecipeDetails(recipeId);
    }

    private void loadRecipeDetails(long recipeId) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        try (Cursor cursor = db.query(
                DatabaseHelper.RECIPE_TABLE,
                new String[]{
                        "_id",
                        "recipe_name",
                        "instructions",
                        "preparation_minutes"
                },
                "_id = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                null
        )) {
            if (!cursor.moveToFirst()) {
                Toast.makeText(
                        this,
                        "Recipe not found.",
                        Toast.LENGTH_SHORT
                ).show();
                finish();
                return;
            }

            recipeName.setText(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("recipe_name")
                    )
            );

            int minutes = cursor.getInt(
                    cursor.getColumnIndexOrThrow("preparation_minutes")
            );

            preparationTime.setText(
                    "Preparation time: " + minutes + " minutes"
            );

            String instructions = cursor.getString(
                    cursor.getColumnIndexOrThrow("instructions")
            );

            instructionsText.setText(
                    instructions == null || instructions.trim().isEmpty()
                            ? "No instructions have been added yet."
                            : instructions
            );
        }

        loadRequiredIngredients(db, recipeId);
    }

    private void loadRequiredIngredients(
            SQLiteDatabase db, long recipeId) {

        StringBuilder ingredients = new StringBuilder();

        try (Cursor cursor = db.query(
                DatabaseHelper.RECIPE_INGREDIENTS_TABLE,
                new String[]{
                        "ingredient_name",
                        "required_qty",
                        "unit"
                },
                "recipe_id = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                "ingredient_name ASC"
        )) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("ingredient_name")
                );

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow("required_qty")
                );

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow("unit")
                );

                ingredients.append("• ")
                        .append(name)
                        .append(" — ")
                        .append(quantity)
                        .append(" ")
                        .append(unit == null ? "" : unit)
                        .append("\n");
            }
        }

        if (ingredients.length() == 0) {
            ingredientsText.setText(
                    "No ingredients have been added to this recipe."
            );
        } else {
            ingredientsText.setText(ingredients.toString().trim());
        }
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }

        super.onDestroy();
    }
}
