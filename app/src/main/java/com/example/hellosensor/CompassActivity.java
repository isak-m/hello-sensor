package com.example.hellosensor;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;
import android.widget.ImageView;

public class CompassActivity extends AppCompatActivity implements SensorEventListener {

    private TextView dTV;
    private ImageView cIV;
    private SensorManager mSensorManager;
    private Sensor mMagnetic;
    private Sensor mAccelerometer;
    private float[] accReadings = new float[3];
    private float[] magReadings = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] orientationAngles = new float[3];
    private float startDegrees = 0f;
    private float currentDegrees = 0f;
    private double[] recentReadings = new double[5];
    private static final float ALPHA = 0.1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        dTV = (TextView) findViewById(R.id.tvDegrees);
        cIV = (ImageView) findViewById(R.id.ivCompass);
    }
    @Override
    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // code for system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mMagnetic,
                SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event == null){
            return;
        }
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            //for(int i = 0; i < 3; i++){
            //    accReadings[i] = event.values[i];
            //}
            accReadings = applyLowPassFilter(event.values, accReadings);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            //for(int i = 0; i < 3; i++){
            //    magReadings[i] = event.values[i];
            //}
            magReadings = applyLowPassFilter(event.values, magReadings);
        }
        updateOrientationAngles();
    }


    private String getDirection(double angle){
        StringBuilder direction = new StringBuilder();

        if (angle >= 350 || angle <= 10){
            direction.append("N");
        }
        if (angle < 350 && angle > 280) {
            direction.append("NW");
        }
        if (angle <= 280 && angle > 260) {
            direction.append("W");
        }
        if (angle <= 260 && angle > 190) {
            direction.append("SW");
        }
        if (angle <= 190 && angle > 170) {
            direction.append("N");
        }
        if (angle <= 170 && angle > 100) {
            direction.append("SE");
        }
        if (angle <= 100 && angle > 80) {
            direction.append("E");
        }
        if (angle <= 80 && angle > 10) {
            direction.append("NE");
        }
        return direction.toString();
    }

    private void updateOrientationAngles(){
        mSensorManager.getRotationMatrix(rotationMatrix, null, accReadings, magReadings);
        float[] orientation = SensorManager.getOrientation(rotationMatrix, orientationAngles);
        double degrees = (Math.toDegrees((double)orientation[0]) + 360.0) % 360.0;
        double angle = Math.round(degrees * 100) / 100;
        String direction = getDirection(degrees);
        dTV.setText(angle + " degrees " + direction);
        cIV.setRotation((float) angle * -1);
    }

    /**
     * Moving average filter - jobbar med grader, men borde jobba med radianer eller något så att den inte ballar ur vid när den korsar 0/360 grader strecket
     * **/
    private double maFilter(double degrees){
        double sin = 0;
        double cos = 0;
        for(int i = recentReadings.length - 1; i > 0; i--){
            // update recent recentReadings
            recentReadings[i] = recentReadings[i-1];
        }
        recentReadings[0] = degrees;
        for(double ang : recentReadings){
            //sum += ang;
            sin += Math.sin(ang);
            cos += Math.cos(ang);
        }
        double avSin = sin / recentReadings.length;
        double avCos = cos / recentReadings.length;
        double average = Math.atan(sin/cos);
        return average;
    }

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