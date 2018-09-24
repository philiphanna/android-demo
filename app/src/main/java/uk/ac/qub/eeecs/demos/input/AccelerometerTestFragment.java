package uk.ac.qub.eeecs.demos.input;

import uk.ac.qub.eeecs.demos.R;
import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AccelerometerTestFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.accelerometer_test_fragment,
				container, false);

		// Get the default accelerometer and megnetic field sensors
		SensorManager sensorManager = (SensorManager) getActivity()
				.getSystemService(Context.SENSOR_SERVICE);
		Sensor accelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Sensor magnetic = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		// Setup and register the accelerometer listener
		AccelerometerSensorEventListener accelerometerListener = new AccelerometerSensorEventListener();
		sensorManager.registerListener(accelerometerListener, accelerometer,
				SensorManager.SENSOR_DELAY_GAME);

		// Setup and register the compass listener
		CompassSensorEventListener compassListener = new CompassSensorEventListener();
		sensorManager.registerListener(compassListener, accelerometer,
				SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(compassListener, magnetic,
				SensorManager.SENSOR_DELAY_GAME);

		return view;
	}

	/**
	 * Simple accelerometer that returns the local gravity vectors and does not
	 * take into account the orientation of the device.
	 */
	private class AccelerometerSensorEventListener implements
			SensorEventListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.hardware.SensorEventListener#onSensorChanged(android.hardware
		 * .SensorEvent)
		 */
		@Override
		public void onSensorChanged(SensorEvent event) {

			TextView xTextView = (TextView) getView().findViewById(
					R.id.accelerometer_test_x_accel);
			xTextView.setText(Float.toString(event.values[0]));

			TextView yTextView = (TextView) getView().findViewById(
					R.id.accelerometer_test_y_accel);
			yTextView.setText(Float.toString(event.values[1]));

			TextView zTextView = (TextView) getView().findViewById(
					R.id.accelerometer_test_z_accel);
			zTextView.setText(Float.toString(event.values[2]));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.hardware.SensorEventListener#onAccuracyChanged(android.hardware
		 * .Sensor, int)
		 */
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	}

	/**
	 * Compass listener which uses the magnetic and gravity field vectors to
	 * provide a north facing (Azimuth) heading
	 */
	private class CompassSensorEventListener implements SensorEventListener {

		/**
		 * Vectors for holding the raw gravity and geomagnetic vectors
		 */
		private float[] mGravity = new float[3];
		private float[] mGeomagnetic = new float[3];

		/**
		 * Rotation and incline transform matrices formed using the gravity and
		 * geomagnetic vectors. Defined for reuse (avoiding array
		 * creation/deletion costs).
		 */
		private float[] mRotate = new float[9]; // Rotaton matrix
		private float[] mIncline = new float[9]; // Inclination matrix

		/**
		 * Orientation vector extracted from the rotation matrix. Defined for
		 * reuse (avoiding array creation/deletion costs).
		 */
		private float[] mOrientation = new float[3]; // Orientation matrix

		/**
		 * Azimuth component of the orientation vector
		 */
		private float mAzimuth;

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.hardware.SensorEventListener#onSensorChanged(android.hardware
		 * .SensorEvent)
		 */
		@Override
		public void onSensorChanged(SensorEvent event) {

			// Extract the current gravity vector
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				mGravity[0] = event.values[0];
				mGravity[1] = event.values[1];
				mGravity[2] = event.values[2];
			}

			// Extract the current magnetic vector
			if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				mGeomagnetic[0] = event.values[0];
				mGeomagnetic[1] = event.values[1];
				mGeomagnetic[2] = event.values[2];
			}

			// Using the gravity and geomagnetic vectors attempt to form
			// rotation and incline matricies
			boolean done = SensorManager.getRotationMatrix(mRotate, mIncline,
					mGravity, mGeomagnetic);
			if (done) {
				// Extract the orientation vector from the rotation matrix
				SensorManager.getOrientation(mRotate, mOrientation);

				// Extract and shape the Azimuth in a 0 to 2PI range.
				// No synchronisation is needed to the shared access
				// azimuth property as reads/writes to primitive
				// data are atomic.
				mAzimuth = mOrientation[0];
				float twoPI = (float) (2.0 * Math.PI);
				mAzimuth = (mAzimuth + twoPI) % twoPI;
			}

			TextView azimuthHeading = (TextView) getView().findViewById(
					R.id.accelerometer_test_compass);
			azimuthHeading.setText(Integer.toString((int) Math
					.toDegrees(mAzimuth)));
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	}
}
