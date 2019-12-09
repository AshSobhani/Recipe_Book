package com.example.recipebookcw3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Arrays;

public class DBManager extends SQLiteOpenHelper {
    SQLiteDatabase db;

    public DBManager(Context context) {
        super(context, "recipeBookDB", null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE recipes (" +
                "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                " name VARCHAR(128) NOT NULL," +
                " instructions VARCHAR(128) NOT NULL," +
                "rating INTEGER);");

        db.execSQL("CREATE TABLE ingredients (" +
                " _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "ingredientname VARCHAR(128) NOT NULL);");

        db.execSQL("CREATE TABLE recipe_ingredients (\n" +
                " recipe_id INT NOT NULL," +
                " ingredient_id INT NOT NULL," +
                " CONSTRAINT fk1 FOREIGN KEY (recipe_id) REFERENCES recipes (_id)," +
                " CONSTRAINT fk2 FOREIGN KEY (ingredient_id) REFERENCES ingredients (_id)," +
                " CONSTRAINT _id PRIMARY KEY (recipe_id, ingredient_id) );");
    }

    public void addNewRecipe (String name, String ingredients, String instructions, String rating){
        //Log.d("Johnny", "In class");

        //Shortcut db
        db = getWritableDatabase();
        ArrayList <String> allDBIngredients = getDatabaseIngredients();
        boolean duplicate;
        int ingredientId = -1;

        //Convert Rating into an integer value
        int intRating;
        try {
            intRating = Integer.parseInt(rating);
        }
        catch (NumberFormatException e)
        {
            intRating = 0;
        }

        //Run SQL command to input new recipe values
        db.execSQL("INSERT INTO recipes (name, instructions, rating) " +
                "VALUES " +
                "('" + name + "','" + instructions + "','" + intRating + "');");

        //Split Ingredients into array of ingredients
        String ingredientsArray[] = ingredients.split("\\r?\\n");

        //Log.d("Johnny", "Before adding");
        //Only add ingredients if they are not duplicates
        for (String ingredient: ingredientsArray) {

            duplicate = false;
            for (String ingredientInDB : allDBIngredients) {
                if (ingredient.equals(ingredientInDB)) {
                    duplicate = true;
                    Cursor cursor = db.query("ingredients", new String[]{"_id"}, "ingredientname=?",
                            new String[]{ingredient}, null, null, null);

                    if (cursor.moveToFirst()) {
                        do {
                            ingredientId = cursor.getInt(0);
                        } while (cursor.moveToNext());

                        cursor.close();
                    }
                    break;
                }
            }

            if (!duplicate) {
                db.execSQL("INSERT INTO ingredients (ingredientname) " +
                        "VALUES " +
                        "('" + ingredient + "');");

                ingredientId = getNewestId("ingredients");

                //Log.d("Johnny", "Adding New Ingredients");
            }

            db.execSQL("INSERT INTO recipe_ingredients (recipe_id, ingredient_id)" +
                    "VALUES " +
                    "('" + getNewestId("recipes") + "','" + ingredientId + "');");
        }
    }

    public void deleteRecipe(int id) {
        ArrayList <Integer> recipeIngredientsId = new ArrayList<>();
        db = getWritableDatabase();
        Cursor c;

        //Execute sql query to remove from database
        db.delete("recipes", "_id=?", new String[]{""+id});

        //Check ingredients on
        c = db.query("recipe_ingredients", new String[]{"recipe_id", "ingredient_id"},
                "recipe_id=?", new String[]{"" + id}, null, null, null);

        if (c.moveToFirst()) {
            do {
                recipeIngredientsId.add(c.getInt(1));
            } while (c.moveToNext());
            c.close();
        }
        c.close();

        db.delete("recipe_ingredients", "recipe_id=?", new String[]{""+id});

        //Log.d("Johnny", "ingredientIDs: " + recipeIngredientsId);

        //Get all ingredients and make ingredient ID int
        ArrayList <String> ingredientsArray = getDatabaseRecipeIngredients();
        int ingredientId = -1;
        boolean exists = false;

        //Iterate through all recipe ingredients and ingredients in table
        for (int recipeIngredientId: recipeIngredientsId){

            //Log.d("Johnny", "ingredients in rec.ing: " + ingredientsArray);

            exists = false;
            for (String ingredient: ingredientsArray) {
                Cursor cursor = db.query("ingredients", new String[]{"_id"}, "_id=?",
                        new String[]{ingredient}, null, null, null);

                if (cursor.moveToFirst()) {
                    do {
                        ingredientId = cursor.getInt(0);
                        //Log.d("Johnny", "recipeIngredientId: " + recipeIngredientId);
                        //Log.d("Johnny", "ingredientId: " + ingredientId);
                    } while (cursor.moveToNext());
                    cursor.close();
                }
                //If it does exist set flag to true so it does'nt get deleted later
                if (recipeIngredientId == ingredientId) {
                    //Log.d("Johnny", "ingredient exists: " + recipeIngredientId);
                    exists = true;
                }
                //Log.d("Johnny", "ingredient that does'nt exist: " + recipeIngredientId);
                //Log.d("Johnny", "Exists: " + exists);
            }

            //If recipe ingredient does'nt exist in any other recipe then remove it from ingredient table
            if (!exists) {
                //Log.d("Johnny", "Deleting Ingredient");
                db.delete("ingredients", "_id=?", new String[]{"" + recipeIngredientId});
            }
        }
    }

    public void updateRating(String id, String newRating){
		db = getWritableDatabase();

        Log.d("Johnny", "updateRating: " + newRating);

		ContentValues ratingCV = new ContentValues();
        ratingCV.put("rating", newRating);

		//db.insert("recipes", null, ratingCV);
		db.update("recipes", ratingCV, "_id=" + id, null);
	}

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public ArrayList<ArrayList<String>> getRecipeDetails(int id) {
        ArrayList <String> recipeData = new ArrayList<>();
        ArrayList <String> recipeIngredients = new ArrayList<>();
        Cursor c;

        //Log.d("Johnny", "HERE ID:" + id);

        String [] recipeId = {"" + id};
        c = getWritableDatabase().rawQuery("select r._id as recipe_id, r.rating, ri.ingredient_id, i.ingredientname, r.name, r.instructions "+
                "from recipes r "+
                        "join recipe_ingredients ri on (r._id = ri.recipe_id)"+
                        "join ingredients i on (ri.ingredient_id = i._id) where r._id == ?", recipeId);

        //Log.d("Johnny", "recipeId: " + recipeId);
        //Log.d("Johnny", "HERE2");
        //Log.d("Johnny", "c.getCount(): " + c.getCount());

        if(c.moveToFirst()) {
            do {
                //Log.d("Johnny", "name: " + recipeData.add(c.getString(4)));
                
                //Don't set every time in cases of many ingredients
                if (recipeData.isEmpty()){
                    recipeData.add(c.getString(4));
                    recipeData.add(c.getString(5));
                    recipeData.add(c.getString(1));
                }
                recipeIngredients.add(c.getString(3));

            } while(c.moveToNext());
        }
        c.close();

        //Return as array of arrays holding all data and ingredient
        return new ArrayList<ArrayList<String>>(Arrays.asList(recipeData, recipeIngredients));
    }

    public ArrayList getDatabaseIngredients () {
        ArrayList <String> allIngredients = new ArrayList<String>();

        Cursor c = getWritableDatabase().query("ingredients", new String[] { "_id", "ingredientname"},
                null, null, null, null, null);

        if(c.moveToFirst()) {
            do {
                allIngredients.add(c.getString(1));
            } while(c.moveToNext());
        }

        return allIngredients;
    }

    public ArrayList getDatabaseRecipeIngredients () {
        ArrayList <String> allRecipeIngredients = new ArrayList<String>();

        Cursor c = getWritableDatabase().query("recipe_ingredients", new String[] { "recipe_id", "ingredient_id"},
                null, null, null, null, null);

        if(c.moveToFirst()) {
            do {
                allRecipeIngredients.add(c.getString(1));
            } while(c.moveToNext());
        }

        return allRecipeIngredients;
    }

    public int getNewestId (String table) {
        int maxId = 0;
        Cursor cursor = db.rawQuery("Select Max(_id) as maxId from " + table,null);

        if (cursor.moveToFirst()) {
            do {
                maxId = cursor.getInt(0);
            } while (cursor.moveToNext());

            cursor.close();
        }
        return maxId;
    }

    public Cursor getRecipesC(String sort) {
        return getWritableDatabase().query("recipes", new String[] { "_id", "name", "instructions", "rating"},
                null, null, null, null, sort);
    }

    public Cursor getIngredientsC() {
        return getWritableDatabase().query("ingredients", new String[] { "_id", "ingredientname"},
                null, null, null, null, null);
    }
}
