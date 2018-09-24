package uk.ac.qub.eeecs.demos;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Add the demo details fragment, listing all available demos
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new DemoDetailsFragment()).commit();
		}
	}
}
