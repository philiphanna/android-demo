package uk.ac.qub.eeecs.demos.storage;

import uk.ac.qub.eeecs.demos.R;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class SharedPreferencesTestFragment extends Fragment {

	/**
	 * Text view for displaying the top scores
	 */
	private TextView mTopScoreTextView;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the view for this fragment
		View view = inflater.inflate(R.layout.shared_preferences_fragment,
				container, false);

		// Extract and store the top score text view
		mTopScoreTextView = (TextView) view
				.findViewById(R.id.shared_preferences_textview);

		// Load and display the top scores
		loadTopScores();
		displayTopScores();

		// Setup the edit text to accept scores and update the top scores
		EditText editText = (EditText) view
				.findViewById(R.id.shared_preferences_edittext);
		editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView textView, int actionId,
					KeyEvent event) {

				// Extract and score and convert it into an integer
				int score = 0;
				try {
					score = Integer.parseInt(textView.getText().toString());
				} catch (NumberFormatException e) {
				}

				// Clear the entered text
				textView.setText("");

				// Update the top scores
				if (updateTopScores(score)) {
					// If we have a new top score then save and update
					saveTopScores();
					displayTopScores();
				}

				return true;
			}
		});

		return view;
	}

	// ////////////////////////////////////////////////////////////////////
	// Top Scores
	// ////////////////////////////////////////////////////////////////////

	/**
	 * Number of top scores that will be stored
	 */
	private final int NUM_TOP_SCORES = 5;

	/**
	 * Array of top scores
	 */
	private int mTopScores[] = new int[NUM_TOP_SCORES];

	/**
	 * Load the top scores from the shared preferences
	 */
	private void loadTopScores() {
		// Get the shared preferences for the app
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity()
						.getApplicationContext());

		// Retrieve the top scores
		for (int scoreIdx = 0; scoreIdx < NUM_TOP_SCORES; scoreIdx++) {
			mTopScores[scoreIdx] = preferences
					.getInt("Top" + (scoreIdx + 1), 0);
		}
	}

	/**
	 * Save the top scores to the shared preferences
	 */
	private void saveTopScores() {
		// Get the shared preferences for the app
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity()
						.getApplicationContext());

		// Get an editor for updating the preferences
		Editor preferenceEditor = preferences.edit();

		// Store the top scores
		for (int scoreIdx = 0; scoreIdx < NUM_TOP_SCORES; scoreIdx++) {
			preferenceEditor.putInt("Top" + (scoreIdx + 1),
					mTopScores[scoreIdx]);
		}

		// Commit the preference changes
		preferenceEditor.commit();
	}

	/**
	 * Display the top scores
	 */
	private void displayTopScores() {
		StringBuilder builder = new StringBuilder();

		for (int scoreIdx = 0; scoreIdx < NUM_TOP_SCORES; scoreIdx++) {
			builder.append("Top score ");
			builder.append((scoreIdx + 1));
			builder.append(" = ");
			builder.append(mTopScores[scoreIdx]);
			builder.append("\n");
		}

		mTopScoreTextView.setText(builder.toString());
	}

	/**
	 * Update the top scores with the new score
	 * 
	 * @param newScore
	 *            New score to consider against the top scores
	 * @return Boolean true if the top scores were changed, otherwise false
	 */
	private boolean updateTopScores(int newScore) {

		boolean isChanged = false;

		int score = newScore, temp;
		for (int scoreIdx = 0; scoreIdx < NUM_TOP_SCORES; scoreIdx++) {
			if (score > mTopScores[scoreIdx]) {
				temp = mTopScores[scoreIdx];
				mTopScores[scoreIdx] = score;
				score = temp;

				isChanged = true;
			}
		}

		return isChanged;
	}
}