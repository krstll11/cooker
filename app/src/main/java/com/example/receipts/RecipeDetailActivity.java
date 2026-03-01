package com.example.receipts;



import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.receipts.models.Recipe;
import com.example.receipts.utils.RecipeRepository;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView textViewName, textViewIngredients, textViewSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);


        textViewName = findViewById(R.id.textViewDetailName);
        textViewIngredients = findViewById(R.id.textViewDetailIngredients);
        textViewSteps = findViewById(R.id.textViewDetailSteps);


        int recipeId = getIntent().getIntExtra("recipe_id", -1);


        Recipe recipe = RecipeRepository.getRecipeById(recipeId);
        if (recipe != null) {

            textViewName.setText(recipe.getNameResId());


            String[] ingredients = getResources().getStringArray(recipe.getIngredientsResId());
            textViewIngredients.setText(TextUtils.join("\n", ingredients));


            String[] steps = getResources().getStringArray(recipe.getStepsResId());
            StringBuilder stepsText = new StringBuilder();
            for (int i = 0; i < steps.length; i++) {
                stepsText.append(i + 1).append(". ").append(steps[i]).append("\n\n");
            }
            textViewSteps.setText(stepsText.toString().trim());
        }
    }
}
