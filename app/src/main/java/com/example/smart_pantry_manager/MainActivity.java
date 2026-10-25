package com.example.smart_pantry_manager;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button btnPantry = findViewById(R.id.btnPantry);
        Button btnAddIngredient = findViewById(R.id.btnAddIngredient);
        Button btnRecipes = findViewById(R.id.btnRecipes);
        Button btnRecipeDetails = findViewById(R.id.btnRecipeDetails);
        Button btnSettings = findViewById(R.id.btnSettings);

        btnPantry.setOnClickListener(view -> {
            showMessage("Pantry Screen is Next");
            Intent intent = new Intent(
                    MainActivity.this,
                    PantryActivity.class
            );
            startActivity(intent);

        });

        btnAddIngredient.setOnClickListener(view -> {
            showMessage("Add Ingredient screen is next");
            Intent intent = new Intent(
                    MainActivity.this,
                    IngredientFormActivity.class

            );
            startActivity(intent);

        });

        btnRecipes.setOnClickListener(view -> {
            showMessage("Recipe suggestions are next");
            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );
            startActivity(intent);
        });

        btnRecipeDetails.setOnClickListener(view ->
                showMessage("Recipe details screen is next"));

        btnSettings.setOnClickListener(v ->
                showMessage("Settings screen is next"));
    }

    private void showMessage(String message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }
}