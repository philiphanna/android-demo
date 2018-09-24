package uk.ac.qub.eeecs.demos.input;

import uk.ac.qub.eeecs.demos.R;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class SingleTouchTestFragment extends Fragment {

	/*
	 * (non-Javadoc)
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the view
		View view = inflater.inflate(R.layout.single_touch_test_fragment,
				container, false);

		// Get the 'touch' area and add a touch listener to it
		TextView touchArea = (TextView) view
				.findViewById(R.id.single_touch_test_event_toucharea);
		touchArea.setOnTouchListener(new SingleTouchListener());

		return view;
	}

	// ////////////////////////////////////////////////////////////////////////
	// Single Touch Event Handler
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Listener class that can support a single touch event (not that useful but
	 * it acts as a basis on which a multi-touch listener can be constructed).
	 */
	@SuppressLint("ClickableViewAccessibility")
	private class SingleTouchListener implements OnTouchListener {

		// ////////////////////////////////////////////////////////////////////
		// Touch Events
		// ////////////////////////////////////////////////////////////////////

		/**
		 * Remember the last motion event that occurred (used to determine if a
		 * move event has concluded and to limit the number of events that are
		 * output).
		 */
		private int mLastMotionEvent = MotionEvent.ACTION_CANCEL;

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.view.View.OnTouchListener#onTouch(android.view.View,
		 * android.view.MotionEvent)
		 */
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			// Extract the single-touch event type and location
			int eventType = event.getActionMasked();
			float x = event.getX();
			float y = event.getY();

			// Display the touch position
			displayTouchPosition(x, y);

			// If we have a change of touch event (down to move, move to up,
			// etc.) then display relevant information
			if (mLastMotionEvent != eventType) {

				switch (eventType) {
				case MotionEvent.ACTION_DOWN:
					displayEvent("Down", x, y);
					break;
				case MotionEvent.ACTION_MOVE:
					displayEvent("Move Start", x, y);
					break;
				case MotionEvent.ACTION_UP:
					if (mLastMotionEvent == MotionEvent.ACTION_MOVE) {
						displayEvent("Move End", x, y);
					}
					displayEvent("Up", x, y);
					break;
				}
			}

			// Remember the last event type that occurred
			mLastMotionEvent = eventType;

			return true;
		}

		// ////////////////////////////////////////////////////////////////////
		// Touch Info Display
		// ////////////////////////////////////////////////////////////////////

		/**
		 * String builder to help put together the output strings
		 */
		private StringBuilder mEventDetails = new StringBuilder();

		/**
		 * Display the x and y touch location
		 * 
		 * @param x
		 *            Location of the touch event on the x axis
		 * @param y
		 *            Location of the touch event on the y axis
		 */
		private void displayTouchPosition(float x, float y) {
			TextView xLoc = (TextView) getView().findViewById(
					R.id.single_touch_test_X_loc);
			xLoc.setText(String.format("%.1f", x));
			TextView yLoc = (TextView) getView().findViewById(
					R.id.single_touch_test_Y_loc);
			yLoc.setText(String.format("%.1f", y));
		}

		/**
		 * Display details of the event that has occurred
		 * 
		 * @param event
		 *            Event descriptor
		 * @param x
		 *            Location of event along x-axis
		 * @param y
		 *            Location of event along y-axis
		 */
		private void displayEvent(String event, float x, float y) {

			mEventDetails.append(String.format("%-15s", event));
			mEventDetails.append(" [");
			mEventDetails.append(String.format("%.1f", x));
			mEventDetails.append(",");
			mEventDetails.append(String.format("%.1f", y));
			mEventDetails.append("]\n");

			TextView eventTextView = (TextView) getView().findViewById(
					R.id.single_touch_test_event_textview);
			eventTextView.setText(mEventDetails.toString());
		}
	}
}