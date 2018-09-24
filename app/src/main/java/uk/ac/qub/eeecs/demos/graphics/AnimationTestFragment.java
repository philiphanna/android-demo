package uk.ac.qub.eeecs.demos.graphics;

import java.io.IOException;
import java.io.InputStream;

import uk.ac.qub.eeecs.demos.R;
import android.app.Fragment;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AnimationTestFragment extends Fragment {

	// ////////////////////////////////////////////////////////////////////////
	// Core entities
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Game world
	 */
	private GameWorld mGameWorld;

	/**
	 * Canvas onto which the game world will be drawn
	 */
	private RenderView mRenderView;

	// ////////////////////////////////////////////////////////////////////////
	// Fragment setup
	// ////////////////////////////////////////////////////////////////////////

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create the game world
		mGameWorld = new GameWorld();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Create a custom view that will provide bitmap drawing facilities
		mRenderView = new RenderView(getActivity());

		return mRenderView;
	}

	// ////////////////////////////////////////////////////////////////////////
	// Animation class
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Simple animation holder
	 */
	public class Animation {

		// ////////////////////////////////////////////////////////////////////
		// Animation Properties
		// ////////////////////////////////////////////////////////////////////
				
		/**
		 * Bitmap holding the frames of this animation
		 */
		private Bitmap animationFrames;

		public Bitmap getBitmap() {
			return animationFrames;
		}
				
		/**
		 * Width and height of each frame of the animation
		 */
		private int frameWidth;
		private int frameHeight;

		/**
		 * Number of frames in the animation
		 */
		private int frameCount;
		
		/**
		 * Index of the current frame of animation
		 */
		private int currentFrame;

		/**
		 * Display period for each frame alongside a frame timer
		 */
		private double frameTimer;
		private double framePeriod;

		/**
		 * Boolean flag determining if the animation should loop
		 */
		private boolean isLooping = false;
		
		/**
		 * Boolean flag determining if the animation is currently playing
		 */
		private boolean isPlaying = false;

		// ////////////////////////////////////////////////////////////////////
		// Animation Constructor and Methods
		// ////////////////////////////////////////////////////////////////////
				
		/**
		 * Create a new animation
		 * 
		 * @param animationFrames Bitmap holding the frames of the animation
		 * @param frameCount Number of horizontal frames in the animation 
		 *        (assumed to be of equal width)
		 */
		public Animation(Bitmap animationFrames, int frameCount) {
			
			this.animationFrames = animationFrames;
			this.frameCount = frameCount;
			
			frameHeight = animationFrames.getHeight();
			frameWidth = animationFrames.getWidth() / frameCount;
		}

		/**
		 * Trigger playback of this animation 
		 * 
		 * @param animationPeriod Period over which the animation should play
		 * @param loop True if the animation should play repeatedly
		 */
		public void play(double animationPeriod, boolean loop) {
			framePeriod = animationPeriod / (double) frameCount;
			currentFrame = -1;
			isLooping = loop;
			
			isPlaying = true;			
		}

		/**
		 * Update which frame of the animation should be displayed
		 * 
		 * @param elapsedTime Elapsed time since the last update
		 */
		public void update(double elapsedTime) {
			if (!isPlaying)
				return;

			// If this is the first time update has been called since the 
			// play method was called then reset the current frame and timer
			if (currentFrame == -1) {
				currentFrame = 0;
				frameTimer = 0.0;
			}

			// Update the amount of time accumulated against this frame
			frameTimer += elapsedTime;

			// If the frame display duration has been exceeded then try to
			// go on to the next frame, looping or stopping if the end of 
			// the animation has been reached.
			if (frameTimer >= framePeriod) {
				currentFrame++;
				if (currentFrame >= frameCount) {
					if (isLooping) {
						currentFrame = 0;
					} else {
						currentFrame = frameCount - 1;
						isPlaying = false;
					}
				}

				// 'Reset' the frame timer
				frameTimer -= framePeriod;
			}
		}

		/**
		 * Update the specified rect object to contain the region of the bitmap 
		 * holding the current frame of animation.
		 * 
		 * @param sourceRect Rect object to be updated
		 */
		public void getSourceRect(Rect sourceRect) {
			if(currentFrame >= 0)
				sourceRect.set(currentFrame * frameWidth, 0, currentFrame
						* frameWidth + frameWidth, frameHeight);
		}
	}

	// ////////////////////////////////////////////////////////////////////////
	// Simple game world 
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Define a simple game world
	 */
	private class GameWorld {

		// ////////////////////////////////////////////////////////////////////
		// World 'objects'
		// ////////////////////////////////////////////////////////////////////

		/**
		 * Define a couple of animations
		 */
		private Animation animation1;
		private Animation animation2;

		// ////////////////////////////////////////////////////////////////////
		// World construction and update
		// ////////////////////////////////////////////////////////////////////

		/**
		 * Create the new world
		 */
		public GameWorld() {
			setupWorld();
		}

		/**
		 * Create and position the fish
		 */
		private void setupWorld() {

			Bitmap animation1Bitmap = null, animation2Bitmap = null;

			// Attempt to load the bitmap used for the animation
			try {
				AssetManager assetManager = getActivity().getAssets();

				InputStream inputStream = assetManager
						.open("img/Animation1.png");
				animation1Bitmap = BitmapFactory.decodeStream(inputStream);
				inputStream.close();

				inputStream = assetManager.open("img/Animation2.png");
				animation2Bitmap = BitmapFactory.decodeStream(inputStream);
				inputStream.close();

			} catch (IOException e) {
				Log.d(getActivity().getResources().getString(R.string.LOG_TAG),
						"Load error: " + e.getMessage());
			}

			// Create the two animations
			animation1 = new Animation(animation1Bitmap, 12);
			animation2 = new Animation(animation2Bitmap, 20);

			// Indicate that playback should commence
			animation1.play(1.2, true);
			animation2.play(2.2, true);
		}

		/**
		 * Update the world
		 */
		public void update(double elapsedTime) {
			animation1.update(elapsedTime);
			animation2.update(elapsedTime);
		}

		// ////////////////////////////////////////////////////////////////////
		// World draw
		// ////////////////////////////////////////////////////////////////////

		/**
		 * Define a source rectangle to hold the portion of the source bitmap
		 * that is to be drawn and a screen rectangle to hold the region we will
		 * draw to.
		 */
		private Rect sourceRect = new Rect();
		private Rect screenRect = new Rect();

		/**
		 * Draw the world 
		 * 
		 * @param canvas
		 *            Canvas object on which to draw
		 */
		public void draw(Canvas canvas) {

			// Draw the two animations
			animation1.getSourceRect(sourceRect);
			screenRect.set(100, 100, 400, 400);
			canvas.drawBitmap(
					animation1.getBitmap(), sourceRect, screenRect, null);

			animation2.getSourceRect(sourceRect);
			screenRect.set(500, 200, 600, 340);
			canvas.drawBitmap(
					animation2.getBitmap(), sourceRect, screenRect, null);
		}
	}

	/**
	 * Custom view object that will repeatedly update and display the game
	 * world.
	 */
	private class RenderView extends View {

		/**
		 * Elapsed time value
		 */
		long referenceTime;

		/**
		 * Create a new render view instance
		 * 
		 * @param context
		 *            Parent context
		 */
		public RenderView(Context context) {
			super(context);

			referenceTime = System.nanoTime();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.view.View#onDraw(android.graphics.Canvas)
		 */
		@Override
		protected void onDraw(Canvas canvas) {

			canvas.drawColor(Color.BLACK);

			// Work out how much time (in second) has elapsed since the last update
			long timeNow = System.nanoTime();
			long elapsedMs = (timeNow - referenceTime) / 1000000L;
			double elapsedTime = (double) elapsedMs / 1000.0;
			referenceTime = timeNow;

			// Cheeky - we should not call these in a draw method....
			mGameWorld.update(elapsedTime);

			// Draw
			mGameWorld.draw(canvas);

			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
			}

			// Invalid our canvas, so we'll be asked to redraw
			invalidate();
		}
	}
}