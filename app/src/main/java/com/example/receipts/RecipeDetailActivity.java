package com.example.receipts;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.receipts.models.Recipe;
import com.example.receipts.utils.RecipeRepository;

import java.util.Locale;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView textViewName, textViewIngredients, textViewSteps;
    private Button btnSpeak;
    private VideoView videoView;
    private MediaPlayer mediaPlayer;
    private int recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);


        textViewName = findViewById(R.id.textViewDetailName);
        textViewIngredients = findViewById(R.id.textViewDetailIngredients);
        textViewSteps = findViewById(R.id.textViewDetailSteps);
        btnSpeak = findViewById(R.id.btnSpeak);
        videoView = findViewById(R.id.videoView);

        recipeId = getIntent().getIntExtra("recipe_id", -1);

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


        setupVideo();


        btnSpeak.setOnClickListener(v -> playRecipeAudio());


        if (recipeId != 1) {
            btnSpeak.setVisibility(View.GONE);
        }
    }

    private void setupVideo() {
        if (recipeId == 1) {

            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.recipe_3_video);
            videoView.setVideoURI(videoUri);


            MediaController mediaController = new MediaController(this);
            videoView.setMediaController(mediaController);
            mediaController.setAnchorView(videoView);


        } else {

            videoView.setVisibility(View.GONE);
        }
    }

    private String getCurrentLanguage() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String langCode = prefs.getString("My_Lang", "");
        if (langCode.isEmpty()) {
            langCode = Locale.getDefault().getLanguage();
        }
        return langCode;
    }

    private void playRecipeAudio() {
        if (recipeId != 1) return;

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        String lang = getCurrentLanguage();
        int audioRes;
        switch (lang) {
            case "ru":
                audioRes = R.raw.recipe_1_audio_ru;
                break;
            case "zh":
                audioRes = R.raw.recipe_1_audio_zh;
                break;
            default:
                audioRes = R.raw.recipe_1_audio_en;
                break;
        }

        mediaPlayer = MediaPlayer.create(this, audioRes);
        mediaPlayer.setOnCompletionListener(mp -> {
            mp.release();
            mediaPlayer = null;
        });
        mediaPlayer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}