package uk.ac.qub.eeecs.demos.storage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import uk.ac.qub.eeecs.demos.R;
import android.app.Fragment;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AssetsTestFragment extends Fragment {

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
		View view = inflater.inflate(R.layout.assets_test_fragment, container,
				false);

		// Get the asset manager for the current activity and load in a
		// text and bitmap asset
		AssetManager assetManager = getActivity().getAssets();
		String text = loadText(assetManager, "txt/welcome.txt");
		Bitmap bitmap = loadBitmap(assetManager, "img/ARGB_8888.png");

		// Display the loaded text
		TextView outputTextView = (TextView) view
				.findViewById(R.id.assets_test_textview);
		outputTextView.setText(text != null ? text
				: "ERROR: Could not open text file.");

		// Display the loaded bitmap
		ImageView imageView = (ImageView) view
				.findViewById(R.id.assets_test_imageview);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		}
		
		return view;
	}

	// ////////////////////////////////////////////////////////////////////////
	// Load text asset
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Load in the named text file from the specified asset manager
	 * 
	 * @param assetManager
	 *            Asset manager holding the text file
	 * @param asset
	 *            Location and name of the asset
	 * @return Loaded text file formatted as a string
	 */
	private String loadText(AssetManager assetManager, String asset) {

		String text = null;
		InputStream inputStream = null;

		try {
			// Try to open the text file
			inputStream = assetManager.open(asset);

			// Load in the text in 4k chunks
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			byte[] chunk = new byte[4096];
			int len = 0;
			while ((len = inputStream.read(chunk)) > 0)
				byteStream.write(chunk, 0, len);

			// Convert and return as a UFT8 string
			text = new String(byteStream.toByteArray(), "UTF8");

		} catch (IOException e) {
			Log.e(getActivity().getResources().getString(R.string.LOG_TAG),
					"Error loading text asset: " + e.getMessage());
		} finally {
			if (inputStream != null)
				try {
					inputStream.close();
				} catch (IOException e) { /* Let's just return with a null */ }
		}

		return text;
	}

	// ////////////////////////////////////////////////////////////////////////
	// Load bitmap
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Load in the named bitmap from the specified asset manager
	 * 
	 * @param assetManager
	 *            Asset manager holding the bitmap
	 * @param asset
	 *            Location and name of the asset
	 * @return Loaded bitmap
	 */
	private Bitmap loadBitmap(AssetManager assetManager, String asset) {

		Bitmap bitmap = null;
		InputStream inputStream = null;

		try {
			// Try to open the bitmap
			inputStream = assetManager.open(asset);

			// Setup what load preferences we might have (this could be an
			// argument)
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;

			// Load the bitmap
			bitmap = BitmapFactory.decodeStream(inputStream, null, options);

		} catch (IOException e) {
			Log.e(getActivity().getResources().getString(R.string.LOG_TAG),
					"Error loading bitmap: " + e.getMessage());
		} finally {
			if (inputStream != null)
				try {
					inputStream.close();
				} catch (IOException e) { /* Let's just return with a null */ }
		}

		return bitmap;
	}
}