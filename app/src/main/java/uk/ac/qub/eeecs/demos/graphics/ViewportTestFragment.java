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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ViewportTestFragment extends Fragment {

	// ////////////////////////////////////////////////////////////////////////
	// Game world
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Game world in which positive y points upwards
	 */
	private GameWorldPositiveYUp mGameWorldYUp;

	/**
	 * Game world in which positive y points downwards (as is the case for
	 * screen coordinates on most displays, with (0,0) being the top-left
	 * corner).
	 */
	private GameWorldPositiveYDown mGameWorldYDown;

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

		// Create the two game worlds
		mGameWorldYUp = new GameWorldPositiveYUp();
		mGameWorldYDown = new GameWorldPositiveYDown();
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
	// World in which positive-y points upwards
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Define an equivalent to the Rect class, called Box, in which it is
	 * assumed that positive-y points up.
	 * 
	 * This might seem an odd box to create, however, consider the following
	 * fragment from the Android Rect class description:
	 * 
	 * "Rect holds four integer coordinates for a rectangle. Note: most methods
	 * do not check to see that the coordinates are sorted correctly (i.e. left
	 * <= right and top <= bottom)."
	 * 
	 * In particular, Rect assumes that top <= bottom, i.e. that positive y 
	 * points downwards. In box, positive y will point upwards, i.e. top >= bottom
	 */
	private class Box {
		public float x; 		// Centre x location 
		public float y; 		// Centre y location
		
		public float width;		// Box width (we could(should) have used half width)
		public float height;	// Box height (we could(should) have used half height)
		
		public Box() {
			x = 0; y = 0; width = 1; height = 1;
		}

		public Box(float x, float y, float width, float height) {
			this.x = x; this.y = y; this.width = width; this.height = height;
		}
	}

	/**
	 * Define a game world in which positive y points up
	 */
	private class GameWorldPositiveYUp {

		// ////////////////////////////////////////////////////////////////////
		// World 'objects'
		// ////////////////////////////////////////////////////////////////////		
		
		/**
		 * Define graphical parameters within the world (a series of platforms)
		 */
		private final int mNumPlatforms = 100;				
		private Box mPlatforms[] = new Box[mNumPlatforms];
		private Bitmap mPlatformBitmap;

		/**
		 * Define the layer viewport region. A more fancy layerport class
		 * could have been defined that would offer move/focus methods.
		 */		
		private Box mLayerViewport; 

		// ////////////////////////////////////////////////////////////////////
		// World construction and update
		// ////////////////////////////////////////////////////////////////////		
				
		/**
		 * Create the new world
		 */
		public GameWorldPositiveYUp() {
			createViewports();
			loadAndPositionPlatforms();
		}

		/**
		 * Define the layer viewport
		 */
		private void createViewports() {
			mLayerViewport = new Box(150, 150, 300, 300);
		}

		/**
		 * Create and position the platforms
		 */
		private void loadAndPositionPlatforms() {

			// Attempt to load the bitmap used for the platforms
			try {
				AssetManager assetManager = getActivity().getAssets();
				InputStream inputStream = assetManager.open("img/Platform.png");
				mPlatformBitmap = BitmapFactory.decodeStream(inputStream);
				inputStream.close();
			} catch (IOException e) {
				Log.d(getActivity().getResources().getString(R.string.LOG_TAG),
						"Load error: " + e.getMessage());
			}

			// Define the location of each platform
			for (int idx = 0; idx < mNumPlatforms; idx++) {
				int x = mPlatformBitmap.getWidth() / 2 + idx * mPlatformBitmap.getWidth();
				int y = mPlatformBitmap.getHeight() / 2 + idx * mPlatformBitmap.getHeight();
				mPlatforms[idx] = new Box(x, y, 
						mPlatformBitmap.getWidth(), mPlatformBitmap.getHeight());
			}
		}

		/**
		 * Update the layer by moving the viewport along the x and y axis
		 */
		public void update() {
			float scrollSpeed = 10.0f;
			mLayerViewport.x += scrollSpeed;
			mLayerViewport.y += scrollSpeed
					* (float) mPlatformBitmap.getHeight() / (float) mPlatformBitmap.getWidth();

			// Move the viewport back when we've reached the end
			if (mLayerViewport.x > mNumPlatforms * mPlatforms[0].width) {
				mLayerViewport.x = mPlatformBitmap.getWidth() / 2;
				mLayerViewport.y = mPlatformBitmap.getHeight() / 2;
			}
		}
		
		// ////////////////////////////////////////////////////////////////////
		// World draw
		// ////////////////////////////////////////////////////////////////////		
		
		/**
		 * Define a source rectangle to hold the portion of the source bitmap that is
		 * to be drawn and a screen rectangle to hold the region we will draw to.
		 * IMPORTANT: Rect's are used (i.e. positive-y points down) as this is 
		 * assumed when drawing to a canvas.
		 */
		private Rect sourceRect = new Rect();
		private Rect screenRect = new Rect();
		
		/**
		 * Draw the world to the specified viewport defined on the specified canvas
		 * 
		 * @param canvas Canvas object on which to draw
		 * @param screenViewport Viewport on the canvas object to draw to
		 */
		public void draw(Canvas canvas, Rect screenViewport) {

			// Determine the x- and y-aspect rations between the layer and screen viewports			
			float screenXScale = 
					(float) screenViewport.width() / (float) mLayerViewport.width; 
			float screenYScale = 
					(float) screenViewport.height() / (float) mLayerViewport.height;

			// Test each platfrom to see if it needs to be drawn
			for (int idx = 0; idx < mNumPlatforms; idx++) {

				// Check if some portion of the platform is visible within the layer viewport

				if (mPlatforms[idx].x - mPlatforms[idx].width / 2 
						< mLayerViewport.x + mLayerViewport.width / 2 && 
					mPlatforms[idx].x + mPlatforms[idx].width / 2 
						> mLayerViewport.x - mLayerViewport.width / 2 && 
					mPlatforms[idx].y - mPlatforms[idx].height / 2 
						< mLayerViewport.y + mLayerViewport.height / 2 && 
					mPlatforms[idx].y + mPlatforms[idx].height / 2 
						> mLayerViewport.y - mLayerViewport.height / 2) {

					// At this point we know some portion of the platform is visible
					
					// Determining the scale factor for mapping the bitmap onto this 
					// platform. This should really be wrapped up inside a platform 
					// object and not recalculated ever single draw request. Note
					// the use of float casting to avoid integer maths.

					float sourceScaleWidth = 
							(float) mPlatformBitmap.getWidth()/(float) mPlatforms[idx].width;
					float sourceScaleHeight = 
							(float) mPlatformBitmap.getHeight()/(float) mPlatforms[idx].height;

					// Work out what region of the platform is visible within the viewport,
					// The y-axis is inverted (i.e. 0 is the top of the bitmap image and 
					// positive y moves down) - as is the convention for images.
					
					float sourceX = Math.max(0.0f,
							(mLayerViewport.x - mLayerViewport.width / 2) 
								- (mPlatforms[idx].x - mPlatforms[idx].width / 2));
					float sourceY = Math.max(0.0f,
							(mPlatforms[idx].y + mPlatforms[idx].height / 2) 
								- (mLayerViewport.y + mLayerViewport.height / 2));

					float sourceWidth = ((mPlatforms[idx].width - sourceX) - Math.max(0.0f,
							(mPlatforms[idx].x + mPlatforms[idx].width / 2)
								- (mLayerViewport.x + mLayerViewport.width / 2)));
					float sourceHeight = ((mPlatforms[idx].height - sourceY) - Math.max(0.0f,
							(mLayerViewport.y - mLayerViewport.height / 2) 
								- (mPlatforms[idx].y - mPlatforms[idx].height / 2)));

					// Set the region of the source bitmap we will draw (scaling as needed)					
					sourceRect.set(
							(int)(sourceX * sourceScaleWidth), (int)(sourceY * sourceScaleHeight),
							(int)((sourceX + sourceWidth) * sourceScaleWidth),
							(int)((sourceY + sourceHeight) * sourceScaleHeight));

					// Work out which region of the screen viewport (relative to the canvas) 
					// we will be drawing to. Assuming here that (0,0) is the top-left position
					// and positive y moves down the way.
										
					float screenX = screenViewport.left + Math.max(0.0f,
							((mPlatforms[idx].x - mPlatforms[idx].width / 2) 
									- (mLayerViewport.x - mLayerViewport.width / 2))) * screenXScale;
					float screenY = screenViewport.top + Math.max(0.0f,
							((mLayerViewport.y + mLayerViewport.height / 2) 
									- (mPlatforms[idx].y + mPlatforms[idx].height / 2))) * screenYScale;

					float screenWidth = sourceWidth * screenXScale;
					float screenHeight = sourceHeight * screenYScale;

					// Set the region to the canvas to which we will draw					
					screenRect.set((int)screenX, (int)screenY,
							(int)(screenX + screenWidth), (int)(screenY + screenHeight));

					// Draw the platform
					canvas.drawBitmap(mPlatformBitmap, sourceRect, screenRect,
							null);
				}
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////
	// World in which positive-y points downwards
	// ////////////////////////////////////////////////////////////////////////	
	
	/**
	 * Define a game world in which positive y points down
	 */
	private class GameWorldPositiveYDown {

		
		// ////////////////////////////////////////////////////////////////////
		// World 'objects'
		// ////////////////////////////////////////////////////////////////////		
		
		/**
		 * Define graphical parameters within the world (a series of platforms)
		 */
		private final int mNumPlatforms = 100;
		private Rect mPlatforms[] = new Rect[mNumPlatforms];
		private Bitmap mPlatformBitmap;
		
		/**
		 * Define the layer viewport region. A more fancy layerport class
		 * could have been defined that would offer move/focus methods.
		 */				
		private Rect mLayerViewport; 
				
		// ////////////////////////////////////////////////////////////////////
		// World construction and update
		// ////////////////////////////////////////////////////////////////////		
		
		/**
		 * Create the new world
		 */		
		public GameWorldPositiveYDown() {
			createViewports();
			loadAndPositionPlatforms();
		}

		/**
		 * Define the layer viewport
		 */		
		private void createViewports() {
			mLayerViewport = new Rect(0, 0, 300, 300);
		}

		/**
		 * Create and position the platforms
		 */		
		private void loadAndPositionPlatforms() {

			// Attempt to load the bitmap used for the platforms
			try {
				AssetManager assetManager = getActivity().getAssets();
				InputStream inputStream = assetManager.open("img/Platform.png");
				mPlatformBitmap = BitmapFactory.decodeStream(inputStream);
				inputStream.close();
			} catch (IOException e) {
				Log.d(getActivity().getResources().getString(R.string.LOG_TAG),
						"Load error: " + e.getMessage());
			}

			// Define the location of each platform
			for (int idx = 0; idx < mNumPlatforms; idx++) {
				int x = idx * mPlatformBitmap.getHeight();
				int y = idx * mPlatformBitmap.getWidth();
				mPlatforms[idx] = new Rect(x, y,
					x + mPlatformBitmap.getWidth(), 
					y + mPlatformBitmap.getHeight());
			}
		}

		/**
		 * Update the layer by moving the viewport along the x and y axis
		 */		
		public void update() {
			int scrollSpeed = 10;
			mLayerViewport.offset(scrollSpeed, (int)(scrollSpeed 
				* (float)mPlatformBitmap.getWidth() / (float)mPlatformBitmap.getHeight()));

			// Move the viewport back when we've reached the end
			if (mLayerViewport.right > mNumPlatforms * mPlatforms[0].width())
				mLayerViewport.offsetTo(0, 0);
		}

		
		// ////////////////////////////////////////////////////////////////////
		// World draw
		// ////////////////////////////////////////////////////////////////////		
		
		/**
		 * Define a source rectangle to hold the portion of the source bitmap that is
		 * to be drawn and a screen rectangle to hold the region we will draw to.
		 * IMPORTANT: Rect's are used (i.e. positive-y points down) as this is 
		 * assumed when drawing to a canvas.
		 */
				
		private Rect sourceRect = new Rect();
		private Rect screenRect = new Rect();
		
		/**
		 * Draw the world to the specified viewport defined on the specified canvas
		 * 
		 * @param canvas Canvas object on which to draw
		 * @param screenViewport Viewport on the canvas object to draw to
		 */		
		public void draw(Canvas canvas, Rect screenViewport) {

			// Determine the x- and y-aspect rations between the layer and screen viewports				
			float screenXScale = 
					(float) screenViewport.width() / (float) mLayerViewport.width(); 			
			float screenYScale = 
					(float) screenViewport.height() / (float) mLayerViewport.height();

			// Test each platfrom to see if it needs to be drawn
			for (int idx = 0; idx < mNumPlatforms; idx++) {

				// Check if some portion of the platform is visible within the layer viewport

				if (mPlatforms[idx].left < mLayerViewport.right &&
					mPlatforms[idx].right > mLayerViewport.left &&
					mPlatforms[idx].top < mLayerViewport.bottom &&
					mPlatforms[idx].bottom > mLayerViewport.top) {

					// At this point we know some portion of the platform is visible
					
					// Determining the scale factor for mapping the bitmap onto this 
					// platform. This should really be wrapped up inside a platform 
					// object and not recalculated ever single draw request. Note
					// the use of float casting to avoid integer maths.
					float sourceScaleWidth = 
							(float) mPlatformBitmap.getWidth() / (float) mPlatforms[idx].width();
					float sourceScaleHeight = 
							(float) mPlatformBitmap.getHeight() / (float) mPlatforms[idx].height();

					// Work out what region of the platform is visible within the viewport,
										
					float sourceX = Math.max(0.0f, 
							mLayerViewport.left - mPlatforms[idx].left);
					float sourceY = Math.max(0.0f, 
							mLayerViewport.top - mPlatforms[idx].top);

					float sourceWidth = ((mPlatforms[idx].width() - sourceX) - Math.max(0.0f, 
							mPlatforms[idx].right - mLayerViewport.right));
					float sourceHeight = ((mPlatforms[idx].height() - sourceY) - Math.max(0.0f, 
							mPlatforms[idx].bottom - mLayerViewport.bottom));

					// Set the region of the source bitmap we will draw (scaling as needed)										
					sourceRect.set(
							(int)(sourceX * sourceScaleWidth), (int)(sourceY * sourceScaleHeight),
							(int)((sourceX + sourceWidth) * sourceScaleWidth),
							(int)((sourceY + sourceHeight) * sourceScaleHeight));

					// Work out which region of the screen viewport (relative to the canvas) 
					// we will be drawing to. Assuming here that (0,0) is the top-left position
					// and positive y moves down the way.

					float screenX = screenViewport.left + Math.max(0.0f,
							(mPlatforms[idx].left - mLayerViewport.left)) * screenXScale;
					float screenY = screenViewport.top + Math.max(0.0f,
							(mPlatforms[idx].top - mLayerViewport.top)) * screenYScale;

					float screenWidth = sourceWidth * screenXScale;
					float screenHeight = sourceHeight * screenYScale;

					// Set the region to the canvas to which we will draw					
					screenRect.set((int) screenX, (int) screenY,
							(int) (screenX + screenWidth),
							(int) (screenY + screenHeight));

					// Draw to the screen
					canvas.drawBitmap(
							mPlatformBitmap, sourceRect, screenRect, null);
				}
			}
		}
	}

	/**
	 * Custom view object that will repeatedly update and display the game world.
	 */
	private class RenderView extends View {

		/**
		 * Define a total of four screen viewports 
		 */
		private Rect mScreenViewportTopLeft;
		private Rect mScreenViewportTopRight;
		private Rect mScreenViewportBottomLeft;
		private Rect mScreenViewportBottomRight;

		/**
		 * Create a new render view instance
		 * 
		 * @param context
		 *            Parent context
		 */
		public RenderView(Context context) {
			super(context);

			// Define the two viewports for the up world
			mScreenViewportTopLeft = new Rect(50, 50, 500, 100);
			mScreenViewportTopRight = new Rect(550, 50, 700, 200);

			// Define the two viewports for the down world
			mScreenViewportBottomLeft = new Rect(50, 550, 500, 1000);
			mScreenViewportBottomRight = new Rect(550, 550, 700, 700);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.view.View#onDraw(android.graphics.Canvas)
		 */
		@Override
		protected void onDraw(Canvas canvas) {

			// Cheeky - we should not call these in a draw method....
			mGameWorldYUp.update();
			mGameWorldYDown.update();

			// Draw the two up world viewports
			mGameWorldYUp.draw(canvas, mScreenViewportTopLeft);
			mGameWorldYUp.draw(canvas, mScreenViewportTopRight);

			// Draw the two down world viewports
			mGameWorldYDown.draw(canvas, mScreenViewportBottomLeft);
			mGameWorldYDown.draw(canvas, mScreenViewportBottomRight);

			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
			}

			// Invalid our canvas, so we'll be asked to redraw
			invalidate();
		}
	}
}
