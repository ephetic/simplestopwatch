package omegacentauri.mobi.simplestopwatch;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

public class StopWatch extends Activity {
    SharedPreferences options;
    long baseTime = 0;
    long pausedTime = 0;
    boolean active = false;
    boolean paused = false;
    boolean chronoStarted = false;
    private TextView chrono1 = null;
    private TextView chrono2 = null;
    private MyChrono stopwatch;
    private Button resetButton;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        options = PreferenceManager.getDefaultSharedPreferences(this);
        MyChrono.detectBoot(options, "");
        setContentView(R.layout.activity_stop_watch);
        chrono1 = (TextView)findViewById(R.id.chrono1);
        chrono2 = (TextView)findViewById(R.id.chrono2);
        resetButton = (Button)findViewById(R.id.reset);
        startButton = (Button)findViewById(R.id.start);
        stopwatch = new MyChrono(this, chrono1, chrono2, (TextView)findViewById(R.id.fraction));
    }

    @Override
    protected void onResume() {
        super.onResume();
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.v("chrono", "landscape");
            chrono2.setVisibility(View.GONE);
        } else {
            Log.v("chrono", "portrait");
            chrono2.setVisibility(View.VISIBLE);
        }
        chrono1.post(new Runnable() {
            @Override
            public void run() {
                stopwatch.updateViews();
            }
        });

        Log.v("chrono", "onResume");

        stopwatch.restore(options, "");
        stopwatch.updateViews();
        updateButtons();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.v("chrono", "onConfChanged");
        super.onConfigurationChanged(newConfig);
        stopwatch.updateViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopwatch.save(options, "");
        stopwatch.stopUpdating();
    }

    void updateButtons() {
        if (!stopwatch.active) {
            startButton.setText("Start");
            resetButton.setVisibility(View.INVISIBLE);
        }
        else {
            if (stopwatch.paused) {
                startButton.setText("Continue");
                resetButton.setVisibility(View.VISIBLE);
            } else {
                startButton.setText("Stop");
                resetButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    void pressReset() {
        stopwatch.resetButton();
        updateButtons();
    }

    void pressStart() {
        stopwatch.startStopButton();
        updateButtons();
    }

    public void onButtonStart(View v) {
        pressStart();
    }

    public void onButtonReset(View v) {
        pressReset();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_DOWN)
            return false;
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP /*|| keyCode == KeyEvent.KEYCODE_A*/) {
            pressStart();
            return true;
        }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN /*|| keyCode == KeyEvent.KEYCODE_C*/) {
            pressReset();
            return true;
        }
        return false;
    }
}
