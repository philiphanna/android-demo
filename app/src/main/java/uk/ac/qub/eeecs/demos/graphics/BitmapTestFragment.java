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

public class BitmapTestFragment extends Fragment {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Create a custom view object that will provide various bitmap drawing
		// examples
		return new RenderView(getActivity());
	}

	/**
	 * Custom view object that will display a range of bitmap drawing options.
	 * The view will automatically invalidate itself so that it is continuously
	 * redrawn as a means of providing an animated display.
	 */
	private class RenderView extends View {

		/**
		 * Bitmap image that will be drawn
		 */
		private Bitmap mImage;

		/**
		 * Paint instance for the bitmap
		 */
		private Paint mPaint;

		/**
		 * Source and destination rectangles
		 */
		private Rect source = new Rect();
		private Rect destination = new Rect();

		/**
		 * Matrix instance that will be used to demonstrate render options
		 */
		private Matrix matrix = new Matrix();

		/**
		 * Variables that will control bitmap animation
		 */
		private float scale = 1.0f;
		private float rotation = 0.0f;
		private float offset = 0.0f;
		private float scaleDir = 1.0f, offsetDir = 1.0f;

		/**
		 * Create a new render view instance
		 * 
		 * @param context
		 *            Parent context
		 */
		public RenderView(Context context) {
			super(context);

			// Create a new paint object
			mPaint = new Paint();

			// Attempt to load the bitmap
			try {
				AssetManager assetManager = getActivity().getAssets();
				InputStream inputStream = assetManager
						.open("img/ARGB_8888_Small.png");
				mImage = BitmapFactory.decodeStream(inputStream);
				inputStream.close();
			} catch (IOException e) {
				Log.d(getActivity().getResources().getString(R.string.LOG_TAG),
						"Load error: " + e.getMessage());
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.view.View#onDraw(android.graphics.Canvas)
		 */
		@Override
		protected void onDraw(Canvas canvas) {

			// ////////////////////////////////////////////////////////////////
			// Drawing without using a matrix
			// ////////////////////////////////////////////////////////////////

			/**
			 * Draw a bitmap at a specific location
			 */

			// Draw the bitmap at (top-left) location [150,50]
			canvas.drawBitmap(mImage, 50.0f, 50.f, mPaint);

			/**
			 * Drawing a bitmap with the specified alpha multiplier - this can
			 * be used for fading objects in or out, particle effects, etc.
			 */

			// Draw two bitmap at (top-left) locations [200,50], [245, 70] with
			// 50% alpha
			mPaint.setAlpha(128);
			canvas.drawBitmap(mImage, 200.0f, 50.f, mPaint);
			canvas.drawBitmap(mImage, 245.0f, 70.f, mPaint);
			mPaint.setAlpha(255); // Reset the alpha value

			/**
			 * Draw the specified portion of the image at the specified location
			 * (potentially scaled). Commonly used to extract a portion of a
			 * sprite sheet, e.g. a frame of animation. The second rectangle is
			 * flipped along the x-axis, also commonly used in animation to
			 * 'flip' the frames of an animation from left-to-right (or
			 * right-to-left).
			 */
			// Draw the middle [50,50] of the bitmap at (top-left) location
			// [400,50]
			source.set(25, 25, 75, 75);
			destination.set(400, 50, 450, 100);
			canvas.drawBitmap(mImage, source, destination, mPaint);
			// Draw the source flipped now
			source.set(75, 25, 25, 75);
			destination.set(475, 50, 525, 100);
			canvas.drawBitmap(mImage, source, destination, mPaint);

			// ////////////////////////////////////////////////////////////////
			// Drawing using a matrix
			// ////////////////////////////////////////////////////////////////

			/**
			 * Matrix scaling (you could also use the source/destination
			 * rectangle to achieve the same effect)
			 */

			// Draw with a 50% x and y scale at the specified location
			matrix.reset();
			matrix.setScale(0.5f, 0.5f);
			matrix.postTranslate(50.0f, 200.0f);
			canvas.drawBitmap(mImage, matrix, mPaint);

			// Draw with a 200% x and y scale at the specified location
			matrix.reset();
			matrix.setScale(2.0f, 2.0f);
			matrix.postTranslate(150.0f, 200.0f);
			canvas.drawBitmap(mImage, matrix, mPaint);

			/**
			 * Matrix rotation (three images, with alpha blending, rotated about
			 * the top-left corner and centre of the image.
			 */

			// Draw with clockwise rotations of 15, 30 and 45 degrees. The
			// rotation occurs about point [0,0], before translation this point
			// corresponds to the top-left corner of the bitmap
			matrix.reset();
			matrix.setRotate(15.0f);
			matrix.postTranslate(475.0f, 200.0f);
			canvas.drawBitmap(mImage, matrix, mPaint);
			matrix.reset();
			matrix.setRotate(30.0f);
			matrix.postTranslate(475.0f, 200.0f);
			canvas.drawBitmap(mImage, matrix, mPaint);
			matrix.reset();
			matrix.setRotate(45.0f);
			matrix.postTranslate(475.0f, 200.0f);
			canvas.drawBitmap(mImage, matrix, mPaint);

			// Draw with clockwise rotations of 15, 30 and 45 degrees. The
			// rotation occurs about point [50,50], before translation this
			// point corresponds to the middle of the bitmap
			matrix.reset();
			matrix.setRotate(15.0f, 50.0f, 50.0f);
			matrix.postTranslate(625.0f, 200.0f);
			canvas.drawBitmap(mImage, matrix, mPaint);
			matrix.reset();
			matrix.setRotate(30.0f, 50.0f, 50.0f);
			matrix.postTranslate(625.0f, 200.0f);
			canvas.drawBitmap(mImage, matrix, mPaint);
			matrix.reset();
			matrix.setRotate(45.0f, 50.0f, 50.0f);
			matrix.postTranslate(625.0f, 200.0f);
			canvas.drawBitmap(mImage, matrix, mPaint);

			/**
			 * Matrix skewing - not used that often
			 */

			// Skew by 0.1 and 0.2 on the x- and y-axis
			matrix.reset();
			matrix.setSkew(0.1f, 0.2f);
			matrix.postTranslate(50.0f, 450.0f);
			canvas.drawBitmap(mImage, matrix, mPaint);

			/**
			 * The real power of matrices - combining various effects together -
			 * Best to use the ISROT series of multiplications to get the right
			 * combinations. I - Identity - start with the identity matrix (no
			 * change), S - Scale - then scale, R - Rotate - then rotate about
			 * the centre of the object, O - Orbit - then rotate about a
			 * separate orbit point (if desired), T - Translate - then finally
			 * translate to the target world point
			 * 
			 * We need to be careful about combining matricies as AB is not the
			 * same as BA Use ISROT to build up the matrices, applying a post
			 * effect (postScale, etc.)
			 */

			// Update the scale, rotation and offset values
			scale += scaleDir * 0.02f;
			if (scale < 0.75f)
				scaleDir = 1.0f;
			else if (scale > 1.25f)
				scaleDir = -1.0f;
			offset += offsetDir * 5.0f;
			if (offset < 0)
				offsetDir = 1.0f;
			else if (offset > 400)
				offsetDir = -1.0f;
			rotation += 2.0f;

			matrix.reset();
			matrix.setScale(scale, scale); // Scale the image
			matrix.postRotate(rotation, 50.0f, 50.0f); // Centre point rotation
			// matrix.postRotate(rotation, 200.0f, 200.0f); // We could rotate about an external point if desired
			matrix.postTranslate(150.0f + offset, 700.0f); // Finally translate
			canvas.drawBitmap(mImage, matrix, mPaint);

			try {
				// Go to sleep for 20ms (i.e. target 50FPS)
				Thread.sleep(20);
			} catch (InterruptedException e) {
			}

			// Invalid our canvas, so we'll be asked to redraw
			invalidate();
		}
	}
}
