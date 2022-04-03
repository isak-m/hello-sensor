package com.example.hellosensor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
    private TextView tiltTV;
    private float[] accReadings = new float[3];
    private static final float ALPHA = 0.5f;
    private ConstraintLayout bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        xTV = (TextView) findViewById(R.id.textView2);
        yTV = (TextView) findViewById(R.id.textView3);
        zTV = (TextView) findViewById(R.id.textView4);
        tiltTV = (TextView) findViewById(R.id.textView6);
        bg = (ConstraintLayout) findViewById(R.id.background);
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
        accReadings = applyLowPassFilter(event.values, accReadings);
        xTV.setText(String.format("%.2f", accReadings[0]));
        yTV.setText(String.format("%.2f", accReadings[1]));
        zTV.setText(String.format("%.2f", accReadings[2]));
        setTilt(accReadings);
    }

    private void setTilt(float[] accReadings){
        StringBuilder sb = new StringBuilder("");
        if ( accReadings[2] > -3){ // rättvänd
            sb.append("Rättvänd");
            // Sätta färg på bakgrunden: https://www.geeksforgeeks.org/how-to-change-the-background-color-after-clicking-the-button-in-android/
            bg.setBackgroundColor(getResources().getColor(R.color.white));
            if (accReadings[0] > 1) {
                // sätta färg på text: https://stackoverflow.com/questions/7378636/setting-background-colour-of-android-layout-element
                tiltTV.setTextColor(getResources().getColor(R.color.red));
                sb.append(", åt vänster");
            } else if (accReadings[0] < -1){
                tiltTV.setTextColor(getResources().getColor(R.color.blue));
                sb.append(", åt höger");
            } else {
                tiltTV.setTextColor(getResources().getColor(R.color.teal_700));
                sb.append(", rakt fram");
            }
        } else {
            sb.append("Upp och ner");
            bg.setBackgroundColor(getResources().getColor(R.color.grey));
            if (accReadings[0] > 1) {
                tiltTV.setTextColor(getResources().getColor(R.color.blue));
                sb.append(", åt höger");
            } else if (accReadings[0] < -1){
                tiltTV.setTextColor(getResources().getColor(R.color.red));
                sb.append(", åt vänster");
            } else {
                tiltTV.setTextColor(getResources().getColor(R.color.teal_700));
                sb.append(", rakt fram");
            }
        }
        tiltTV.setText(sb.toString());
    }

    // https://stackoverflow.com/questions/27846604/how-to-get-smooth-orientation-data-in-android
    private float[] applyLowPassFilter(float[] input, float[] output) {
        if ( output == null ) return input;

        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}