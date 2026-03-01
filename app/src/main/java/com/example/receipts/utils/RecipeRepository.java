package com.example.receipts.utils;



import com.example.receipts.R;
import com.example.receipts.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeRepository {
    private static List<Recipe> recipes = new ArrayList<>();

    static {

        recipes.add(new Recipe(1, "breakfast", "english",
                R.string.recipe_1_name, R.array.recipe_1_ingredients, R.array.recipe_1_steps));

        recipes.add(new Recipe(2, "lunch", "russian",
                R.string.recipe_2_name, R.array.recipe_2_ingredients, R.array.recipe_2_steps));

        recipes.add(new Recipe(3, "dinner", "sichuan",
                R.string.recipe_3_name, R.array.recipe_3_ingredients, R.array.recipe_3_steps));
    }

    public static Recipe getRecipeById(int id) {
        for (Recipe r : recipes) {
            if (r.getId() == id) return r;
        }
        return null;
    }

    public static List<Recipe> getAllRecipes() {
        return new ArrayList<>(recipes);
    }
}
