package com.example.receipts;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.receipts.adapters.RecipeAdapter;
import com.example.receipts.models.Recipe;
import com.example.receipts.utils.RecipeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerCategory, spinnerCuisine;
    private Button btnLanguage;
    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private List<Recipe> allRecipes;
    private List<Recipe> filteredRecipes;

    private final String[] categoryKeys = {"all", "breakfast", "lunch", "dinner"};
    private final String[] cuisineKeys = {"all", "english", "russian", "sichuan"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setAppLocale();
        setContentView(R.layout.activity_main);

        initViews();
        setupSpinners();
        loadRecipes();
        setupRecyclerView();
        setupLanguageButton();
    }


    private void setAppLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String langCode = prefs.getString("My_Lang", "");

        if (langCode.isEmpty()) {
            langCode = Locale.getDefault().getLanguage();
        }

        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources res = getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        res.updateConfiguration(config, res.getDisplayMetrics());
    }


    private void initViews() {
        spinnerCategory = findViewById(R.id.spinner_category);
        spinnerCuisine = findViewById(R.id.spinner_cuisine);
        btnLanguage = findViewById(R.id.btn_language);
        recyclerView = findViewById(R.id.recyclerView);
    }


    private void setupSpinners() {

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);


        ArrayAdapter<CharSequence> cuisineAdapter = ArrayAdapter.createFromResource(this,
                R.array.cuisines_array, android.R.layout.simple_spinner_item);
        cuisineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCuisine.setAdapter(cuisineAdapter);


        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterRecipes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        spinnerCuisine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterRecipes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }


    private void loadRecipes() {
        allRecipes = RecipeRepository.getAllRecipes();
    }
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        filteredRecipes = new ArrayList<>(allRecipes); // сначала показываем все
        adapter = new RecipeAdapter(this, filteredRecipes);
        recyclerView.setAdapter(adapter);
    }


    private void filterRecipes() {
        int categoryPos = spinnerCategory.getSelectedItemPosition();
        int cuisinePos = spinnerCuisine.getSelectedItemPosition();

        String selectedCategory = categoryKeys[categoryPos];
        String selectedCuisine = cuisineKeys[cuisinePos];

        filteredRecipes.clear();
        for (Recipe recipe : allRecipes) {
            boolean matchCategory = selectedCategory.equals("all") || recipe.getCategory().equals(selectedCategory);
            boolean matchCuisine = selectedCuisine.equals("all") || recipe.getCuisine().equals(selectedCuisine);
            if (matchCategory && matchCuisine) {
                filteredRecipes.add(recipe);
            }
        }
        adapter.notifyDataSetChanged();
    }


    private void setupLanguageButton() {
        btnLanguage.setOnClickListener(v -> showLanguageDialog());
    }


    private void showLanguageDialog() {
        String[] languages = {"English", "Русский", "中文"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.change_language)
                .setItems(languages, (dialog, which) -> {
                    String langCode;
                    switch (which) {
                        case 0: langCode = "en"; break;
                        case 1: langCode = "ru"; break;
                        case 2: langCode = "zh"; break;
                        default: langCode = "en";
                    }
                    saveLanguage(langCode);
                    recreate();
                })
                .show();
    }


    private void saveLanguage(String langCode) {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("My_Lang", langCode);
        editor.apply();
    }
}