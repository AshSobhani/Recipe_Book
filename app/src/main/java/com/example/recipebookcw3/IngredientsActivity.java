package com.example.recipebookcw3;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class IngredientsActivity extends AppCompatActivity {
    SimpleCursorAdapter dataAdapter;
    DBManager db;
    Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        //Make instance of my database
        db = new DBManager(this);

        //Update Recipe List
        updateIngredientsList();
    }

    public void updateIngredientsList(){
        //Get recipe list view
        ListView recipeListView = (ListView) findViewById(R.id.ingredientsList);

        //Get c (all the data)
        c = db.getIngredientsC();

        //Iterate through the data and do something with it
        if(c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                String ingredient = c.getString(1);

            } while(c.moveToNext());
        }

        //Mogul creators...
        String[] columns = new String[] {
                "ingredientname"
        };
        int[] to = new int[] {
                R.id.ingredientName,
        };
        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.ingredient_row,
                c,
                columns,
                to,
                0);

        recipeListView.setAdapter(dataAdapter);
    }

    public void toRecipesActivity(View v){
        setResult(0);
        finish();
    }
}
