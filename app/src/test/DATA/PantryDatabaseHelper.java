package com.example.test.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PantryDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PANTRY = "pantry_table";
    public static final String TABLE_RECIPES = "recipe_table";
    public static final String TABLE_RECIPE_INGREDIENTS =
            "recipe_ingredients_table";
    public static final String TABLE_SETTINGS = "settings_table";

    public PantryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Stores ingredients owned by the user.
        db.execSQL(
                "CREATE TABLE " + TABLE_PANTRY + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "ingredient_name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL CHECK(quantity > 0), " +
                        "unit TEXT NOT NULL, " +
                        "expiration_date TEXT)"
        );

        // Stores recipe information.
        db.execSQL(
                "CREATE TABLE " + TABLE_RECIPES + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "recipe_name TEXT NOT NULL, " +
                        "instructions TEXT NOT NULL, " +
                        "preparation_minutes INTEGER NOT NULL DEFAULT 0)"
        );

        // Links recipes to their required ingredients.
        db.execSQL(
                "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "recipe_id INTEGER NOT NULL, " +
                        "ingredient_name TEXT NOT NULL, " +
                        "required_qty REAL NOT NULL CHECK(required_qty > 0), " +
                        "unit TEXT NOT NULL, " +
                        "FOREIGN KEY(recipe_id) REFERENCES " +
                        TABLE_RECIPES + "(_id) ON DELETE CASCADE)"
        );

        // Saves simple user preferences.
        db.execSQL(
                "CREATE TABLE " + TABLE_SETTINGS + " (" +
                        "setting_key TEXT PRIMARY KEY, " +
                        "setting_value TEXT NOT NULL)"
        );

        // Default settings.
        db.execSQL(
                "INSERT INTO " + TABLE_SETTINGS +
                        " (setting_key, setting_value) VALUES " +
                        "('expiry_alerts', 'true')"
        );

        db.execSQL(
                "INSERT INTO " + TABLE_SETTINGS +
                        " (setting_key, setting_value) VALUES " +
                        "('sort_order', 'name')"
        );
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        // Preserve user data when adding future schema upgrades.
        // Add version-specific migration statements here.
    }
}
