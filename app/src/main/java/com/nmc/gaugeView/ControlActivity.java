package com.nmc.gaugeView;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nmc.aqi.R;
import com.nmc.aqi.SpeedView;

import java.util.Locale;

public class ControlActivity extends AppCompatActivity {

    SpeedView speedView;
    SeekBar seekBar;
    EditText maxSpeed, speedometerWidth;
    CheckBox withTremble;
    TextView textSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        speedView = findViewById(R.id.awesomeSpeedometer);
        seekBar = findViewById(R.id.seekBar);
        textSpeed = findViewById(R.id.textSpeed);
        maxSpeed = findViewById(R.id.maxSpeed);
        speedometerWidth = findViewById(R.id.speedometerWidth);
        withTremble = findViewById(R.id.withTremble);

        withTremble.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                speedView.setWithTremble(isChecked);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textSpeed.setText(String.format(Locale.getDefault(), "%d", progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        speedView.speedTo(50);
    }

    public void setSpeed(View view) {
        speedView.speedTo(seekBar.getProgress());
    }

    public void setMaxSpeed(View view) {
        try {
            int max = Integer.parseInt(maxSpeed.getText().toString());
            seekBar.setMax(max);
            speedView.setMaxSpeed(max);
        }
        catch (Exception e) {
            maxSpeed.setError(e.getMessage());
        }
    }

    public void setSpeedometerWidth(View view) {
        try {
            float width = Float.parseFloat(speedometerWidth.getText().toString());
            speedView.setSpeedometerWidth(width);
        }
        catch (Exception e) {
            speedometerWidth.setError(e.getMessage());
        }
    }

    public void setSpeedTextSize(View view) {
        EditText speedTextSize = findViewById(R.id.speedTextSize);
        try {
            float size = Float.parseFloat(speedTextSize.getText().toString());
            speedView.setSpeedTextSize(size);
        }
        catch (Exception e) {
            speedTextSize.setError(e.getMessage());
        }
    }

    public void setIndicatorColor(View view) {
        EditText indicatorColor = findViewById(R.id.indicatorColor);
        try{
            speedView.setIndicatorColor(Color.parseColor(indicatorColor.getText().toString()));
        } catch (Exception e) {
            indicatorColor.setError(e.getMessage());
        }
    }

    public void setCenterCircleColor(View view) {
        EditText centerCircleColor = findViewById(R.id.centerCircleColor);
        try{
            speedView.setCenterCircleColor(Color.parseColor(centerCircleColor.getText().toString()));
        } catch (Exception e) {
            centerCircleColor.setError(e.getMessage());
        }
    }

    public void setGoodLevelColor(View view) {
        EditText goodLevelColor = findViewById(R.id.goodLevelColor);
        try{
            speedView.setGoodLevelColor(Color.parseColor(goodLevelColor.getText().toString()));
        } catch (Exception e) {
            goodLevelColor.setError(e.getMessage());
        }
    }
    public void setSatisfactoryLevelColor(View view) {
        EditText satisfactoryLevelColor = findViewById(R.id.satisfactoryLevelColor);
        try{
            speedView.setSatisfactoryLevelColor(Color.parseColor(satisfactoryLevelColor.getText().toString()));
        } catch (Exception e) {
            satisfactoryLevelColor.setError(e.getMessage());
        }
    }

    public void setModerateLevelColor(View view) {
        EditText moderateLevelColor = findViewById(R.id.moderateLevelColor);
        try{
            speedView.setModerateLevelColor(Color.parseColor(moderateLevelColor.getText().toString()));
        } catch (Exception e) {
            moderateLevelColor.setError(e.getMessage());
        }
    }

    public void setPoorLevelColor(View view) {
        EditText poorLevelColor = findViewById(R.id.poorLevelColor);
        try{
            speedView.setPoorLevelColor(Color.parseColor(poorLevelColor.getText().toString()));
        } catch (Exception e) {
            poorLevelColor.setError(e.getMessage());
        }
    }

    public void setVeryPoorLevelColor(View view) {
        EditText veryPoorLevelColor = findViewById(R.id.veryPoorLevelColor);
        try{
            speedView.setVeryPoorLevelColor(Color.parseColor(veryPoorLevelColor.getText().toString()));
        } catch (Exception e) {
            veryPoorLevelColor.setError(e.getMessage());
        }
    }

    public void setSevereLevelColor(View view) {
        EditText severeLevelColor = findViewById(R.id.severeLevelColor);
        try{
            speedView.setSevereLevelColor(Color.parseColor(severeLevelColor.getText().toString()));
        } catch (Exception e) {
            severeLevelColor.setError(e.getMessage());
        }
    }
}
