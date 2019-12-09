package com.example.recipebookcw3;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class SelRecipeActivity extends AppCompatActivity {
    TextView name, ingredients, instructions, rating;
    int id;
    DBManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sel_recipe);

        db = new DBManager(this);

        if(getIntent().getExtras()!=null) {
            //Unpack bundle from recipes activity
            Bundle bundle = getIntent().getExtras();
            id = bundle.getInt("boxId");
        }

        name = findViewById(R.id.selRecipe);
        ingredients = findViewById(R.id.ingredientsText);
        instructions = findViewById(R.id.instructionsText);
        rating = findViewById(R.id.ratingText);

        updateRecipeFields();
    }

    public void updateRecipeFields() {
        ArrayList <ArrayList<String>> recipeDetails = db.getRecipeDetails(id);
        String ingredientBuffer = "";

        //Open up the array taking the data and ingredients
        ArrayList<String> recipeData = recipeDetails.get(0);
        ArrayList<String> recipeIngredients = recipeDetails.get(1);

        //Set all the text accordingly
        name.setText(recipeData.get(0));
        instructions.setText(recipeData.get(1));
        rating.setText(recipeData.get(2));

        //Loop for ingredients to parse all of them in
        for (String ingredient:recipeIngredients) {
            ingredientBuffer += ingredient + "\n";
        }
        ingredients.setText(ingredientBuffer);
    }

    public void onUpdateRating(View v) {
        EditText newRating = findViewById(R.id.updateRatingText);
        rating.setText(newRating.getText());

        //update the rating on database
        db.updateRating("" + id, "" + rating.getText());
    }

    public void onDeleteRecipe(View v) {
        db.deleteRecipe(id);
        finish();
    }

    public void toRecipesActivity(View v){
        setResult(0);
        finish();
    }
}
