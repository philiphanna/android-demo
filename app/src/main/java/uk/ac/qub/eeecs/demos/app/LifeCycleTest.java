package uk.ac.qub.eeecs.demos.app;

import uk.ac.qub.eeecs.demos.R;
import uk.ac.qub.eeecs.demos.SingleFragmentActivity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

public class LifeCycleTest extends SingleFragmentActivity {

	/**
	 * Declared to be static to count the number of created activities
	 */
	private static int sActivityCounter = 0;

	/*
	 * Return the fragment to be set for this activity within the superclass'
	 * onCreate method
	 * 
	 * @see uk.ac.qub.eeecs.demos.SingleFragmentActivity#createFragment()
	 */
	@Override
	public Fragment createFragment() {
		return new LifeCycleTestFragment();
	}

	// ////////////////////////////////////////////////////////////////////////
	// Create and Destroy
	// ////////////////////////////////////////////////////////////////////////

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		sActivityCounter++;
		notify("Activity created.");
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onDestroy() {
		notify("Activity destroyed.");
		super.onDestroy();
	}

	// ////////////////////////////////////////////////////////////////////////
	// Start and Stop
	// ////////////////////////////////////////////////////////////////////////

	@Override
	protected void onStart() {
		notify("\tActivity started.");
		super.onStart();
	}

	@Override
	protected void onStop() {
		notify("\tActivity stopped.");
		super.onStop();
	}

	// ////////////////////////////////////////////////////////////////////////
	// Resume and Pause
	// ////////////////////////////////////////////////////////////////////////

	@Override
	protected void onResume() {
		notify("\t\tActivity resumed.");
		super.onResume();
	}

	@Override
	protected void onPause() {
		notify("\t\tActivity paused.");
		super.onPause();
	}

	// ////////////////////////////////////////////////////////////////////////
	// Notify Related
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Display the event message on the fragment associated with this activity
	 * 
	 * @param event
	 *            String formatted event details
	 */
	private void notify(String event) {
		FragmentManager fm = getFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.activity_fragment_id);
		LifeCycleTestFragment.notify(this, fragment, event, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + sActivityCounter;
	}
}
