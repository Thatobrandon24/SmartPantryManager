package com.example.smart_pantry_manager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecipeMatcher {

    private final DatabaseHelper databaseHelper;

    public RecipeMatcher(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public static class RecipeResult {
        public final long id;
        public final String name;
        public final String instructions;
        public final int preparationMinutes;

        public RecipeResult(
                long id,
                String name,
                String instructions,
                int preparationMinutes) {
            this.id = id;
            this.name = name;
            this.instructions = instructions;
            this.preparationMinutes = preparationMinutes;
        }
    }

    private static class Requirement {
        String name;
        double quantity;
        String unit;

        Requirement(String name, double quantity, String unit) {
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
        }
    }

    public List<RecipeResult> getStrictMatches() {

        List<RecipeResult> matches = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        // Aggregate quantities for equivalent ingredient names and units.
        Map<String, Map<String, Double>> pantry = loadPantry(db);

        Cursor recipes = db.query(
                DatabaseHelper.RECIPE_TABLE,
                null, null, null, null, null, "recipe_name ASC"
        );

        try {
            while (recipes.moveToNext()) {
                long recipeId = recipes.getLong(
                        recipes.getColumnIndexOrThrow("_id"));

                String name = recipes.getString(
                        recipes.getColumnIndexOrThrow("recipe_name"));

                String instructions = recipes.getString(
                        recipes.getColumnIndexOrThrow("instructions"));

                int minutes = recipes.getInt(
                        recipes.getColumnIndexOrThrow(
                                "preparation_minutes"));

                List<Requirement> requirements =
                        loadRequirements(db, recipeId);

                // Empty or incomplete recipes are never suggested.
                if (requirements.isEmpty()) {
                    continue;
                }

                boolean allAvailable = true;

                for (Requirement requirement : requirements) {
                    String ingredientKey =
                            normalizeName(requirement.name);

                    Map<String, Double> availableUnits =
                            pantry.get(ingredientKey);

                    if (availableUnits == null) {
                        allAvailable = false;
                        break;
                    }

                    String targetUnit = normalizeUnit(requirement.unit);
                    double available = 0;

                    for (Map.Entry<String, Double> entry
                            : availableUnits.entrySet()) {

                        Double converted = convert(
                                entry.getValue(),
                                entry.getKey(),
                                targetUnit);

                        if (converted != null) {
                            available += converted;
                        }
                    }

                    if (available + 0.000001 < requirement.quantity) {
                        allAvailable = false;
                        break;
                    }
                }

                if (allAvailable) {
                    matches.add(new RecipeResult(
                            recipeId, name, instructions, minutes));
                }
            }
        } finally {
            recipes.close();
        }

        return matches;
    }

    private Map<String, Map<String, Double>> loadPantry(
            SQLiteDatabase db) {

        Map<String, Map<String, Double>> pantry = new HashMap<>();

        Cursor cursor = db.query(
                DatabaseHelper.PANTRY_TABLE,
                new String[]{"ingredient_name", "quantity", "unit"},
                null, null, null, null, null
        );

        try {
            while (cursor.moveToNext()) {
                String name = normalizeName(cursor.getString(0));
                double quantity = cursor.getDouble(1);
                String unit = normalizeUnit(cursor.getString(2));

                if (quantity <= 0) {
                    continue;
                }

                Map<String, Double> units = pantry.get(name);

                if (units == null) {
                    units = new HashMap<>();
                    pantry.put(name, units);
                }

                Double previous = units.get(unit);
                units.put(unit,
                        (previous == null ? 0 : previous) + quantity);
            }
        } finally {
            cursor.close();
        }

        return pantry;
    }

    private List<Requirement> loadRequirements(
            SQLiteDatabase db, long recipeId) {

        List<Requirement> requirements = new ArrayList<>();

        Cursor cursor = db.query(
                DatabaseHelper.RECIPE_INGREDIENTS_TABLE,
                new String[]{
                        "ingredient_name", "required_qty", "unit"
                },
                "recipe_id = ?",
                new String[]{String.valueOf(recipeId)},
                null, null, null
        );

        try {
            while (cursor.moveToNext()) {
                requirements.add(new Requirement(
                        cursor.getString(0),
                        cursor.getDouble(1),
                        cursor.getString(2)
                ));
            }
        } finally {
            cursor.close();
        }

        return requirements;
    }

    // Normalize common ingredient names and plural forms.
    public static String normalizeName(String input) {

        if (input == null) {
            return "";
        }

        String name = input.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");

        Map<String, String> irregular = new HashMap<>();
        irregular.put("tomatoes", "tomato");
        irregular.put("potatoes", "potato");
        irregular.put("leaves", "leaf");
        irregular.put("loaves", "loaf");
        irregular.put("berries", "berry");
        irregular.put("eggs", "egg");
        irregular.put("chilies", "chili");

        if (irregular.containsKey(name)) {
            return irregular.get(name);
        }

        if (name.endsWith("ies") && name.length() > 4) {
            return name.substring(0, name.length() - 3) + "y";
        }

        if (name.endsWith("oes") && name.length() > 4) {
            return name.substring(0, name.length() - 2);
        }

        if (name.endsWith("s")
                && !name.endsWith("ss")
                && name.length() > 3) {
            return name.substring(0, name.length() - 1);
        }

        return name;
    }

    private String normalizeUnit(String input) {

        if (input == null) {
            return "";
        }

        String unit = input.trim().toLowerCase(Locale.ROOT);

        switch (unit) {
            case "kg":
            case "kilogram":
            case "kilograms":
                return "kg";

            case "g":
            case "gram":
            case "grams":
                return "g";

            case "l":
            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return "l";

            case "ml":
            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
                return "ml";

            case "unit":
            case "piece":
            case "pieces":
            case "units":
            case "item":
            case "items":
                return "units";

            default:
                return unit;
        }
    }

    // Return null for incompatible units.
    private Double convert(
            double quantity, String from, String to) {

        if (from.equals(to)) {
            return quantity;
        }

        if (from.equals("kg") && to.equals("g")) {
            return quantity * 1000;
        }

        if (from.equals("g") && to.equals("kg")) {
            return quantity / 1000;
        }

        if (from.equals("l") && to.equals("ml")) {
            return quantity * 1000;
        }

        if (from.equals("ml") && to.equals("l")) {
            return quantity / 1000;
        }

        return null;
    }
}

