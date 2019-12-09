package com.example.recipebookcw3;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.TextView;

public class RecipesActivity extends AppCompatActivity {
    static int INGREDIENTS_ACTIVITY_REQUEST_CODE = 1;
    static int ADDRECIPE_ACTIVITY_REQUEST_CODE = 2;
    static int SELRECIPE_ACTIVITY_REQUEST_CODE = 3;

    SimpleCursorAdapter dataAdapter;
    DBManager db;
    boolean toggle;
    String sort;
    Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        //Make instance of my database
        db = new DBManager(this);
        sort = "_id";

        //Update Recipe List
        updateRecipeList();
    }

    public void updateRecipeList() {
        //Get recipe list view
        ListView recipeListView = (ListView) findViewById(R.id.recipiesList);

        //Get c (all the data)
        c = db.getRecipesC(sort);

        //Iterate through the data and do something with it
        if(c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                String name = c.getString(1);
                String rating = c.getString(3);

            } while(c.moveToNext());
        }

        //Mogul creators...
        String[] columns = new String[] {
                "_id",
                "name",
                "rating"
        };
        int[] to = new int[] {
        		R.id.boxId,
                R.id.recipeName,
                R.id.recipeRating,
        };
        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.recipe_row,
                c,
                columns,
                to,
                0);

        recipeListView.setAdapter(dataAdapter);

        //Create intent to got to selected recipe
		final Intent intent = new Intent(this, SelRecipeActivity.class);

		recipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				int id = Integer.parseInt(((TextView) view.findViewById(R.id.boxId)).getText().toString());
				intent.putExtra("boxId", id);
				startActivityForResult(intent, SELRECIPE_ACTIVITY_REQUEST_CODE);
			}
		});
    }

    public void onRatingSort(View v) {
        if (toggle) {
            toggle = false;
            sort = "_id";
        } else {
            toggle = true;
            sort = "rating desc";
        }
        updateRecipeList();
    }

    public void onIngredientsActivity(View v) {
        //Make intent and start activity
        Intent intent = new Intent(this, IngredientsActivity.class);
        startActivityForResult(intent, INGREDIENTS_ACTIVITY_REQUEST_CODE);
    }

    public void onAddRecipeActivity(View v) {
        //Make intent and start activity
        Intent intent = new Intent(this, AddRecipeActivity.class);
        startActivityForResult(intent, ADDRECIPE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        updateRecipeList();
    }
}
