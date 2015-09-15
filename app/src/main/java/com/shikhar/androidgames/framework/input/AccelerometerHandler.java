package com.shikhar.androidgames.framework.input;

import android.content.Context;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class AccelerometerHandler implements SensorEventListener {
	float accelX;
	float accelY;
	float accelZ;
	 private  SensorManager mSensorManager;
     private  Sensor mAccelerometer;
     private boolean enabled=false;

	public AccelerometerHandler(Context context) {
		mSensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		if (mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
			 mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			if (mAccelerometer!=null) enabled=true;
		}
        
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// nothing to do here
	}

	public void onSensorChanged(SensorEvent event) {
		accelX = event.values[0];
		accelY = event.values[1];
		accelZ = event.values[2];
	}

	public float getAccelX() {
		return accelX;
	}

	public float getAccelY() {
		return accelY;
	}

	public float getAccelZ() {
		return accelZ;
	}
	
	protected void registerListener() {
		enabled=mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

	protected void unRegisterListener() {
	   mSensorManager.unregisterListener(this);
	   enabled=false;
	}

	public boolean isEnabled() {
		return enabled;
	}

}