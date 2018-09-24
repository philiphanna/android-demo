package uk.ac.qub.eeecs.demos;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DemoDetailsFragment extends ListFragment {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup the available demos
		defineAndAddDemos();

		// Add the demos to the list to be displayed
		setListAdapter(new DemoDetailsAdapter(getActivity(), 0,
				mDemoManager.getDemoDetails()));
	}

	/**
	 * Adapter class for displaying details of each demo
	 */
	private class DemoDetailsAdapter extends ArrayAdapter<DemoDetails> {

		/**
		 * Create a new demo adapter
		 * 
		 * @param context
		 *            Context for the adapter
		 * @param resource
		 *            Resource ID
		 * @param objects
		 *            List of demos
		 */
		public DemoDetailsAdapter(Context context, int resource,
				List<DemoDetails> objects) {
			super(context, resource, objects);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			// Inflate a simple list item view if needed
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						android.R.layout.simple_list_item_1, null);
			}

			// Add in details for the demo at this position
			DemoDetails demoDetails = getItem(position);
			((TextView) convertView).setText(demoDetails.getName());

			return convertView;
		}
	}

	// ////////////////////////////////////////////////////////////////////////
	// Available demos
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Manager responsible for holding details of all available demos
	 */
	private DemoManager mDemoManager;

	/**
	 * Setup the available demos
	 */
	private void defineAndAddDemos() {

		mDemoManager = new DemoManager(getActivity());

		DemoDetails lifeCycleTest = new DemoDetails("Life Cycle Test",
				"LifeCycleTest", "app");
		mDemoManager.add(lifeCycleTest);
		DemoDetails gameLoopTest = new DemoDetails("Game Loop Test",
				"GameLoopTest", "app");
		mDemoManager.add(gameLoopTest);		
		DemoDetails singleTouchTest = new DemoDetails("Single Touch Test",
				"SingleTouchTest", "input");
		mDemoManager.add(singleTouchTest);
		DemoDetails multiTouchTest = new DemoDetails("Multi Touch Test",
				"MultiTouchTest", "input");
		mDemoManager.add(multiTouchTest);
		DemoDetails keyTest = new DemoDetails("Key Test", "KeyTest", "input");
		mDemoManager.add(keyTest);
		DemoDetails accelerometerTest = new DemoDetails(
				"Accelerometer and Compass Test", "AccelerometerTest", "input");
		mDemoManager.add(accelerometerTest);
		DemoDetails assetsTest = new DemoDetails("Assets Test", "AssetsTest",
				"storage");
		mDemoManager.add(assetsTest);
		DemoDetails sharedPreferencesTest = new DemoDetails(
				"Shared Preferences Test", "SharedPreferencesTest", "storage");
		mDemoManager.add(sharedPreferencesTest);
		DemoDetails soundPoolTest = new DemoDetails("Sound Pool Test",
				"SoundPoolTest", "audio");
		mDemoManager.add(soundPoolTest);
		DemoDetails mediaPlayerTest = new DemoDetails("Media Player Test",
				"MediaPlayerTest", "audio");
		mDemoManager.add(mediaPlayerTest);
		DemoDetails bitmapTest = new DemoDetails("Bitmap Test", "BitmapTest",
				"graphics");
		mDemoManager.add(bitmapTest);
		DemoDetails ribbonTest = new DemoDetails("Image Ribbon Test", "RibbonTest",
				"graphics");
		mDemoManager.add(ribbonTest);		
		DemoDetails animationTest = new DemoDetails("Animation Test", "AnimationTest",
				"graphics");
		mDemoManager.add(animationTest);				
		DemoDetails viewportTest = new DemoDetails("Viewport Test", "ViewportTest",
				"graphics");
		mDemoManager.add(viewportTest);		
		DemoDetails surfaceViewTest = new DemoDetails("Surface View Test",
				"SurfaceViewTest", "graphics");
		mDemoManager.add(surfaceViewTest);
		DemoDetails canvasThreadTest = new DemoDetails("Canvas Thread Test",
				"CanvasThreadTest", "graphics");
		mDemoManager.add(canvasThreadTest);
		DemoDetails particleSystemDemo = new DemoDetails("Particle System Demo",
				"ParticleSystemDemo", "particle");
		mDemoManager.add(particleSystemDemo);		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.ListFragment#onListItemClick(android.widget.ListView,
	 * android.view.View, int, long)
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		// Get details of the selected demo
		DemoDetails demoDetails = mDemoManager.getDemoDetails().get(position);

		try {
			// Load the demo class (which will be a Fragment subclass)
			Class<?> demoClass = Class.forName(mDemoManager.getContext()
					.getPackageName()
					+ "."
					+ demoDetails.getClassLocation()
					+ "." + demoDetails.getClassName());

			// Introduce the new fragment
			Intent intent = new Intent(getActivity(), demoClass);
			startActivity(intent);
		} catch (ClassNotFoundException e) {
			Log.e(getResources().getString(R.string.LOG_TAG), e.toString());
		}
	}
}
