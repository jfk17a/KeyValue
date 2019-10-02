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

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class DashboardFragment extends Fragment {
    public int START_TIME_IN_MILLIS = 30000;
    private TextView timer;
    private DashboardViewModel dashboardViewModel;
    private CountDownTimer countDownTimer;

    private boolean timerRunning;

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
        timer = root.findViewById(R.id.CountDown);
        Button button = root.findViewById(R.id.mash_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                int defaultValue = getResources().getInteger(R.integer.saved_times_pressed_default_key);
                int timesPressed = sharedPref.getInt(getString(R.string.saved_button_press_count_key), defaultValue);

                int newTimesPressed = timesPressed + 1;

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.saved_button_press_count_key), newTimesPressed);
                editor.apply();

                // be amazing, do something
                textView.setText("Button has been pressed " + sharedPref.getInt(getString(R.string.saved_button_press_count_key), getResources().getInteger(R.integer.saved_times_pressed_default_key))+ " times!");
            }
        });
        return root;
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                //buttonStartPause.setText("Start");
                //buttonStartPause.setVisibility(View.INVISIBLE);
                //buttonReset.setVisibility(View.VISIBLE);
            }
        }.start();

        timerRunning = true;
        //buttonStartPause.setText("pause");
        //buttonReset.setVisibility(View.INVISIBLE);
    }

    private void updateCountDownText() {
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d", seconds);

        timer.setText(timeLeftFormatted);
    }
}