package uk.ac.qub.eeecs.demos.audio;

import uk.ac.qub.eeecs.demos.SingleFragmentActivity;
import android.app.Fragment;

public class SoundPoolTest extends SingleFragmentActivity {

	/*
	 * Return the fragment to be set for this activity within the superclass'
	 * onCreate method
	 * 
	 * @see uk.ac.qub.eeecs.demos.SingleFragmentActivity#createFragment()
	 */

	@Override
	public Fragment createFragment() {
		return new SoundPoolTestFragment();
	}
}
