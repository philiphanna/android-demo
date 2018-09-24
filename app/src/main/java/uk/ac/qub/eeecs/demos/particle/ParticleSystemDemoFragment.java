package uk.ac.qub.eeecs.demos.particle;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Particle system demo
 * 
 * @version 1.0
 */
public class ParticleSystemDemoFragment extends Fragment {

	// /////////////////////////////////////////////////////////////////////////
	// Properties: Game Loop
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Surface to which the game is rendered
	 */
	private RenderSurface mRenderSurface;

	/**
	 * Particle world 
	 */
	private ParticleWorld mParticleWorld;

	/**
	 * Location at which the screen was last touched
	 */
	private Vector2 lastTouchLocation = new Vector2(300,300);

	// /////////////////////////////////////////////////////////////////////////
	// Methods: State Management
	// /////////////////////////////////////////////////////////////////////////

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Create a new particle world
		mParticleWorld = new ParticleWorld(getActivity());
				
		// Create the output view and associated renderer
		mRenderSurface = new RenderSurface(getActivity(), 30);
		mRenderSurface.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// Simply store the location of the last touch event
				lastTouchLocation.x = event.getX();
				lastTouchLocation.y = event.getY();
				return true;
			}
		});

		return mRenderSurface;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();

		// Resume the game loop
		mRenderSurface.resume();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		// Pause the game loop
		mRenderSurface.pause();

		super.onPause();
	}

	// /////////////////////////////////////////////////////////////////////////
	// Game Loop
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Core game loop thread
	 * 
	 * @version 1.0
	 */
	private class RenderSurface extends View implements Runnable {

		// ////////////////////////////////////////////////////////////////////
		// Properties
		// ////////////////////////////////////////////////////////////////////

		/**
		 * Concurrent boolean lock that can be used to control update and draw
		 * inter-thread sequencing.
		 */
		class BooleanLock {
			boolean isLocked;

			public BooleanLock(boolean isLocked) {
				this.isLocked = isLocked;
			}
		}

		/**
		 * Sequence locks for the update and draw steps
		 */
		volatile BooleanLock draw;

		/**
		 * Thread on which the game loop will run
		 */
		Thread renderThread = null;

		/**
		 * Flag determining if the update/draw thread is running
		 */
		volatile boolean running = false;

		float elapsedTime;

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
		public RenderSurface(Context context, int targetFramesPerSecond) {
			super(context);

			// Setup the target step period
			targetStepPeriod = 1000000000 / targetFramesPerSecond;
			// Create update and draw locks
			draw = new BooleanLock(false);
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
					elapsedTime = (float) ((currentTime - startStep) / 1000000000.0);
					startStep = currentTime;

					mParticleWorld.update(elapsedTime, lastTouchLocation);

					// Trigger a draw request
					synchronized (draw) {
						draw.isLocked = true;
					}
					postInvalidate();
					// Wait for the draw to complete before progressing
					// If a plan-update-draw approach was employed the
					// wait for the draw would be tested post plan completion.
					synchronized (draw) {
						if (draw.isLocked) {
							draw.wait();
						}
					}

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

		@Override
		protected void onDraw(Canvas canvas) {
			mParticleWorld.draw(canvas, elapsedTime);

			synchronized (draw) {
				draw.isLocked = false;
				draw.notifyAll();
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
					// Log something here
					// retry
				}
			}
		}

		/**
		 * Resume the game loop. This method will be called by the game whenever
		 * it is resumed.
		 */
		public void resume() {
			running = true;

			draw.isLocked = false;

			renderThread = new Thread(this);
			renderThread.start();
		}
	}
}