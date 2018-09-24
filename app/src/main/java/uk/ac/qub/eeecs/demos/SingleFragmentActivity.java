package uk.ac.qub.eeecs.demos;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * Base class from which all demos will derive - offering a game configured
 * activity holding a single fragment.
 */
public abstract class SingleFragmentActivity extends Activity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Define the type of activity we want created - no title, full screen
		// and keep the screen on whilst it is visible. This needs to be
		// completed before any components
		// are inflated.
		Window window = getWindow();
		window.requestFeature(Window.FEATURE_NO_TITLE);
		window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// Set the content view up to hold a single fragment
		setContentView(R.layout.activity_fragment);

		// Add whatever fragment is appropriate for the derived call - calling
		// the overloaded createFragment() method to retrieve the fragment.
		FragmentManager fm = getFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.activity_fragment_id);

		if (fragment == null) {
			fragment = createFragment();
			fm.beginTransaction().add(R.id.activity_fragment_id, fragment)
					.commit();
		}
	}

	/**
	 * Get the fragment for this particular activity
	 * 
	 * @return Fragment for this activity
	 */
	public abstract Fragment createFragment();
}
