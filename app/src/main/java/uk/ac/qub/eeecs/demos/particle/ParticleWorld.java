package uk.ac.qub.eeecs.demos.particle;

import java.io.IOException;
import java.io.InputStream;

import uk.ac.qub.eeecs.demos.R;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

/**
 * Simple world containing a single particle system (this would normally be the
 * game world or game screen).
 * 
 * @version 1.0
 */
public class ParticleWorld {

	// /////////////////////////////////////////////////////////////////////////
	// Properties
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Particle system
	 */
	private ParticleSettings.Systems mCurrentParticleSystem
				= ParticleSettings.Systems.Explosion;
	private ParticleEmitter mParticleEmitter;

	/**
	 * Next particle system control
	 */
	private boolean mNextParticleSystemTrigger = true;
	private Rect mNextParticleSystemRegion;
	private Bitmap mNextParticleSystemBitmap;

	// /////////////////////////////////////////////////////////////////////////
	// Constructor
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Create a new world containing a particle system
	 * 
	 * @param context
	 */
	public ParticleWorld(Context context) {

		// Setup the particle system
		mCurrentParticleSystem = ParticleSettings.Systems.Explosion;
		mParticleEmitter = new ParticleEmitter(context, ParticleSettings.Explosion);
		
		// Load the next particle system button
		try {
			AssetManager assetManager = context.getAssets();
			InputStream inputStream = assetManager.open("img/NextArrow.png");
			mNextParticleSystemBitmap = BitmapFactory.decodeStream(inputStream);
			inputStream.close();

			mNextParticleSystemRegion = new Rect(50, 50,
					mNextParticleSystemBitmap.getWidth() + 50,
					mNextParticleSystemBitmap.getHeight() + 50);

		} catch (IOException e) {
			Log.d(context.getResources().getString(R.string.LOG_TAG),
					"Load error: " + e.getMessage());
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// Methods
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * Update the world
	 * 
	 * @param elapsedTime
	 *            Elapsed time information
	 * @param touchLocation
	 *            Location at which a touch event occurred
	 */
	public void update(float elapsedTime, Vector2 touchLocation) {
		// Swap the particle system if needed
		if(mNextParticleSystemTrigger && 
				mNextParticleSystemRegion.contains(
						(int)touchLocation.x, (int)touchLocation.y)) {
			setupNextParticleSystem();			
			mNextParticleSystemTrigger = false;
		} else mNextParticleSystemTrigger = true;
				
		mParticleEmitter.update(elapsedTime, touchLocation);
	}

	/**
	 * Change to the next defined particle system
	 */
	private void setupNextParticleSystem() {
		
		switch(mCurrentParticleSystem) {		
			case Smoke: // Go onto Explosion
				mCurrentParticleSystem = ParticleSettings.Systems.Explosion;
				mParticleEmitter.setParticleSettings(ParticleSettings.Explosion);
				break;
			case Explosion: // Go onto Particle
				mCurrentParticleSystem = ParticleSettings.Systems.Particle;
				mParticleEmitter.setParticleSettings(ParticleSettings.Particle);
				break;
			case Particle: // Go onto Smoke
				mCurrentParticleSystem = ParticleSettings.Systems.Smoke;
				mParticleEmitter.setParticleSettings(ParticleSettings.Smoke);
				break;		
		}
	}
	
	/**
	 * Draw the world
	 * 
	 * @param canvas
	 *            Canvas to use to draw the world
	 * @param elapsedTime
	 *            Elapsed time information
	 */
	public void draw(Canvas canvas, float elapsedTime) {

		canvas.drawColor(Color.BLACK);
		mParticleEmitter.draw(canvas, elapsedTime);
		canvas.drawBitmap(mNextParticleSystemBitmap, null, mNextParticleSystemRegion, null);
	}
}
