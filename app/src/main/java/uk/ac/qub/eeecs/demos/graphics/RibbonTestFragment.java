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
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RibbonTestFragment extends Fragment {

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
	// World in which positive-y points upwards
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Define an equivalent to the Rect class, called Box, in which it is
	 * assumed that positive-y points up.
	 */
	private class Box {
		public float x; // Centre x location
		public float y; // Centre y location

		public float width; // Box width (we could(should) have used half width)
		public float height; // Box height (we could(should) have used half
								// height)

		public Box() {
			x = 0;
			y = 0;
			width = 1;
			height = 1;
		}

		public Box(float x, float y, float width, float height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
	}

	/**
	 * Define a simple game world
	 */
	private class GameWorld {

		// ////////////////////////////////////////////////////////////////////
		// World 'objects'
		// ////////////////////////////////////////////////////////////////////

		/**
		 * Define a collection of fish 'objects'
		 */
		private final int mNumFish = 100;
		private Box mFish[] = new Box[mNumFish];
		private Bitmap mFishBitmap;

		/**
		 * Define the image and bound used to hold the background
		 */
		private Box mBackgroundRibbon;
		private Bitmap mRibbonBitmap;

		/**
		 * Define the layer viewport region.
		 */
		private Box mLayerViewport;

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

			// Define the layer viewport
			mLayerViewport = new Box(500, 500, 1000, 1000);

			// Define the ribbon bound so it's a bit bigger than the viewport
			float ribbonWidth = mLayerViewport.width * 1.2f;
			mBackgroundRibbon = new Box(ribbonWidth / 2.0f, mLayerViewport.y,
					ribbonWidth, mLayerViewport.height);

			// Attempt to load the bitmap used for the fish and ribbon
			try {
				AssetManager assetManager = getActivity().getAssets();

				InputStream inputStream = assetManager.open("img/Fish.png");
				mFishBitmap = BitmapFactory.decodeStream(inputStream);
				inputStream.close();

				inputStream = assetManager.open("img/Ribbon.png");
				mRibbonBitmap = BitmapFactory.decodeStream(inputStream);
				inputStream.close();

			} catch (IOException e) {
				Log.d(getActivity().getResources().getString(R.string.LOG_TAG),
						"Load error: " + e.getMessage());
			}

			// Define the location of each fish
			Random random = new Random();
			for (int idx = 0; idx < mNumFish; idx++) {
				int x = (mFishBitmap.getWidth() + 100) * idx;
				int y = random.nextInt(1000);
				mFish[idx] = new Box(x, y, mFishBitmap.getWidth(),
						mFishBitmap.getHeight());
			}
		}

		/**
		 * Update the layer by moving the viewport along the x axis
		 */
		public void update() {
			float scrollSpeed = 10.0f;
			mLayerViewport.x += scrollSpeed;
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
		 * Draw the world to the specified viewport defined on the specified
		 * canvas
		 * 
		 * @param canvas
		 *            Canvas object on which to draw
		 * @param screenViewport
		 *            Viewport on the canvas object to draw to
		 */
		public void draw(Canvas canvas, Rect screenViewport) {

			// Draw the background ribbon
			drawBackgroundRibbon(canvas, screenViewport);

			// Draw the fish as needed.
			for (int idx = 0; idx < mNumFish; idx++) {
				// If visible get source and destination rects
				if (getSourceAndScreenRect(mFish[idx], mFishBitmap.getWidth(),
						mFishBitmap.getHeight(), mLayerViewport,
						screenViewport, sourceRect, screenRect)) {
					// Draw the fish
					canvas.drawBitmap(mFishBitmap, sourceRect, screenRect, null);
				}
			}
		}

		/**
		 * Draw the background ribbon
		 * 
		 * @param canvas
		 *            Canvas object on which to draw
		 * @param screenViewport
		 *            Viewport on the canvas object to draw to
		 */
		private void drawBackgroundRibbon(Canvas canvas, Rect screenViewport) {

			// Based on the layer viewport's location, set the x position of
			// the first ribbon image that falls within the viewport.

			int ribbonInset = (int) (mLayerViewport.x - mLayerViewport.width / 2)
					/ (int) mBackgroundRibbon.width;
			mBackgroundRibbon.x = ribbonInset * mBackgroundRibbon.width
					+ mBackgroundRibbon.width / 2.0f;

			// Draw this image
			getSourceAndScreenRect(mBackgroundRibbon, mRibbonBitmap.getWidth(),
					mRibbonBitmap.getHeight(), mLayerViewport, screenViewport,
					sourceRect, screenRect);
			canvas.drawBitmap(mRibbonBitmap, sourceRect, screenRect, null);

			// Check if we need to draw a second ribbon to fill-in the whole
			// viewport
			if (mBackgroundRibbon.x + mBackgroundRibbon.width / 2 < mLayerViewport.x
					+ mLayerViewport.width / 2) {

				// If so, move the ribbon x's location on by one image worth and
				// draw
				mBackgroundRibbon.x += mBackgroundRibbon.width;
				getSourceAndScreenRect(mBackgroundRibbon,
						mRibbonBitmap.getWidth(), mRibbonBitmap.getHeight(),
						mLayerViewport, screenViewport, sourceRect, screenRect);
				canvas.drawBitmap(mRibbonBitmap, sourceRect, screenRect, null);
			}
		}

		/**
		 * Determine a source bitmap Rect and destintation screen Rect if the
		 * specified entity bound falls within the layer's viewport.
		 * 
		 * Note: Ideally the entityBound, entityBitmapWidth and
		 * entityBitmapHeight properties would be wrapped up within an entity
		 * class that could be passed to this method.
		 * 
		 * @param entityBound
		 *            Bounding box of the entity to check against the layer
		 *            viewport
		 * @param entityBitmapWidth
		 *            Width of the bitmap to be used for drawing the entity
		 * @param entityBitmapHeight
		 *            Height of the bitmap to be used for drawing the entity
		 * @param layerViewport
		 *            Layer viewport region to check the entity against
		 * @param screenViewport
		 *            Screen viewport region that will be used to draw the
		 * @param sourceRect
		 *            Output Rect holding the region of the bitmap to draw
		 * @param screenRect
		 *            Output Rect holding the region of the screen to draw to
		 * @return boolean true if the entity is visible, false otherwise
		 */
		private boolean getSourceAndScreenRect(Box entityBound,
				int entityBitmapWidth, int entityBitmapHeight,
				Box layerViewport, Rect screenViewport, Rect sourceRect,
				Rect screenRect) {

			// Determine if the entity falls within the layer viewport
			if (entityBound.x - entityBound.width / 2 < layerViewport.x
					+ layerViewport.width / 2
					&& entityBound.x + entityBound.width / 2 > layerViewport.x
							- layerViewport.width / 2
					&& entityBound.y - entityBound.height / 2 < layerViewport.y
							+ layerViewport.height / 2
					&& entityBound.y + entityBound.height / 2 > layerViewport.y
							- layerViewport.height / 2) {

				// Work out what region of the entity is visible within the
				// layer viewport,

				float sourceX = Math.max(0.0f,
						(layerViewport.x - layerViewport.width / 2)
								- (entityBound.x - entityBound.width / 2));
				float sourceY = Math.max(0.0f,
						(entityBound.y + entityBound.height / 2)
								- (layerViewport.y + layerViewport.height / 2));

				float sourceWidth = ((entityBound.width - sourceX) - Math.max(
						0.0f, (entityBound.x + entityBound.width / 2)
								- (layerViewport.x + layerViewport.width / 2)));
				float sourceHeight = ((entityBound.height - sourceY) - Math
						.max(0.0f, (layerViewport.y - layerViewport.height / 2)
								- (entityBound.y - entityBound.height / 2)));

				// Determining the scale factor for mapping the bitmap onto this
				// rect and set the sourceRect value.

				float sourceScaleWidth = (float) entityBitmapWidth
						/ (float) entityBound.width;
				float sourceScaleHeight = (float) entityBitmapHeight
						/ (float) entityBound.height;

				sourceRect.set((int) (sourceX * sourceScaleWidth),
						(int) (sourceY * sourceScaleHeight),
						(int) ((sourceX + sourceWidth) * sourceScaleWidth),
						(int) ((sourceY + sourceHeight) * sourceScaleHeight));

				// Determine =which region of the screen viewport (relative to
				// the canvas)
				// we will be drawing to.

				// Determine the x- and y-aspect rations between the layer and
				// screen viewports
				float screenXScale = (float) screenViewport.width()
						/ (float) layerViewport.width;
				float screenYScale = (float) screenViewport.height()
						/ (float) layerViewport.height;

				float screenX = screenViewport.left
						+ screenXScale
						* Math.max(
								0.0f,
								((entityBound.x - entityBound.width / 2) - (layerViewport.x - layerViewport.width / 2)));
				float screenY = screenViewport.top
						+ screenYScale
						* Math.max(
								0.0f,
								((layerViewport.y + layerViewport.height / 2) - (entityBound.y + entityBound.height / 2)));

				// Set the region to the canvas to which we will draw
				screenRect.set((int) screenX, (int) screenY,
						(int) (screenX + sourceWidth * screenXScale),
						(int) (screenY + sourceHeight * screenYScale));

				return true;
			}

			// Not visible
			return false;
		}
	}

	/**
	 * Custom view object that will repeatedly update and display the game
	 * world.
	 */
	private class RenderView extends View {

		/**
		 * Define the screen viewport
		 */
		private Rect mScreenViewport;

		/**
		 * Create a new render view instance
		 * 
		 * @param context
		 *            Parent context
		 */
		public RenderView(Context context) {
			super(context);

			// Assuming we're running full screen, size accordingly
			DisplayMetrics metrics = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay()
					.getMetrics(metrics);
			int size = Math.min(metrics.heightPixels, metrics.widthPixels);

			// Create the screen viewports
			mScreenViewport = new Rect(50, 50, size - 50, size - 50);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.view.View#onDraw(android.graphics.Canvas)
		 */
		@Override
		protected void onDraw(Canvas canvas) {

			// Cheeky - we should not call these in a draw method....
			mGameWorld.update();

			// Draw the world
			mGameWorld.draw(canvas, mScreenViewport);

			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
			}

			// Invalid our canvas, so we'll be asked to redraw
			invalidate();
		}
	}
}
