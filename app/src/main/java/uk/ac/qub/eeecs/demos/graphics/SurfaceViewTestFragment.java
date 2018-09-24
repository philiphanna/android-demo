package uk.ac.qub.eeecs.demos.graphics;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import uk.ac.qub.eeecs.demos.R;
import android.app.Fragment;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

public class SurfaceViewTestFragment extends Fragment {

	// ////////////////////////////////////////////////////////////////////////
	// Fragment and renderer setup
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Surface view render instance that will be used to draw the contents on
	 * this fragment
	 */
	private SurfaceViewRenderer surfaceViewRenderer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Create a new render surface renderer that will be used to provide the
		// render view for this fragment
		surfaceViewRenderer = new SurfaceViewRenderer(getActivity());
		return surfaceViewRenderer;
	}

	/*
	 * #(non-Javadoc)
	 * 
	 * @see android.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();

		// Restart the renderer
		surfaceViewRenderer.resume();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {

		// When the fragment is paused also pause the renderer
		surfaceViewRenderer.pause();

		super.onPause();
	}


	// ////////////////////////////////////////////////////////////////////////
	// Render Thread
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Surface view render thread that will repeatedly acquire the view and
	 * render to it as fast as possible.
	 */
	class SurfaceViewRenderer extends SurfaceView implements Runnable {

        // ////////////////////////////////////////////////////////////////////////
        // Setup and Draw Methods
        // ////////////////////////////////////////////////////////////////////////

        /**
         * Define the data items used when drawing
         */
        private Bitmap mImage;
        private Paint mPaint;
        private Rect mRect;
        private Random mRandom;
        private long mNumCalls;

        /**
         * Method that will be called by the render thread when setup is triggered
         */
        private void doSetup() {
            mNumCalls = 0;
            mRandom = new Random();
            mRect = new Rect();
            mPaint = new Paint();

            // Try to load in the image that we will draw
            try {
                AssetManager assetManager = getActivity().getAssets();
                InputStream inputStream = assetManager.open("img/ARGB_8888.png");
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                mImage = BitmapFactory.decodeStream(inputStream, null, options);
                inputStream.close();
            } catch (IOException e) {
                Log.d(getActivity().getResources().getString(R.string.LOG_TAG),
                        "Load error: " + e.getMessage());
            }
        }

        /**
         * Method that will be called by the render thread when the canvas needs to
         * be redrawn
         *
         * @param canvas
         *            Canvas to be redrawn
         */
        private void doDraw(Canvas canvas) {

            int width = canvas.getWidth();
            int height = canvas.getHeight();

            // Draw the loaded bitmap 1000 times at random positions and with
            // random sizes
            int batchSize = 1000;
            for (int drawIdx = 0; drawIdx < batchSize; drawIdx++) {

                mRect.left = mRandom.nextInt(width);
                mRect.right = mRect.left + mRandom.nextInt(width - mRect.left);
                mRect.top = mRandom.nextInt(height);
                mRect.bottom = mRect.top + mRandom.nextInt(height - mRect.top);

                canvas.drawBitmap(mImage, null, mRect, null);
            }

            // Display a count of the number of frames that have been displayed
            mNumCalls++;
            mPaint.setTextSize(36.0f);
            mPaint.setTextAlign(Paint.Align.LEFT);
            mPaint.setColor(Color.WHITE);
            canvas.drawText("Num=" + mNumCalls, 50.0f, 50.0f, mPaint);
        }

        // ////////////////////////////////////////////////////////////////////////
        // Thread and Surfaceview elements
        // ////////////////////////////////////////////////////////////////////////

        /**
		 * Thread object that this rendered will use
		 */
		Thread renderThread = null;

		/**
		 * Flag determining if the render is running
		 */
		volatile boolean running = false;

		/**
		 * Define a surface holder for the render surface
		 */
		SurfaceHolder holder;

		/**
		 * Create a new render thread
		 * 
		 * @param context
		 *            Render context
		 */
		public SurfaceViewRenderer(Context context) {
			super(context);

			// Acquire a holder for this surface
			holder = getHolder();

			// Do whatever setup is needed
			doSetup();
		}

		/**
		 * Thread core run method
		 */
		@Override
		public void run() {
			while (running) {
				// Don't proceed until we've got a valid surface on which to
				// render
				if (!holder.getSurface().isValid())
					continue;

				// Lock the surface as we wish to draw on it
				Canvas canvas = holder.lockCanvas();

				// Draw whatever needs to be drawn
				doDraw(canvas);

				// Unlock the surface and post its contents to make it visible
				holder.unlockCanvasAndPost(canvas);
			}
		}

		/**
		 * Actions whenever the thread is paused
		 */
		public void pause() {
			running = false;
			while (true) {
				try {
					// Wait for the render thread's run method to stop before
					// returning
					renderThread.join();
					return;
				} catch (InterruptedException e) {
				}
			}
		}

		/**
		 * Whenever the thread is resumed (or started when the Android fragment
		 * is first resumed on creation) create a new thread and start the
		 * render process
		 */
		public void resume() {
			running = true;
			renderThread = new Thread(this);
			renderThread.start();
		}
	}
}
