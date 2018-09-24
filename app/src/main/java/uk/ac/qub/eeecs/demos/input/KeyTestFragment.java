package uk.ac.qub.eeecs.demos.input;

import uk.ac.qub.eeecs.demos.R;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class KeyTestFragment extends Fragment {

	/**
	 * Text view that can contain the output alongside a string builder to help
	 * construct the output
	 */

	private TextView mKeyEventsOutput;
	private StringBuilder mBuilder = new StringBuilder();

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
		View view = inflater.inflate(R.layout.key_test_fragment, container,
				false);

		// Get the edit text view and add a key listener
		mKeyEventsOutput = (TextView) view.findViewById(R.id.key_test_textview);
		EditText keyTestEditText = (EditText) view
				.findViewById(R.id.key_test_edittext);
		keyTestEditText.setOnKeyListener(new KeyTestOnKeyListener());

		return view;
	}

	// ////////////////////////////////////////////////////////////////////////
	// Key Listener
	// ////////////////////////////////////////////////////////////////////////

	private class KeyTestOnKeyListener implements View.OnKeyListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.view.View.OnKeyListener#onKey(android.view.View, int,
		 * android.view.KeyEvent)
		 */
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {

			switch (event.getAction()) {
			case KeyEvent.ACTION_DOWN:
				mBuilder.append("Key Down \t[");
				mBuilder.append(keyCode == KeyEvent.KEYCODE_ENTER ? ' '
						: (char) event.getUnicodeChar());
				mBuilder.append("][");
				mBuilder.append(KeyEvent.keyCodeToString(keyCode));
				mBuilder.append("]");
				mBuilder.append("\n");
				break;
			case KeyEvent.ACTION_UP:
				mBuilder.append("Key Up \t\t[");
				mBuilder.append(keyCode == KeyEvent.KEYCODE_ENTER ? ' '
						: (char) event.getUnicodeChar());
				mBuilder.append("][");
				mBuilder.append(KeyEvent.keyCodeToString(keyCode));
				mBuilder.append("]");
				mBuilder.append("\n");
				break;
			default:
				mBuilder.append("Other Event \t[");
				mBuilder.append(KeyEvent.keyCodeToString(keyCode));
				mBuilder.append("]");
				mBuilder.append("\n");
			}

			// Display and log the key event
			mKeyEventsOutput.setText(mBuilder.toString());
			Log.d(v.getContext().getApplicationContext().getResources()
					.getString(R.string.LOG_TAG), mBuilder.toString());

			// Often the return is return keyCode != KeyEvent.KEYCODE_BACK;
			// which will return true (signifying that the key event has been
			// processed unless the back key has been pressed. Currently we just
			// return false which lets the edit text component process the key
			// process as normal.
			return false;
		}
	}
}
