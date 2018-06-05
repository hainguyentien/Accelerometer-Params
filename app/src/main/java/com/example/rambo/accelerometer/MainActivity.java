package com.example.rambo.accelerometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    Sensor sensorAccelerometer;

    private long lastUpdate = 0;
    private float last_x,last_y,last_Acceleration;
    private static final int SHAKE_THRESHOLD = 600;
    private TextView params;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        params = findViewById(R.id.txtspeed);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER){

            /*
                In this example, alpha is calculated as t / (t + dT),
                where t is the low-pass filter's time-constant and
                dT is the event delivery rate.
             */
            final float alpha = (float) 0.8;

            /*
                If you push the device on the left side (so it moves to the right), the x acceleration value is positive.
             */
            float x = alpha * last_x + (1 - alpha) * event.values[0];

            /*
                If you push the device on the bottom (so it moves away from you), the y acceleration value is positive.
             */
            float y = alpha * last_y + (1 - alpha) * event.values[1];

            /*
                Acceleration value include Gravity approximately equal 9.81 m/s2
                If you push the device toward the sky with an acceleration of A m/s2,
                the acceleration value is equal to A + 9.81,
                which corresponds to the acceleration of the device (+A m/s2) minus the force of gravity (-9.81 m/s2)
             */

            float Acceleration = alpha * last_Acceleration + (1 - alpha) * event.values[2];

            /*
                High-pass filter (Exactly values)
             */

            float a = event.values[0] - x;
            float b = event.values[1] - y;
            float c = (float) (event.values[2] - Acceleration + 9.81);

            long curTime = System.currentTimeMillis();

            if (curTime - lastUpdate > 100){
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                /*
                    Speed value : When you move your device, this value will calc your device speed
                    If your device is freezing, this value approximately equal 0
                 */

                float speed = Math.abs(x + y + Acceleration - last_x - last_Acceleration - last_y)/diffTime * 10000;

                if (speed > SHAKE_THRESHOLD){

                }
                params.setText("x = " + x +
                        "\ny = " + y +
                        "\nAcceleration = " + Acceleration +
                        "\nx_HP = " + a +
                        "\ny_HP = " + b +
                        "\nAcceleration_HP = " + c +
                        "\nLast_x = " + last_x +
                        "\nLast_y = " + last_y +
                        "\nLast_Acceleration = " + last_Acceleration +
                        "\nSpeed = " + speed);
                last_x = x;
                last_y = y;
                last_Acceleration = Acceleration;

            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorAccelerometer,SensorManager.SENSOR_DELAY_NORMAL);
    }
}
