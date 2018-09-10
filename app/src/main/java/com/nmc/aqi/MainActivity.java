package com.nmc.aqi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {

    //Button refreshData;
    TubeSpeedometer tubeSpeedometer;

    TextView temp_content,
            humidity_content,
            heatIndex_content,
            co_content,
            co2_content,
            pm_content,
            aqi_content;

    String mainData,
            temp_data,
            humidity_data,
            heatIndex_data,
            co_data,
            co2_data,
            pm_data,
            aqi_data;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Used for subscript, replace 'sub' with 'sup' for superscript
        ((TextView)findViewById(R.id.co2_gas)).setText(Html.fromHtml("CO<sub>2</sub> ="));
        ((TextView)findViewById(R.id.co2_gas)).setTextColor(R.color.co2Color);
        ((TextView)findViewById(R.id.co2)).setText(Html.fromHtml("CO<sub>2</sub>in"));
        ((TextView)findViewById(R.id.ug)).setText(Html.fromHtml("µg/m<sub>3</sub>"));
        ((TextView)findViewById(R.id.ug)).setTextColor(R.color.co2Color);

        temp_content = findViewById(R.id.temperature);
        humidity_content = findViewById(R.id.humidity);
        heatIndex_content = findViewById(R.id.heat_index);
        co_content = findViewById(R.id.co);
        co2_content = findViewById(R.id.co2);
        pm_content = findViewById(R.id.pm);
        aqi_content = findViewById(R.id.aqi);
        tubeSpeedometer = findViewById(R.id.speedometer);

         Intent intentBundle = getIntent();

         Bundle bundle = intentBundle.getExtras();

        if (bundle != null) {

            //mGPUImage = new GPUImage(context);
            mainData = bundle.getString("mainData");

        //List<String> dataList = Arrays.asList(mainData.split(","));

        String[] dataList = mainData.split(",");

        temp_data = dataList[0];
        /*humidity_data = dataList[1];
        heatIndex_data = dataList[2];
        co_data = dataList[3];
        co2_data = dataList[4];
        pm_data = dataList[5];
        aqi_data = dataList[6];*/

        temp_content.setText(temp_data);

        temp_content.setHintTextColor(R.color.Black);
        humidity_content.setText(humidity_data);
        heatIndex_content.setText(heatIndex_data);
        co_content.setText(co_data);
        co2_content.setText(co2_data);
        pm_content.setText(pm_data);
        aqi_content.setText(aqi_data);

         /*Send data to Gauge for AQI Level*/
        float aqiData = Float.parseFloat(temp_data);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tubeSpeedometer.setSpeedTextPosition(Gauge.Position.IN_CENTER_OF_VIEW);
        tubeSpeedometer.setTickTextFormat(Gauge.INTEGER_FORMAT);
        tubeSpeedometer.speedTo(aqiData);
        //To stop movement of tick
        tubeSpeedometer.setWithTremble(false);
        }
    }
}