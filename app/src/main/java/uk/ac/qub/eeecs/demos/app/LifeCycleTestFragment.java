package uk.ac.qub.eeecs.demos.app;

import uk.ac.qub.eeecs.demos.R;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LifeCycleTestFragment extends Fragment {

	/**
	 * Declared to be static to count the number of created fragments
	 */
	private static int sFragmentCounter = 0;

	// ////////////////////////////////////////////////////////////////////////
	// Create and Destroy
	// ////////////////////////////////////////////////////////////////////////

	@Override
	public void onCreate(Bundle savedInstanceState) {
		sFragmentCounter++;
		notify("Fragment created.");
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		notify("Fragment view created.");

		// Inflate the view
		View view = inflater.inflate(R.layout.life_cycle_test_fragment,
				container, false);

		// The log method assumes the view has already been set - which it won't
		// be until we return from this method, so display the output text as
		// part of the default inflate process
		TextView text = (TextView) view
				.findViewById(R.id.life_cycle_test_textview);
		text.setText(eventBuilder.toString());

		return view;
	}

	@Override
	public void onDestroy() {
		notify("Fragment destroyed.");
		super.onDestroy();
	}

	// ////////////////////////////////////////////////////////////////////////
	// Start and Stop
	// ////////////////////////////////////////////////////////////////////////

	@Override
	public void onStart() {
		notify("\tFragment started.");
		super.onStart();
	}

	@Override
	public void onStop() {
		notify("\tFragment stopped.");
		super.onStop();
	}

	// ////////////////////////////////////////////////////////////////////////
	// Resume and Pause
	// ////////////////////////////////////////////////////////////////////////

	@Override
	public void onPause() {
		notify("\t\tFragment paused.");
		super.onPause();
	}

	@Override
	public void onResume() {
		notify("\t\tFragment resumed.");
		super.onResume();
	}

	// ////////////////////////////////////////////////////////////////////////
	// Notify Related
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Display the event message.
	 * 
	 * @param event
	 *            String formatted event message
	 */
	private void notify(String event) {
		LifeCycleTestFragment.notify(getActivity(), this, event, false);
	}

	/**
	 * String builder used to construct the output messages. The string builder
	 * is declared as static to enable it to accumulate messages across activity
	 * and fragment instances.
	 */
	private static StringBuilder eventBuilder = new StringBuilder();

	/**
	 * Display the event message. The fragment method is declared as static to
	 * enable it to receive messages across activity and fragment instances.
	 * 
	 * @param activity
	 *            Activity associated with the message
	 * @param fragment
	 *            Fragment on which the message should be displayed
	 * @param event
	 *            String event message to add
	 * @param activityMessage
	 *            True if the event message was received from the activity,
	 *            false if the event message was received from the fragment.
	 */
	public static void notify(Activity activity, Fragment fragment,
			String event, boolean activityMessage) {

		// Add the event to the string builder
		eventBuilder.append(String.format("%-30s", event));
		eventBuilder.append(" : ");
		eventBuilder.append(activityMessage ? activity.toString() : fragment
				.toString());
		eventBuilder.append("\n");

		// Log the event details
		Log.d(activity.getResources().getString(R.string.LOG_TAG), event);

		// If a view exists, then display the event
		if (fragment != null) {
			View view = fragment.getView();
			if (view != null) {
				TextView text = (TextView) view
						.findViewById(R.id.life_cycle_test_textview);
				text.setText(eventBuilder.toString());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#toString()
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + sFragmentCounter;
	}
}