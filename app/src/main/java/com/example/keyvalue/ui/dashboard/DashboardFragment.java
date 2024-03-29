package com.example.keyvalue.ui.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.keyvalue.R;

import org.w3c.dom.Text;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class DashboardFragment extends Fragment {
    public int START_TIME_IN_MILLIS = 5000;
    private TextView timer;
    private TextView highScoreView;
    private DashboardViewModel dashboardViewModel;
    private CountDownTimer countDownTimer;
    private boolean timerRunning = false;

    private long timeLeftInMillis = START_TIME_IN_MILLIS;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) { textView.setText(s);
            }
        });

        highScoreView = root.findViewById(R.id.high_score_view);
        timer = root.findViewById(R.id.CountDown);
        Button button = root.findViewById(R.id.mash_button);

        updateHighScore();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                int defaultValue = getResources().getInteger(R.integer.high_score_number);
                int timesPressed = sharedPref.getInt(getString(R.string.button_mash), defaultValue);
                SharedPreferences.Editor editor = sharedPref.edit();

                if(!timerRunning) {
                    startTimer();
                }

                updateHighScore();
                int newTimesPressed = timesPressed + 1;



                // be amazing, do something
                textView.setText("Button has been pressed " + Integer.toString(newTimesPressed) + " times!");

                if (newTimesPressed > sharedPref.getInt(getString(R.string.high_score),defaultValue)) {
                    editor.putInt(getString(R.string.button_mash), newTimesPressed);
                    editor.apply();
                    editor.putInt(getString(R.string.high_score), newTimesPressed);
                    editor.apply();
                } else {
                    editor.putInt(getString(R.string.button_mash), newTimesPressed);
                    editor.apply();
                }
            }
        });

        return root;
    }

    private void startTimer() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                timeLeftInMillis = START_TIME_IN_MILLIS;
                updateCountDownText();
                editor.putInt(getString(R.string.button_mash), 0);
                editor.apply();
            }
        }.start();

        timerRunning = true;
    }

    private void updateCountDownText() {

        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%2d", seconds);
        timer.setText(timeLeftFormatted);

    }

    private void updateHighScore(){
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        int defaultValue = getResources().getInteger(R.integer.high_score_number);

        highScoreView.setText("High Score Is " + Integer.toString(sharedPref.getInt(getString(R.string.high_score), defaultValue)) + "!");
    }
}