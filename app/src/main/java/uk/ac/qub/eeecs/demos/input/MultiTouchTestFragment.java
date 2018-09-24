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

public class MultiTouchTestFragment extends Fragment {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the view
		View view = inflater.inflate(R.layout.multi_touch_test_fragment,
				container, false);

		// Get the 'touch' area and add a touch listener to it
		TextView multiTouchTextArea = (TextView) view
				.findViewById(R.id.multi_touch_test_event_toucharea);
		multiTouchTextArea.setOnTouchListener(new MultiTouchListener());

		return view;
	}

	// ////////////////////////////////////////////////////////////////////////
	// Multi Touch Event Handler
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Listener class that can support multiple touch event.
	 */
	@SuppressLint("ClickableViewAccessibility")
	private class MultiTouchListener implements OnTouchListener {

		// ////////////////////////////////////////////////////////////////////
		// Touch Events
		// ////////////////////////////////////////////////////////////////////

		/**
		 * Maximum number of concurrent touch events that will be supported.
		 */
		private final int MAX_FINGERS = 20;

		/**
		 * Arrays to hold the last detection action type alongside the location
		 * of the relevant touch ID
		 */
		private int mLastActionType[] = new int[MAX_FINGERS];
		private float mEventX[] = new float[MAX_FINGERS];
		private float mEventY[] = new float[MAX_FINGERS];

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.view.View.OnTouchListener#onTouch(android.view.View,
		 * android.view.MotionEvent)
		 */
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			// Extract details of event that has occurred
			int eventType = event.getActionMasked();
			int eventPointerId = event.getPointerId(event.getActionIndex());

			// Update the event log as appropriate
			if (mLastActionType[eventPointerId] != eventType) {
				switch (eventType) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:
					displayEvent(eventPointerId, "Down");
					break;
				case MotionEvent.ACTION_MOVE:
					displayEvent(eventPointerId, "Move start");
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
					if (mLastActionType[eventPointerId] == MotionEvent.ACTION_MOVE) {
						displayEvent(eventPointerId, "Move end");
					}
					displayEvent(eventPointerId, "Up");
					mEventX[eventPointerId] = 0.0f;
					mEventY[eventPointerId] = 0.0f;
					break;
				}

				mLastActionType[eventPointerId] = eventType;
			}

			// Also extract the location of each occurring touch point
			// (which is wrapped up alongside the event that has occurred).
			for (int ptrIdx = 0; ptrIdx < event.getPointerCount(); ptrIdx++) {
				int pointerId = event.getPointerId(ptrIdx);
				mEventX[pointerId] = event.getX(ptrIdx);
				mEventY[pointerId] = event.getY(ptrIdx);
			}

			displayEventLocations();

			return true;
		}

		// ////////////////////////////////////////////////////////////////////
		// Touch Info Display
		// ////////////////////////////////////////////////////////////////////

		/**
		 * String builders to help put together the output strings
		 */
		private StringBuilder mEventDetails = new StringBuilder();
		private StringBuilder mLocationDetails = new StringBuilder();

		/**
		 * Display the x and y locations of all tracked touch events
		 */
		private void displayEventLocations() {
			// Reset the string buffer and add the x-touch locations
			mLocationDetails.setLength(0);
			for (int i = 0; i < MAX_FINGERS; i++) {
				mLocationDetails.append(mEventX[i]);
				mLocationDetails.append("\n");
			}

			// Display the x touch locations
			TextView xLocationsTextView = (TextView) getView().findViewById(
					R.id.multi_touch_test_X_locs);
			xLocationsTextView.setText(mLocationDetails.toString());

			// Reset the string buffer and add the y-touch locations
			mLocationDetails.setLength(0);
			for (int i = 0; i < MAX_FINGERS; i++) {
				mLocationDetails.append(mEventY[i]);
				mLocationDetails.append("\n");
			}

			// Display the y touch locations
			TextView yLocationsTextView = (TextView) getView().findViewById(
					R.id.multi_touch_test_Y_locs);
			yLocationsTextView.setText(mLocationDetails.toString());
		}

		/**
		 * Display details of the event that has occurred
		 * 
		 * @param pointerId
		 *            Pointer ID of the touch event
		 * @param event
		 *            Type of event which has occurred
		 */
		private void displayEvent(int pointerId, String event) {

			mEventDetails.append("Touch ID [");
			mEventDetails.append(pointerId);
			mEventDetails.append("] \t");
			mEventDetails.append(String.format("%-15s", event));
			mEventDetails.append(" event.\n");

			TextView eventTextView = (TextView) getView().findViewById(
					R.id.multi_touch_test_event_textview);
			eventTextView.setText(mEventDetails.toString());
		}
	}
}
