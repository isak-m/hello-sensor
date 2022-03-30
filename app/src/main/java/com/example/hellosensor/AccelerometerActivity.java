package com.example.hellosensor;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.widget.TextView;
import android.os.Bundle;

public class AccelerometerActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private TextView xTV;
    private TextView yTV;
    private TextView zTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        xTV = (TextView) findViewById(R.id.textView2);
        yTV = (TextView) findViewById(R.id.textView3);
        zTV = (TextView) findViewById(R.id.textView4);
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onSensorChanged(SensorEvent event) {
        xTV.setText(String.format("%.3f", event.values[0]));
        yTV.setText(String.format("%.3f", event.values[1]));
        zTV.setText(String.format("%.3f", event.values[2]));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}