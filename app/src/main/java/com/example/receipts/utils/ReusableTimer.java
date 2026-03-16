package com.example.receipts.utils;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.widget.TextView;

import java.util.Locale;

public class ReusableTimer {

    private CountDownTimer countDownTimer;
    private long millisLeft;
    private long initialMillis;
    private boolean running = false;
    private int defaultTextColor;
    private TextView textView;
    private Runnable onFinishCallback;

    public ReusableTimer(TextView textView, long initialMillis, Runnable onFinishCallback) {
        this.textView = textView;
        this.initialMillis = initialMillis;
        this.millisLeft = initialMillis;
        this.onFinishCallback = onFinishCallback;
        this.defaultTextColor = textView.getCurrentTextColor();
        updateText();
    }

    public void setTime(long millis) {
        if (running) return;
        this.initialMillis = millis;
        this.millisLeft = millis;
        textView.setTextColor(defaultTextColor);
        updateText();
    }

    public boolean isRunning() {
        return running;
    }

    public void start() {
        if (running) return;

        running = true;
        textView.setTextColor(defaultTextColor);

        countDownTimer = new CountDownTimer(millisLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisLeft = millisUntilFinished;
                updateText();
                if (millisLeft <= 60_000) {
                    textView.setTextColor(Color.RED);
                }
            }

            @Override
            public void onFinish() {
                running = false;
                millisLeft = 0;
                updateText();
                textView.setTextColor(defaultTextColor);
                if (onFinishCallback != null) {
                    onFinishCallback.run();
                }
            }
        }.start();
    }

    public void pause() {
        if (!running) return;
        running = false;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void reset() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        running = false;
        millisLeft = initialMillis;
        textView.setTextColor(defaultTextColor);
        updateText();
    }

    public void cancel() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        running = false;
    }

    private void updateText() {
        long minutes = (millisLeft / 1000) / 60;
        long seconds = (millisLeft / 1000) % 60;
        String time = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        textView.setText(time);
    }
}
