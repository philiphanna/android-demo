package uk.ac.qub.eeecs.demos.audio;

import java.io.IOException;

import uk.ac.qub.eeecs.demos.R;
import android.app.Fragment;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MediaPlayerTestFragment extends Fragment {

	/**
	 * Media player that will be used to play the music clip
	 */
	private MediaPlayer mMediaPlayer;

	/**
	 * Boolean flag used to indicate when playback can commence
	 */
	private boolean mMediaAvailable = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the view for this demo
		View view = inflater.inflate(R.layout.media_player_test_fragment,
				container, false);

		// Direct volume change requests to the audio manager
		getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

		// Create a new media player and try to load/prep the music clip
		mMediaPlayer = new MediaPlayer();
		try {
			// Create a suitable file descriptor to the music clip
			AssetManager assetManager = getActivity().getAssets();
			AssetFileDescriptor musicDescriptor = assetManager
					.openFd("music/coach-hello.mp3");

			// Get the media player ready to play the music clip
			mMediaPlayer.setDataSource(musicDescriptor.getFileDescriptor(),
					musicDescriptor.getStartOffset(),
					musicDescriptor.getLength());
			mMediaPlayer.setLooping(true);
			mMediaPlayer.prepare();

			// Indicate that the media is available to be played
			mMediaAvailable = true;
		} catch (IOException e) {
			// If we have any problems loading the music then indicate this
			TextView outputText = (TextView) view
					.findViewById(R.id.media_player_test_textview);
			outputText.setText("ERROR: Problem playing music. "
					+ e.getMessage());
		}

		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();

		// Start the audio playback whenever the fragment is resumed
		if (mMediaAvailable) {
			mMediaPlayer.start();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();

		// Pause/release the audio whenever the fragment is paused/deleted
		if (mMediaAvailable) {
			mMediaPlayer.pause();

			if (getActivity().isFinishing()) {
				mMediaPlayer.stop();
				mMediaPlayer.release();
			}
		}
	}
}
