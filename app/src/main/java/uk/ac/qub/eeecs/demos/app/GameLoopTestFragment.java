package uk.ac.qub.eeecs.demos.app;

import uk.ac.qub.eeecs.demos.R;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class GameLoopTestFragment extends Fragment {

	/**
	 * Game loop that will drive the update and draw behaviours
	 */
	private GameLoop mLoop;

	// ////////////////////////////////////////////////////////////////////////
	// Create and Destroy
	// ////////////////////////////////////////////////////////////////////////

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Create a new game loop running at an impressive 2 FPS!
		mLoop = new GameLoop(2);

		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the view - we're only going to log to the console so nothing
		// useful is displayed
		View view = inflater.inflate(R.layout.life_cycle_test_fragment,
				container, false);
		TextView text = (TextView) view
				.findViewById(R.id.life_cycle_test_textview);
		text.setText("Running the game loop - Check your LogCat window");

		return view;
	}

	// ////////////////////////////////////////////////////////////////////////
	// Resume and Pause
	// ////////////////////////////////////////////////////////////////////////

	@Override
	public void onPause() {
		mLoop.pause();
		Log.d("Loop", "Pausing loop");
		super.onPause();
	}

	@Override
	public void onResume() {
		Log.d("Loop", "Resuming loop");
		mLoop.resume();
		super.onResume();
	}

	// ////////////////////////////////////////////////////////////////////////
	// Update and Draw methods
	// ////////////////////////////////////////////////////////////////////////

	static int counter = 0;

	/**
	 * Update method to update whatever needs to be updated
	 */
	protected void doUpdate() {
		Log.d("Loop", "Update" + (counter++));
	}

	/**
	 * Draw method to draw whatever needs to be drawn. Important this method
	 * will be called from the game loop thread. Do not assume it can updates
	 * views directly (it's not the GUI thread!)
	 */
	protected void doDraw() {
		Log.d("Loop", "Draw" + counter);
	}	
	
	// ////////////////////////////////////////////////////////////////////////
	// Game Loop
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Core game loop thread
	 * 
	 * @version 1.0
	 */
	private class GameLoop implements Runnable {

		// ////////////////////////////////////////////////////////////////////
		// Properties
		// ////////////////////////////////////////////////////////////////////

		/**
		 * Target number of FPS
		 */
		int targetFramesPerSecond;

		/**
		 * Thread on which the game loop will run
		 */
		Thread renderThread = null;

		/**
		 * Flag determining if the update/draw thread is running
		 */
		volatile boolean running = false;

		/**
		 * Variable holding the duration (in ns) of the target game step period.
		 * Changes to the Game's mTargetUpdatesPerSecond will change this value.
		 */
		long targetStepPeriod;

		// ////////////////////////////////////////////////////////////////////
		// Constructor
		// ////////////////////////////////////////////////////////////////////

		/**
		 * Create a new game loop (the update/draw process will not commence
		 * until the run method is executed).
		 */
		public GameLoop(int targetFPS) {
			targetFramesPerSecond = targetFPS;
			// Setup the target step period
			targetStepPeriod = 1000000000 / targetFramesPerSecond;
		}

		// ////////////////////////////////////////////////////////////////////
		// Methods: Update/Draw Loop
		// ////////////////////////////////////////////////////////////////////

		/**
		 * Start the update/draw process within a new thread.
		 * 
		 * A relatively simple approach is employed that can support basic
		 * multi-threading. A more sophisticated threaded approache might adopt
		 * a three-phase prep-update-draw approach where the prep of frame n+1
		 * occurs concurrently (across one or more threads) whilst the draw of
		 * frame n executes. A more sophisticated timing approach might decouple
		 * the draw and render phases, skipping the render of a frame if needed
		 * to maintain a target update rate.
		 */
		@Override
		public void run() {

			/**
			 * Define variables which will be used to provide timing information
			 * to enable precise control of the update/render cycle.
			 * 
			 * startRun records the time at which the first iteration commenced
			 * and is used to track total run time.
			 * 
			 * The startStep and endStep variables record the time before and
			 * time immediately after the update/render step.
			 * 
			 * sleepTime records how long the thread should sleep before it is
			 * necessary to start on the next update/render cycle (this may be a
			 * negative period - i.e. the update/render process took longer than
			 * desired). overSleepTime records how much longer the thread sleep
			 * than was originally requested (i.e. accounting for the
			 * unpredictable delay in waking up the thread).
			 */
			long startRun;
			long startStep, endStep;
			long sleepTime, overSleepTime;

			/**
			 * Define default starting values. The startTime and postRender
			 * times are set to one frame 'in the past' to avoid near zero
			 * timings for the first iteration. overSleepTime is set to zero.
			 */
			startRun = System.nanoTime() - targetStepPeriod;
			startStep = startRun;
			overSleepTime = 0L;

			try {
				while (running) {

					// Update the timing information
					long currentTime = System.nanoTime();
					startStep = currentTime;

					doUpdate();
					doDraw();

					// Measure how long the update/draw took to complete and
					// how long to sleep until the next cycle is due. This may
					// be a negative number (we've exceeded the 'available'
					// time).
					endStep = System.nanoTime();
					sleepTime = (targetStepPeriod - (endStep - startStep))
							- overSleepTime;

					// If needed put the thread to sleep
					if (sleepTime > 0) {
						Thread.sleep(sleepTime / 1000000L); // Covert ns into ms

						// Determine how much longer we slept than was
						// originally requested, we'll correct for this error
						// next frame
						overSleepTime = (System.nanoTime() - endStep)
								- sleepTime;
					} else {
						overSleepTime = 0L;
					}
				}

			} catch (InterruptedException e) {
			}
		}

		// ////////////////////////////////////////////////////////////////////
		// Methods: Pause/Resume
		// ////////////////////////////////////////////////////////////////////

		/**
		 * Pause the game loop. This method will be called by the game whenever
		 * it is paused.
		 */
		public void pause() {
			running = false;
			while (true) {
				try {
					renderThread.join();
					return;
				} catch (InterruptedException e) {
				}
			}
		}

		/**
		 * Resume the game loop. This method will be called by the game whenever
		 * it is resumed.
		 */
		public void resume() {
			running = true;

			renderThread = new Thread(this);
			renderThread.start();
		}
	}

}