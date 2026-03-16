package com.example.receipts;

import android.graphics.Color;
import android.os.CountDownTimer;
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
    private VideoView videoView;
    private int recipeId;

    // Таймеры
    private TextView timerEggsText, timerBaconText, timerBorschtText;
    private Button timerEggsStart, timerEggsPause, timerEggsReset;
    private Button timerBaconStart, timerBaconPause, timerBaconReset;
    private Button timerBorschtStart, timerBorschtPause, timerBorschtReset;

    private CountDownTimer eggsTimer, baconTimer, borschtTimer;
    private boolean eggsRunning = false;
    private boolean baconRunning = false;
    private boolean borschtRunning = false;

    private long eggsMillisLeft;
    private long baconMillisLeft;
    private long borschtMillisLeft;

    private final long EGGS_INITIAL = 8 * 60_000;
    private final long BACON_INITIAL = 10 * 60_000;
    private final long BORSCHT_INITIAL = 120 * 60_000;

    private int defaultTimerColor;

    private MediaPlayer eggsSound, baconSound, borschtSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        textViewName = findViewById(R.id.textViewDetailName);
        textViewIngredients = findViewById(R.id.textViewDetailIngredients);
        textViewSteps = findViewById(R.id.textViewDetailSteps);
        videoView = findViewById(R.id.videoView);

        timerEggsText = findViewById(R.id.timerEggsText);
        timerBaconText = findViewById(R.id.timerBaconText);
        timerBorschtText = findViewById(R.id.timerBorschtText);

        timerEggsStart = findViewById(R.id.timerEggsStart);
        timerEggsPause = findViewById(R.id.timerEggsPause);
        timerEggsReset = findViewById(R.id.timerEggsReset);

        timerBaconStart = findViewById(R.id.timerBaconStart);
        timerBaconPause = findViewById(R.id.timerBaconPause);
        timerBaconReset = findViewById(R.id.timerBaconReset);

        timerBorschtStart = findViewById(R.id.timerBorschtStart);
        timerBorschtPause = findViewById(R.id.timerBorschtPause);
        timerBorschtReset = findViewById(R.id.timerBorschtReset);

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

        eggsMillisLeft = EGGS_INITIAL;
        baconMillisLeft = BACON_INITIAL;
        borschtMillisLeft = BORSCHT_INITIAL;

        defaultTimerColor = timerEggsText.getCurrentTextColor();

        updateTimerText(timerEggsText, eggsMillisLeft);
        updateTimerText(timerBaconText, baconMillisLeft);
        updateTimerText(timerBorschtText, borschtMillisLeft);

        eggsSound = MediaPlayer.create(this, R.raw.timer_eggs_done);
        baconSound = MediaPlayer.create(this, R.raw.timer_bacon_done);
        borschtSound = MediaPlayer.create(this, R.raw.timer_borscht_done);

        if (recipeId == 1) {
            showEggsBaconTimers();
            hideBorschtTimer();
        } else if (recipeId == 2) {
            hideEggsBaconTimers();
            showBorschtTimer();
        } else {
            hideEggsBaconTimers();
            hideBorschtTimer();
        }

        setupTimers();
        setupVideo();
    }

    private void showEggsBaconTimers() {
        findViewById(R.id.timerEggsLabel).setVisibility(View.VISIBLE);
        timerEggsText.setVisibility(View.VISIBLE);
        timerEggsStart.setVisibility(View.VISIBLE);
        timerEggsPause.setVisibility(View.VISIBLE);
        timerEggsReset.setVisibility(View.VISIBLE);

        findViewById(R.id.timerBaconLabel).setVisibility(View.VISIBLE);
        timerBaconText.setVisibility(View.VISIBLE);
        timerBaconStart.setVisibility(View.VISIBLE);
        timerBaconPause.setVisibility(View.VISIBLE);
        timerBaconReset.setVisibility(View.VISIBLE);
    }

    private void hideEggsBaconTimers() {
        findViewById(R.id.timerEggsLabel).setVisibility(View.GONE);
        timerEggsText.setVisibility(View.GONE);
        timerEggsStart.setVisibility(View.GONE);
        timerEggsPause.setVisibility(View.GONE);
        timerEggsReset.setVisibility(View.GONE);

        findViewById(R.id.timerBaconLabel).setVisibility(View.GONE);
        timerBaconText.setVisibility(View.GONE);
        timerBaconStart.setVisibility(View.GONE);
        timerBaconPause.setVisibility(View.GONE);
        timerBaconReset.setVisibility(View.GONE);
    }

    private void showBorschtTimer() {
        findViewById(R.id.timerBorschtLabel).setVisibility(View.VISIBLE);
        timerBorschtText.setVisibility(View.VISIBLE);
        timerBorschtStart.setVisibility(View.VISIBLE);
        timerBorschtPause.setVisibility(View.VISIBLE);
        timerBorschtReset.setVisibility(View.VISIBLE);
    }

    private void hideBorschtTimer() {
        findViewById(R.id.timerBorschtLabel).setVisibility(View.GONE);
        timerBorschtText.setVisibility(View.GONE);
        timerBorschtStart.setVisibility(View.GONE);
        timerBorschtPause.setVisibility(View.GONE);
        timerBorschtReset.setVisibility(View.GONE);
    }

    private void setupTimers() {
        // Яйца 8 минут
        timerEggsStart.setOnClickListener(v -> {
            if (eggsRunning) return;
            startEggsTimer();
        });
        timerEggsPause.setOnClickListener(v -> pauseEggsTimer());
        timerEggsReset.setOnClickListener(v -> resetEggsTimer());

        // Бекон 10 минут
        timerBaconStart.setOnClickListener(v -> {
            if (baconRunning) return;
            startBaconTimer();
        });
        timerBaconPause.setOnClickListener(v -> pauseBaconTimer());
        timerBaconReset.setOnClickListener(v -> resetBaconTimer());

        // Борщ 120 минут
        timerBorschtStart.setOnClickListener(v -> {
            if (borschtRunning) return;
            startBorschtTimer();
        });
        timerBorschtPause.setOnClickListener(v -> pauseBorschtTimer());
        timerBorschtReset.setOnClickListener(v -> resetBorschtTimer());
    }

    private void startEggsTimer() {
        eggsRunning = true;
        timerEggsText.setTextColor(defaultTimerColor);
        eggsTimer = new CountDownTimer(eggsMillisLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                eggsMillisLeft = millisUntilFinished;
                updateTimerText(timerEggsText, eggsMillisLeft);
                checkLastMinute(timerEggsText, eggsMillisLeft);
            }

            @Override
            public void onFinish() {
                eggsRunning = false;
                eggsMillisLeft = 0;
                updateTimerText(timerEggsText, eggsMillisLeft);
                timerEggsText.setTextColor(defaultTimerColor);
                if (eggsSound != null) eggsSound.start();
            }
        }.start();
    }

    private void pauseEggsTimer() {
        if (!eggsRunning) return;
        eggsRunning = false;
        if (eggsTimer != null) eggsTimer.cancel();
    }

    private void resetEggsTimer() {
        if (eggsTimer != null) eggsTimer.cancel();
        eggsRunning = false;
        eggsMillisLeft = EGGS_INITIAL;
        timerEggsText.setTextColor(defaultTimerColor);
        updateTimerText(timerEggsText, eggsMillisLeft);
    }

    private void startBaconTimer() {
        baconRunning = true;
        timerBaconText.setTextColor(defaultTimerColor);
        baconTimer = new CountDownTimer(baconMillisLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                baconMillisLeft = millisUntilFinished;
                updateTimerText(timerBaconText, baconMillisLeft);
                checkLastMinute(timerBaconText, baconMillisLeft);
            }

            @Override
            public void onFinish() {
                baconRunning = false;
                baconMillisLeft = 0;
                updateTimerText(timerBaconText, baconMillisLeft);
                timerBaconText.setTextColor(defaultTimerColor);
                if (baconSound != null) baconSound.start();
            }
        }.start();
    }

    private void pauseBaconTimer() {
        if (!baconRunning) return;
        baconRunning = false;
        if (baconTimer != null) baconTimer.cancel();
    }

    private void resetBaconTimer() {
        if (baconTimer != null) baconTimer.cancel();
        baconRunning = false;
        baconMillisLeft = BACON_INITIAL;
        timerBaconText.setTextColor(defaultTimerColor);
        updateTimerText(timerBaconText, baconMillisLeft);
    }

    private void startBorschtTimer() {
        borschtRunning = true;
        timerBorschtText.setTextColor(defaultTimerColor);
        borschtTimer = new CountDownTimer(borschtMillisLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                borschtMillisLeft = millisUntilFinished;
                updateTimerText(timerBorschtText, borschtMillisLeft);
                checkLastMinute(timerBorschtText, borschtMillisLeft);
            }

            @Override
            public void onFinish() {
                borschtRunning = false;
                borschtMillisLeft = 0;
                updateTimerText(timerBorschtText, borschtMillisLeft);
                timerBorschtText.setTextColor(defaultTimerColor);
                if (borschtSound != null) borschtSound.start();
            }
        }.start();
    }

    private void pauseBorschtTimer() {
        if (!borschtRunning) return;
        borschtRunning = false;
        if (borschtTimer != null) borschtTimer.cancel();
    }

    private void resetBorschtTimer() {
        if (borschtTimer != null) borschtTimer.cancel();
        borschtRunning = false;
        borschtMillisLeft = BORSCHT_INITIAL;
        timerBorschtText.setTextColor(defaultTimerColor);
        updateTimerText(timerBorschtText, borschtMillisLeft);
    }

    private void updateTimerText(TextView tv, long millis) {
        long minutes = (millis / 1000) / 60;
        long seconds = (millis / 1000) % 60;
        String time = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tv.setText(time);
    }

    private void checkLastMinute(TextView tv, long millis) {
        if (millis <= 60_000 && millis > 0) {
            tv.setTextColor(Color.RED);
        } else {
            tv.setTextColor(defaultTimerColor);
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

    @Override
    protected void onStop() {
        super.onStop();

        if (eggsTimer != null) eggsTimer.cancel();
        if (baconTimer != null) baconTimer.cancel();
        if (borschtTimer != null) borschtTimer.cancel();

        if (eggsSound != null) {
            eggsSound.release();
            eggsSound = null;
        }
        if (baconSound != null) {
            baconSound.release();
            baconSound = null;
        }
        if (borschtSound != null) {
            borschtSound.release();
            borschtSound = null;
        }
    }
}
