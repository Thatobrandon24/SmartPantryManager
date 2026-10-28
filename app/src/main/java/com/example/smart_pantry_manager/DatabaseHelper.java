package com.example.smart_pantry_manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "smart_pantry.db";
    public static final int DATABASE_VERSION = 5;

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

            long Chicken_pastaId = insertRecipe(
                    db,
                    "Chicken Pasta Bake",
                    "Serves 6 | Prep: 30 minutes | Cook: 45 minutes\n\n" +
                            "1. Heat 30 ml of olive oil in a pan. " +
                            "Fry the chopped onion gently for 10-12 minutes.\n\n" +
                            "2. Add the crushed garlic and chilli flakes. " +
                            "Cook for 1 minute. Add the chopped tomatoes and sugar. " +
                            "Season to taste and simmer uncovered for about 20 minutes " +
                            "until the sauce thickens. Stir in the mascarpone.\n\n" +
                            "3. Heat 15 ml of olive oil in a separate frying pan. " +
                            "Season the chicken and fry for 5-7 minutes, or until " +
                            "cooked through. Ensure the chicken reaches a safe internal " +
                            "temperature of 74 degrees C.\n\n" +
                            "4. Preheat the oven to 220 degrees C " +
                            "(200 degrees C fan).\n\n" +
                            "5. Cook the penne according to the packet instructions. " +
                            "Drain and toss with the remaining 15 ml of olive oil.\n\n" +
                            "6. Transfer the pasta to an ovenproof dish. " +
                            "Mix in the cooked chicken and tomato sauce.\n\n" +
                            "7. Top with grated cheddar, mozzarella and chopped parsley. " +
                            "Bake for about 20 minutes until golden and bubbling.\n\n" +
                            "8. Serve hot. Check that the chicken is thoroughly cooked.",
                    75
            );

            insertRecipeIngredient(db, Chicken_pastaId, "olive oil", 60, "ml");
            insertRecipeIngredient(db, Chicken_pastaId, "onion", 1, "units");
            insertRecipeIngredient(db, Chicken_pastaId, "garlic", 2, "units");
            insertRecipeIngredient(db, Chicken_pastaId, "chilli flakes", 0.25, "tsp");
            insertRecipeIngredient(db, Chicken_pastaId, "chopped tomatoes", 800, "g");
            insertRecipeIngredient(db, Chicken_pastaId, "caster sugar", 1, "tsp");
            insertRecipeIngredient(db, Chicken_pastaId, "mascarpone", 6, "tbsp");
            insertRecipeIngredient(db, Chicken_pastaId, "chicken breast", 4, "units");
            insertRecipeIngredient(db, Chicken_pastaId, "penne", 300, "g");
            insertRecipeIngredient(db, Chicken_pastaId, "mature cheddar", 70, "g");
            insertRecipeIngredient(db, Chicken_pastaId, "mozzarella", 50, "g");
            insertRecipeIngredient(db, Chicken_pastaId, "parsley", 0.5, "bunch");


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


            long tomatoRiceId = insertRecipe(
                    db,
                    "Tomato Rice",
                    "1. Wash the rice.\n" +
                            "2. Chop the tomato and onion.\n" +
                            "3. Cook the onion and tomato in a pan.\n" +
                            "4. Add the rice and water, then cook until tender.",
                    25
            );

            insertRecipeIngredient(db, tomatoRiceId, "rice", 200, "g");
            insertRecipeIngredient(db, tomatoRiceId, "tomato", 100, "g");
            insertRecipeIngredient(db, tomatoRiceId, "onion", 50, "g");


            long eggTomatoId = insertRecipe(
                    db,
                    "Egg and Tomato",
                    "1. Chop the tomato and onion.\n" +
                            "2. Cook them in a pan until soft.\n" +
                            "3. Add the eggs and stir until cooked.",
                    15
            );

            insertRecipeIngredient(db, eggTomatoId, "egg", 2, "units");
            insertRecipeIngredient(db, eggTomatoId, "tomato", 100, "g");
            insertRecipeIngredient(db, eggTomatoId, "onion", 30, "g");


            long potatoOnionId = insertRecipe(
                    db,
                    "Potato and Onion",
                    "1. Peel and chop the potatoes.\n" +
                            "2. Chop the onion.\n" +
                            "3. Boil the potatoes until tender.\n" +
                            "4. Add the onion and cook until ready.",
                    30
            );

            insertRecipeIngredient(db, potatoOnionId, "potato", 300, "g");
            insertRecipeIngredient(db, potatoOnionId, "onion", 50, "g");

            long Onion_Fried_RiceId = insertRecipe(
                    db,
                    "Onion Fried Rice",
                    "1. Cook the rice until tender.\n" +
                            "2. Chop the onion.\n" +
                            "3. Cook the onion in a pan.\n" +
                            "4. Add the cooked rice and mix well.",
                    20
            );
            insertRecipeIngredient(db, Onion_Fried_RiceId, "rice", 200, "g");
            insertRecipeIngredient(db, Onion_Fried_RiceId, "onion", 50, "g");

            long Tomato_Egg_RiceId = insertRecipe(
                    db,
                    "Tomato Egg Rice",
                    "1. Cook the rice.\n" +
                            "2. Chop and cook the tomato.\n" +
                            "3. Add the egg and cook until set.\n" +
                            "4. Serve with the rice.",
                    25
            );
            insertRecipeIngredient(db, Tomato_Egg_RiceId, "rice", 150, "g");
            insertRecipeIngredient(db, Tomato_Egg_RiceId, "tomato", 100, "g");
            insertRecipeIngredient(db, Tomato_Egg_RiceId, "egg", 1, "units");

            long Potato_Tomato_HashId = insertRecipe(
                    db,
                    "Potato Tomato Hash",
                    "1. Wash and dice the potato.\n" +
                            "2. Cook the potato until tender.\n" +
                            "3. Add chopped tomato and onion.\n" +
                            "4. Cook until the vegetables soften.",
                    30
            );
            insertRecipeIngredient(db, Potato_Tomato_HashId, "potato", 250, "g");
            insertRecipeIngredient(db, Potato_Tomato_HashId, "tomato", 100, "g");
            insertRecipeIngredient(db, Potato_Tomato_HashId, "onion", 30, "g");

            long Onion_OmeletteId = insertRecipe(
                    db,
                    "Onion Omelette",
                    "1. Beat the eggs.\n" +
                            "2. Chop the onion.\n" +
                            "3. Cook the onion in a pan.\n" +
                            "4. Add the eggs and cook until set.",
                    10
            );
            insertRecipeIngredient(db, Onion_OmeletteId, "egg", 2, "units");
            insertRecipeIngredient(db, Onion_OmeletteId, "onion", 30, "g");

            long Tomato_OmeletteId = insertRecipe(
                    db,
                    "Tomato Omelette",
                    "1. Beat the eggs.\n" +
                            "2. Chop the tomato.\n" +
                            "3. Cook the tomato briefly.\n" +
                            "4. Add the eggs and cook until set.",
                    12
            );
            insertRecipeIngredient(db, Tomato_OmeletteId, "egg", 2, "units");
            insertRecipeIngredient(db, Tomato_OmeletteId, "tomato", 100, "g");

            long Garlic_Tomato_PastaId = insertRecipe(
                    db,
                    "Garlic Tomato Pasta",
                    "1. Cook the pasta according to its instructions.\n" +
                            "2. Chop the tomato and garlic.\n" +
                            "3. Cook the garlic and tomato in olive oil.\n" +
                            "4. Mix the sauce with the pasta.",
                    25
            );
            insertRecipeIngredient(db, Garlic_Tomato_PastaId, "pasta", 200, "g");
            insertRecipeIngredient(db, Garlic_Tomato_PastaId, "tomato", 100, "g");
            insertRecipeIngredient(db, Garlic_Tomato_PastaId, "garlic", 1, "units");
            insertRecipeIngredient(db, Garlic_Tomato_PastaId, "olive oil", 15, "ml");

            long Egg_and_Potato_CakesId = insertRecipe(
                    db,
                    "Egg and Potato Cakes",
                    "1. Boil the potato until tender.\n" +
                            "2. Mash the potato.\n" +
                            "3. Beat the egg and mix it into the potato.\n" +
                            "4. Shape into small cakes and cook thoroughly.",
                    25
            );
            insertRecipeIngredient(db, Egg_and_Potato_CakesId, "potato", 200, "g");
            insertRecipeIngredient(db, Egg_and_Potato_CakesId, "egg", 1, "units");

            long Potato_Rice_BowlId = insertRecipe(
                    db,
                    "Potato Rice Bowl",
                    "1. Cook the rice.\n" +
                            "2. Dice and cook the potato until tender.\n" +
                            "3. Chop and cook the tomato.\n" +
                            "4. Serve the vegetables over the rice.",
                    30
            );
            insertRecipeIngredient(db, Potato_Rice_BowlId, "potato", 150, "g");
            insertRecipeIngredient(db, Potato_Rice_BowlId, "rice", 150, "g");
            insertRecipeIngredient(db, Potato_Rice_BowlId, "tomato", 50, "g");

            long Egg_SkilletId = insertRecipe(
                    db,
                    "Tomato Onion Egg Skillet",
                    "1. Chop the tomato and onion.\n" +
                            "2. Cook the vegetables in a pan.\n" +
                            "3. Add the eggs.\n" +
                            "4. Cook until the eggs are fully set.",
                    15
            );
            insertRecipeIngredient(db, Egg_SkilletId, "tomato", 100, "g");
            insertRecipeIngredient(db, Egg_SkilletId, "onion", 50, "g");
            insertRecipeIngredient(db, Egg_SkilletId, "egg", 2, "units");

            long Tomato_Rice_and_PotatoId = insertRecipe(
                    db,
                    "Tomato Rice and Potato Mix",
                    "1. Cook the rice.\n" +
                            "2. Dice and cook the potato until tender.\n" +
                            "3. Chop and cook the tomato and onion.\n" +
                            "4. Combine everything and heat thoroughly.",
                    35
            );
            insertRecipeIngredient(db, Tomato_Rice_and_PotatoId, "rice", 150, "g");
            insertRecipeIngredient(db, Tomato_Rice_and_PotatoId, "potato", 150, "g");
            insertRecipeIngredient(db, Tomato_Rice_and_PotatoId, "tomato", 100, "g");
            insertRecipeIngredient(db, Tomato_Rice_and_PotatoId, "onion", 30, "g");

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

    // add a pantry ingredient
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

    //return all pantry ingredients.
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

    //retrieve one ingredient by its database ID
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

    //  edit an existing ingredient from pantry
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

    //remove an ingredient from pantry
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

        if (oldVersion < 2) {
            insertSampleRecipes(db);
        }
    }
}
