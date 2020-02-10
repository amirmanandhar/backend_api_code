package com.kushal.qrparking.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Sensors {

    private SensorManager sensorManager;

    public Sensors(Context context, int sensorType, SensorEventListener sensorEventListener) {
        registerSensor(context,sensorType,sensorEventListener);
    }

    private void registerSensor(Context context, int sensorType, SensorEventListener sensorEventListener){
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(sensorType);
        sensorManager.registerListener(sensorEventListener,sensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    public SensorManager getSensorManager() {
        return sensorManager;
    }

    public void setSensorManager(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
    }
}
