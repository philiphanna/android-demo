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
import android.view.View;
import android.view.ViewGroup;

public class CanvasThreadTestFragment extends Fragment {

	// ////////////////////////////////////////////////////////////////////////
	// Fragment and renderer setup
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Canvas render instance that will be used to draw the contents on this
	 * fragment
	 */
	private CanvasRenderer mCanvasRenderer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Create a new canvas renderer that will be used to provide the render
		// view for this fragment
		mCanvasRenderer = new CanvasRenderer(getActivity());

		return mCanvasRenderer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();

		// Restart the renderer
		mCanvasRenderer.resume();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {

		// When the fragment is paused also pause the renderer
		mCanvasRenderer.pause();

		super.onPause();
	}


	// ////////////////////////////////////////////////////////////////////////
	// Render Thread
	// ////////////////////////////////////////////////////////////////////////
		
	/**
	 * Canvas render thread that will repeatedly request that the view is
	 * redrawn as fast as possible.
	 */
	class CanvasRenderer extends View implements Runnable {

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
        // Thread Methods
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
		 * Flag determining if a draw call is needed
		 */
		volatile boolean drawNeeded = false;

		/**
		 * Create a new render thread
		 * 
		 * @param context
		 *            Render context
		 */
		public CanvasRenderer(Context context) {
			super(context);

			// Do whatever setup is needed
			doSetup();
		}

		/**
		 * Thread core run method
		 */
		@Override
		public void run() {
			while (running) {
				// If the last draw has been finished then request that the next
				// starts by posting an invalidate message
				if (drawNeeded) {
					drawNeeded = false;
					postInvalidate();
				}

				// Sleep for 10ms (maximum of 100fps)
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
			}
		}

		/**
		 * View method that will be called whenever the invalidate message has
		 * bee acted upon
		 * 
		 * @param canvas
		 *            Canvas object for drawing the object
		 */
		@Override
		protected void onDraw(Canvas canvas) {
			// Draw whatever needs to be drawn and flag that another draw can be
			// triggered
			doDraw(canvas);
			drawNeeded = true;
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
			drawNeeded = true;
			renderThread = new Thread(this);
			renderThread.start();
		}		
	}
}
