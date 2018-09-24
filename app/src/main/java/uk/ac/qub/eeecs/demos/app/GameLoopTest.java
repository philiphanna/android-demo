package uk.ac.qub.eeecs.demos.app;

import uk.ac.qub.eeecs.demos.SingleFragmentActivity;
import android.app.Fragment;

public class GameLoopTest extends SingleFragmentActivity {

	/*
	 * Return the fragment to be set for this activity within the superclass'
	 * onCreate method
	 * 
	 * @see uk.ac.qub.eeecs.demos.SingleFragmentActivity#createFragment()
	 */
	@Override
	public Fragment createFragment() {
		return new GameLoopTestFragment();
	}
}
