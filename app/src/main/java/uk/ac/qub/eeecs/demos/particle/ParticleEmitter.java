package uk.ac.qub.eeecs.demos.particle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import uk.ac.qub.eeecs.demos.R;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.Log;

/**
 * Emitter for a single particle system
 * 
 * @version 1.0
 */
public class ParticleEmitter {

	// /////////////////////////////////////////////////////////////////////////
	// Properties: 
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Static random instance used by all emitters to configure their particles
	 */	
	private static Random random = new Random();

	/**
	 * Return a random between between the specified min and max
	 * 
	 * @param min Minimum value
	 * @param max Maximum value
	 * @return Value in the specified range
	 */
	private static float randomBetween(float min, float max) {
		return min + random.nextFloat() * (max - min);
	}

	/**
	 * Return a random direction between the specified min and max
	 * 
	 * @param min Minimum value
	 * @param max Maximum value
	 * @param outputVector Vector within which the direction will be stored
	 */
	private static void pickRandomDirection(float min, float max, Vector2 outputVector) {
		float angle = randomBetween(min, max);
		// our settings angles are in degrees, so we must convert to radians
		angle = (float) Math.toRadians(angle);
		outputVector.set((float) Math.cos(angle), (float) Math.sin(angle));
	}
	
	/**
	 * Context to which this particle emitter belongs
	 */
	private Context context;

	/**
	 * Bitmap used by this particle system
	 */
	private Bitmap texture;

	/**
	 * Center point of the bitmap (used as a reference point when rotating the bitmap or
	 * when drawing the bitmap centered on the particle location)
	 */
	private Vector2 textureCenter;

	/**
	 * Paint instance used when drawing the particle
	 */
	private Paint paint;

	/**
	 * Amount of time before the next batch of particles needs to be created
	 */
	private float mTimeToBurst = 0.0f;
	
	/**
	 * Previous location at which the last batch of particles was created
	 */
	private Vector2 lastLocation = new Vector2();

	/**
	 * Linked list of active particles currently evolving
	 */
	private LinkedList<Particle> activeParticles;

	/**
	 * Linked list of available inactive particles that an be used if needed
	 */
	private LinkedList<Particle> freeParticles;

	/**
	 * Settings that are used to drive the particle system
	 */
	private ParticleSettings mParticleSettings;
	
	// /////////////////////////////////////////////////////////////////////////
	// Constructors
	// /////////////////////////////////////////////////////////////////////////
	
	/** 
	 * Create a new particle emitter using the specified settings
	 * 
	 * @param context Context to which this particle system belongs
	 * @param particleSettings Settings used to drive this emitter
	 */
	public ParticleEmitter(Context context, ParticleSettings particleSettings) {
		this.context = context;
		mParticleSettings = particleSettings;

		activeParticles = new LinkedList<Particle>();
		freeParticles = new LinkedList<Particle>();

		// Create an initial pool of inactive particles that can be used
		int initialSize = 100;
		for (int i = 0; i < initialSize; i++) {
			Particle particle = new Particle();
			freeParticles.add(particle);
		}

		// Based on the specified settings, configure the emitter
		configure();
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// Methods: Configuration
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Change the particle settings used by this emitter
	 * 	
	 * @param particleSettings New particle settings to use
	 */
	public void setParticleSettings(ParticleSettings particleSettings) {
		// Store the settings
		mParticleSettings = particleSettings;

		// Release all current active particles
		Iterator<Particle> iterator = activeParticles.iterator();
		while (iterator.hasNext()) {
			Particle particle = iterator.next();
			iterator.remove();
			freeParticles.add(particle);
		}

		// Configure the emitter based on the specified settings
		configure();
	}

	/**
	 * Configure the emitter based on the specified settings
	 */
	protected void configure() {

		// Load the bitmap used for the particles
		try {
			AssetManager assetManager = context.getAssets();
			InputStream inputStream = assetManager
					.open(mParticleSettings.textureFilename);
			texture = BitmapFactory.decodeStream(inputStream);
			inputStream.close();
		} catch (IOException e) {
			Log.d(context.getResources().getString(R.string.LOG_TAG),
					"Load error: " + e.getMessage());
		}

		// Store the center points (to rotate, to offset when drawing)
		textureCenter = new Vector2(
				texture.getWidth() / 2.0f, texture.getHeight() / 2.0f);

		// Change the alpha blending (normal or additive) as specified in the settings
		paint = new Paint();
		paint.setAntiAlias(true);
		if (mParticleSettings.additiveBlend)
			paint.setXfermode(new PorterDuffXfermode(Mode.ADD));
	}

	/**
	 * Vector2 object reused when adding particles
	 */
	private Vector2 particlePosition = new Vector2();
	private Vector2 particleOffset = new Vector2();
	
	/**
	 * Add a new batch of particles at the specified location (or spaced between
	 * the last and current location, as appropriate depending upon the emitter settings).
	 * 
	 * @param location Current location
	 * @param lastLocation Last location
	 */
	public void addParticles(Vector2 location, Vector2 lastLocation) {

		// Determine the number of particles to be added
		int numParticles = (int) randomBetween(
				mParticleSettings.minNumParticles, mParticleSettings.maxNumParticles);

		// Setup the location and offset depending upon the burst mode
		switch (mParticleSettings.emissionMode) {
		case Burst:
			particlePosition.set(location);
			particleOffset.set(Vector2.Zero);
			break;
		case Continuous:
			particlePosition.set(lastLocation);
			particleOffset.set((location.x - lastLocation.x) / numParticles,
					(location.y - lastLocation.y) / numParticles);
			break;
		}

		// Initialise and add the particles
		for (int i = 0; i < numParticles; i++) {

			Particle p;
			if (freeParticles.size() == 0) {
				p = new Particle();
			} else {
				p = freeParticles.removeLast();
			}

			initialiseParticle(p, particlePosition);
			activeParticles.add(p);

			particlePosition.x += particleOffset.x;
			particlePosition.y += particleOffset.y;
		}
	}

	/**
	 * Vector2 objects used when initialising particles
	 */
	private Vector2 direction = new Vector2();
	private Vector2 velocity = new Vector2();
	private Vector2 acceleration = new Vector2();
	
	/**
	 * Initialise the particle
	 * 
	 * @param particle Particle to initialise
	 * @param position Location of the particle
	 */
	private void initialiseParticle(Particle particle, Vector2 position) {

		// Determine the orientation and speed
		pickRandomDirection(
				mParticleSettings.minOrientationAngle,
				mParticleSettings.maxOrientationAngle, direction);
		float speed = randomBetween(mParticleSettings.minInitialSpeed,
				mParticleSettings.maxInitialSpeed);

		// Define the velocity
		velocity.x = direction.x * speed;
		velocity.y = direction.y * speed;

		// Define the life span
		float lifeSpan = randomBetween(
				mParticleSettings.minLifespan, mParticleSettings.maxLifespan);

		// Determine the orientation and angular velocity
		float orientation = randomBetween(0.0f, (float) Math.PI * 2.0f);
		float angularVelocity = randomBetween(
				mParticleSettings.minAngularVelocity,
				mParticleSettings.maxAngularVelocity);

		// Determine the scale and scale growth
		float scale = randomBetween(mParticleSettings.minScale,
				mParticleSettings.maxScale);
		float scaleGrowth = randomBetween(
				mParticleSettings.minScaleGrowth,
				mParticleSettings.minScaleGrowth);

		// Define the particle acceleration
		switch (mParticleSettings.accelerationMode) {
		case Aligned:
			// Randomly pick an acceleration using the direction and
			// the minAcceleration/maxAcceleration values
			float accelerationMagnitude = randomBetween(
					mParticleSettings.minAccelerationMagnitude,
					mParticleSettings.maxAccelerationMagnitude);
			acceleration.x = direction.x * accelerationMagnitude;
			acceleration.y = direction.y * accelerationMagnitude;
			break;
		case NonAligned:
			// Select an acceleration in a random direction and magnitude
			// using the defined min and max values
			pickRandomDirection(
					mParticleSettings.minAccelerationDirection,
					mParticleSettings.maxAccelerationDirection, acceleration);
			accelerationMagnitude = randomBetween(
					mParticleSettings.minAccelerationMagnitude,
					mParticleSettings.maxAccelerationMagnitude);
			acceleration.x = direction.x * accelerationMagnitude;
			acceleration.y = direction.y * accelerationMagnitude;
			break;
		default:
			break;
		}

		// Initialise the particle
		particle.initialize(position, velocity, acceleration, orientation,
				angularVelocity, scale, scaleGrowth, lifeSpan);
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// Methods: Update and Draw
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Update the emitter, creating a new burst of particles if needed and 
	 * updating all active particles, removing/storing those that have
	 * become inactive.
	 * 
	 * @param elapsedTime Elapsed time 
	 * @param location Last touch location
	 */
	public void update(float elapsedTime, Vector2 location) {

		// Check and create a new burst of particles if needed
		if (mTimeToBurst > 0.0f)
			mTimeToBurst -= elapsedTime;
		else {
			mTimeToBurst = randomBetween(mParticleSettings.minBurstTime,
					mParticleSettings.maxBurstTime);
			addParticles(location, lastLocation);
			lastLocation.set(location);
		}

		// Update all active particles and remove/store those that have become inactive
		Iterator<Particle> iterator = activeParticles.iterator();
		while (iterator.hasNext()) {
			Particle particle = iterator.next();

			// Add in gravity, if required
			particle.velocity.add(
					mParticleSettings.gravityX,
					mParticleSettings.gravityY);

			// Update the particle
			particle.update(elapsedTime);

			// Remove and store if inactive
			if (!particle.isAlive()) {
				iterator.remove();
				freeParticles.add(particle);
			}
		}
	}

	/**
	 * Matrix used to draw particles
	 */
	private Matrix matrix = new Matrix();
	
	/**
	 * Draw all active particles
	 * 
	 * @param canvas Canvas on which to draw to
	 * @param gameTime Elapsed time since the last draw
	 */
	public void draw(Canvas canvas, float gameTime) {

		for (Particle p : activeParticles) {

			// Avoid having particles pop in and out by using a fade in at
			// the start of the life span and a fade out at the end.
			// An alpha of 100% occurs mid-span and then fades out

			float normalizedLifetime = p.timeSinceBirth / p.lifeSpan;
			float alpha = 4.0f * normalizedLifetime * (1 - normalizedLifetime);
			paint.setAlpha((int) (alpha * 255));

			matrix.reset();
			matrix.setScale(p.scale, p.scale);
			matrix.postRotate(p.orientation, textureCenter.x, textureCenter.y);
			matrix.postTranslate(p.position.x - textureCenter.x, p.position.y - textureCenter.y);
			
			canvas.drawBitmap(texture, matrix, paint);
		}
	}
}