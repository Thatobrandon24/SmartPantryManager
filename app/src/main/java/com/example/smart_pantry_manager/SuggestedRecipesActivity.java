package com.example.smart_pantry_manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private RecipeMatcher recipeMatcher;

    private RecyclerView rvSuggestedRecipes;
    private TextView txtNoRecipes;

    private final List<RecipeMatcher.RecipeResult> recipes =
            new ArrayList<>();

    private RecipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        databaseHelper = new DatabaseHelper(this);
        recipeMatcher = new RecipeMatcher(databaseHelper);

        rvSuggestedRecipes = findViewById(R.id.rvSuggestedRecipes);
        txtNoRecipes = findViewById(R.id.txtNoRecipes);

        Button btnRefreshRecipes =
                findViewById(R.id.btnRefreshRecipes);

        Button btnBackToPantry =
                findViewById(R.id.btnBackToPantry);

        rvSuggestedRecipes.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new RecipeAdapter(recipes);
        rvSuggestedRecipes.setAdapter(adapter);

        btnRefreshRecipes.setOnClickListener(
                view -> loadSuggestedRecipes()
        );

        btnBackToPantry.setOnClickListener(view -> {
            Intent intent = new Intent(
                    SuggestedRecipesActivity.this,
                    PantryActivity.class
            );
            startActivity(intent);
            finish();
        });

        loadSuggestedRecipes();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (adapter != null) {
            loadSuggestedRecipes();
        }
    }

    private void loadSuggestedRecipes() {
        recipes.clear();

        recipes.addAll(recipeMatcher.getStrictMatches());

        adapter.notifyDataSetChanged();

        boolean noMatches = recipes.isEmpty();

        txtNoRecipes.setVisibility(
                noMatches ? View.VISIBLE : View.GONE
        );

        rvSuggestedRecipes.setVisibility(
                noMatches ? View.GONE : View.VISIBLE
        );
    }

    private static class RecipeAdapter
            extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

        private final List<RecipeMatcher.RecipeResult> items;

        RecipeAdapter(List<RecipeMatcher.RecipeResult> items) {
            this.items = items;
        }

        @Override
        public RecipeViewHolder onCreateViewHolder(
                ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(
                            R.layout.item_recipe,
                            parent,
                            false
                    );

            return new RecipeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(
                RecipeViewHolder holder, int position) {

            RecipeMatcher.RecipeResult recipe = items.get(position);

            holder.txtRecipeName.setText(recipe.name);

            holder.txtPreparationTime.setText(
                    "Preparation time: "
                            + recipe.preparationMinutes
                            + " minutes"
            );

            holder.txtRecipeInstructions.setText(
                    recipe.instructions
            );
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class RecipeViewHolder extends RecyclerView.ViewHolder {

            TextView txtRecipeName;
            TextView txtPreparationTime;
            TextView txtRecipeInstructions;

            RecipeViewHolder(View itemView) {
                super(itemView);

                txtRecipeName =
                        itemView.findViewById(R.id.txtRecipeName);

                txtPreparationTime =
                        itemView.findViewById(R.id.txtPreparationTime);

                txtRecipeInstructions =
                        itemView.findViewById(R.id.txtRecipeInstructions);
            }
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
