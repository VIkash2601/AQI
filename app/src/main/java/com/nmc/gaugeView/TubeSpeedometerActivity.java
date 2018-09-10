package com.nmc.gaugeView;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nmc.aqi.R;
import com.nmc.aqi.Gauge;
import com.nmc.aqi.TubeSpeedometer;

import java.util.Locale;

public class TubeSpeedometerActivity extends AppCompatActivity {

    TubeSpeedometer tubeSpeedometer;
    SeekBar seekBar;
    Button ok;
    TextView textSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tube_speedometer);

        tubeSpeedometer = findViewById(R.id.speedometer);
        seekBar = findViewById(R.id.seekBar);
        ok = findViewById(R.id.ok);
        textSpeed = findViewById(R.id.textSpeed);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tubeSpeedometer.setSpeedTextPosition(Gauge.Position.IN_CENTER_OF_VIEW);
        tubeSpeedometer.setTickTextFormat(Gauge.INTEGER_FORMAT);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Send the data to Gauge*/
                //tubeSpeedometer.speedTo(seekBar.getProgress());

                //To stop movement of tick
                tubeSpeedometer.setWithTremble(false);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textSpeed.setText(String.format(Locale.getDefault(), "%d", progress));
                tubeSpeedometer.speedTo(seekBar.getProgress());
                //To stop movement of tick
                tubeSpeedometer.setWithTremble(false);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
