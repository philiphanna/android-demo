package uk.ac.qub.eeecs.demos.audio;

import java.io.IOException;

import uk.ac.qub.eeecs.demos.R;
import android.app.Fragment;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SoundPoolTestFragment extends Fragment {

	/**
	 * Sound pool which will manage the loaded sound effects
	 */
	private SoundPool mSoundPool;

	/**
	 * Maximum number of channels that will be used for playback in the sound
	 * pool
	 */
	private final int mMaxChannels = 20;

	/**
	 * ID of the loaded sound effect (-1 indicating the effect has yet to be
	 * loaded).
	 */
	private int mExplosionSfxId = -1;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the view associated with this fragment
		View view = inflater.inflate(R.layout.sound_pool_test_fragment,
				container, false);

		// Setup the text view with a click listener that will trigger play back
		// of the loaded sound effect
		TextView triggerView = (TextView) view
				.findViewById(R.id.sound_pool_test_textview);
		triggerView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Only play the sound if it has been loaded
				if (mExplosionSfxId != -1) {
					mSoundPool.play(mExplosionSfxId, 1.0f, 1.0f, 1, 0, 1.0f);
				}
			}
		});

		// Setup the activity so that changes in volume will be applied to the
		// played music
		getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
				
		mSoundPool = new SoundPool(mMaxChannels, AudioManager.STREAM_MUSIC, 0);

		try {
			// Use the asset manager to get a relevant file descriptor
			AssetManager assetManager = getActivity().getAssets();
			AssetFileDescriptor assetDescriptor = assetManager
					.openFd("sfx/explosion.mp3");

			// Load the sound effect into the sound pool
			mExplosionSfxId = mSoundPool.load(assetDescriptor, 1);
		} catch (IOException e) {
			triggerView.setText("ERROR: Could not load explosion sound effect."
					+ e.getMessage());
		}

		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();

		// Release the loaded sound(s)
		mSoundPool.release();
	}
}