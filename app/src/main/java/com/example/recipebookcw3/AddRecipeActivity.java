package com.example.recipebookcw3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class AddRecipeActivity extends AppCompatActivity {
    String name, ingredients, instructions, rating;
    DBManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        //Make instance of my database
        db = new DBManager(this);
    }

    public void onAddRecipe (View v) {
        name = ((EditText) findViewById(R.id.newName)).getText().toString();
        ingredients = ((EditText) findViewById(R.id.newIngredients)).getText().toString();
        instructions = ((EditText) findViewById(R.id.newInstructions)).getText().toString();
        rating = ((EditText) findViewById(R.id.newRating)).getText().toString();

        //Log.d("Johnny", "Before calling add new recipe");
        //make instance of database and call addRecipe...
        db.addNewRecipe(name, ingredients, instructions, rating);

        finish();
    }

    public void toRecipesActivity(View v){
        setResult(0);
        finish();
    }
}
