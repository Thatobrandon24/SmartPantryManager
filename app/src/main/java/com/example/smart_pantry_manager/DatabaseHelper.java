package com.example.smart_pantry_manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "smart_pantry.db";
    public static final int DATABASE_VERSION = 1;

    public static final String PANTRY_TABLE = "pantry_table";
    public static final String RECIPE_TABLE = "recipe_table";
    public static final String RECIPE_INGREDIENTS_TABLE =
            "recipe_ingredients_table";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + PANTRY_TABLE + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ingredient_name TEXT NOT NULL, " +
                "quantity REAL NOT NULL CHECK(quantity > 0), " +
                "unit TEXT NOT NULL, " +
                "expiration_date TEXT)");

        db.execSQL("CREATE TABLE " + RECIPE_TABLE + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipe_name TEXT NOT NULL, " +
                "instructions TEXT NOT NULL, " +
                "preparation_minutes INTEGER NOT NULL)");

        db.execSQL("CREATE TABLE " + RECIPE_INGREDIENTS_TABLE + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipe_id INTEGER NOT NULL, " +
                "ingredient_name TEXT NOT NULL, " +
                "required_qty REAL NOT NULL CHECK(required_qty > 0), " +
                "unit TEXT NOT NULL, " +
                "FOREIGN KEY(recipe_id) REFERENCES " +
                RECIPE_TABLE + "(_id) ON DELETE CASCADE)");

        insertSampleRecipes(db);
    }

    private void insertSampleRecipes(SQLiteDatabase db) {
        db.beginTransaction();

        try {
            long pastaId = insertRecipe(
                    db,
                    "Tomato Pasta",
                    "1. Boil the pasta.\n" +
                            "2. Chop the tomatoes and garlic.\n" +
                            "3. Cook the tomatoes and garlic in olive oil.\n" +
                            "4. Mix the sauce with the pasta and serve.",
                    25
            );

            insertRecipeIngredient(db, pastaId, "pasta", 200, "g");
            insertRecipeIngredient(db, pastaId, "tomato", 2, "units");
            insertRecipeIngredient(db, pastaId, "garlic", 1, "units");
            insertRecipeIngredient(db, pastaId, "olive oil", 15, "ml");

            long pancakeId = insertRecipe(
                    db,
                    "Simple Pancakes",
                    "1. Mix the flour and milk.\n" +
                            "2. Add the egg and whisk until smooth.\n" +
                            "3. Cook small portions in a lightly oiled pan.\n" +
                            "4. Turn when bubbles appear and cook the other side.",
                    20
            );

            insertRecipeIngredient(db, pancakeId, "flour", 150, "g");
            insertRecipeIngredient(db, pancakeId, "milk", 200, "ml");
            insertRecipeIngredient(db, pancakeId, "egg", 1, "units");

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private long insertRecipe(
            SQLiteDatabase db,
            String name,
            String instructions,
            int minutes) {

        ContentValues values = new ContentValues();
        values.put("recipe_name", name);
        values.put("instructions", instructions);
        values.put("preparation_minutes", minutes);

        return db.insertOrThrow(RECIPE_TABLE, null, values);
    }

    private void insertRecipeIngredient(
            SQLiteDatabase db,
            long recipeId,
            String name,
            double quantity,
            String unit) {

        ContentValues values = new ContentValues();
        values.put("recipe_id", recipeId);
        values.put("ingredient_name", name);
        values.put("required_qty", quantity);
        values.put("unit", unit);

        db.insertOrThrow(RECIPE_INGREDIENTS_TABLE, null, values);
    }

    // CREATE: add a pantry ingredient.
    public long addPantryItem(
            String name,
            double quantity,
            String unit,
            String expiryDate) {

        if (name == null || name.trim().isEmpty()
                || quantity <= 0
                || Double.isNaN(quantity)
                || Double.isInfinite(quantity)
                || unit == null || unit.trim().isEmpty()) {
            return -1;
        }

        ContentValues values = new ContentValues();
        values.put("ingredient_name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", unit.trim());
        values.put("expiration_date", expiryDate);

        return getWritableDatabase().insert(
                PANTRY_TABLE, null, values);
    }

    // READ: return all pantry ingredients.
    public Cursor getAllPantryItems() {
        return getReadableDatabase().query(
                PANTRY_TABLE,
                null,
                null,
                null,
                null,
                null,
                "ingredient_name COLLATE NOCASE ASC"
        );
    }

    // READ: retrieve one ingredient by its database ID.
    public Cursor getPantryItem(long id) {
        return getReadableDatabase().query(
                PANTRY_TABLE,
                null,
                "_id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );
    }

    // UPDATE: edit an existing ingredient.
    public int updatePantryItem(
            long id,
            String name,
            double quantity,
            String unit,
            String expiryDate) {

        if (id <= 0 || name == null || name.trim().isEmpty()
                || quantity <= 0
                || Double.isNaN(quantity)
                || Double.isInfinite(quantity)
                || unit == null || unit.trim().isEmpty()) {
            return 0;
        }

        ContentValues values = new ContentValues();
        values.put("ingredient_name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", unit.trim());
        values.put("expiration_date", expiryDate);

        return getWritableDatabase().update(
                PANTRY_TABLE,
                values,
                "_id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // DELETE: remove an ingredient.
    public int deletePantryItem(long id) {
        return getWritableDatabase().delete(
                PANTRY_TABLE,
                "_id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        // Add version-specific migrations here when the schema changes.
        // Do not drop tables: existing user data must be preserved.
    }
}
