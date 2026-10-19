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
        Button btnSettings = findViewById(R.id.btnSettings);

        btnPantry.setOnClickListener(v ->
                showMessage("Pantry Screen is Next"));

        btnAddIngredient.setOnClickListener(v ->
                showMessage("Add Ingredient screen is next"));

        btnRecipes.setOnClickListener(v ->
                showMessage("Recipe suggestions are next"));

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