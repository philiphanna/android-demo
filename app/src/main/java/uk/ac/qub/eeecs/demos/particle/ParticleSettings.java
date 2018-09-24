package uk.ac.qub.eeecs.demos.particle;

/**
 * Configuration settings - including a number of predefined systems
 * 
 * @version 1.0
 */
public class ParticleSettings {

	public enum Systems {
		Smoke, Explosion, Particle
	};
	
	
	/**
	 * Smoke effect
	 */
	public static ParticleSettings Smoke = new ParticleSettings(
			"img/Smoke.png",				// textureFilename
			false,							// additiveBlend
			EmissionMode.Burst,				// emissionMode
			0.5f, 							// minBurstTime
			1.0f,							// maxBurstTime
			AccelerationMode.NonAligned, 	// accelerationMode
			-110.0f, 						// minAccelerationDirection;
			-70.0f, 						// maxAccelerationDirection;
			50.0f,							// minAccelerationMagnitude;
			50.0f, 							// maxAccelerationMagnitude;
			0,								// gravityX
			0, 								// gravityY
			4,								// minNumParticles
			16, 							// maxNumParticles
			20, 							// minInitialSpeed
			100, 							// maxInitialSpeed
			-22.5f, 						// minAngularVelocity
			22.5f, 							// maxAngularVelocity
			-110, 							// maxOrientationAngle
			-70, 							// maxDirectionAngle
			5, 								// minLifespan
			7, 								// maxLifespan
			0.5f, 							// minSize
			1.0f, 							// maxSize
			0.1f, 							// minScaleGrowth
			0.5f 							// maxScaleGrowth
	);

	/**
	 * Fireball explosion effect
	 */
	public static ParticleSettings Explosion = new ParticleSettings(
			"img/Explosion.png", 			// textureFilename;
			true, 							// additiveBlend;
			EmissionMode.Burst, 			// emissionMode
			1.0f, 							// minBurstTime
			1.0f, 							// maxBurstTime
			AccelerationMode.Aligned, 		// accelerationMode;
			0.0f, 							// minAccelerationDirection;
			0.0f, 							// maxAccelerationDirection;
			-750.0f,						// minAccelerationMagnitude;
			-760.0f, 						// maxAccelerationMagnitude;
			0.0f, 							// gravityX;
			0.0f, 							// gravityY;
			50, 							// minNumParticles;
			60, 							// maxNumParticles;
			400, 							// minInitialSpeed;
			500, 							// maxInitialSpeed;
			-90.0f, 						// minAngularVelocity;
			90.0f, 							// maxAngularVelocity;
			0, 								// minOrientationAngle;
			360, 							// maxOrientationAngle;
			0.5f, 							// minLifespan;
			1.0f, 							// maxLifespan;
			0.3f, 							// minSize;
			1.0f, 							// maxSize;
			0.1f, 							// minScaleGrowth
			0.2f 							// maxScaleGrowth
	);

	/**
	 * Dropping particles effect
	 */
	public static ParticleSettings Particle = new ParticleSettings(
			"img/Particle.png", 			// textureFilename;
			false, 							// additiveBlend;
			EmissionMode.Continuous, 		// emissionMode
			0.05f, 							// minBurstTime
			0.05f, 							// maxBurstTime
			AccelerationMode.NonAligned, 	// accelerationMode;
			85.0f, 							// minAccelerationDirection;
			95.0f, 							// maxAccelerationDirection;
			3.0f,							// minAccelerationMagnitude;
			4.0f, 							// maxAccelerationMagnitude;
			0.0f, 							// gravityX;
			9.8f, 							// gravityY;
			15, 							// minNumParticles;
			20, 							// maxNumParticles;
			50, 							// minInitialSpeed;
			100, 							// maxInitialSpeed;
			0.0f, 							// minAngularVelocity;
			45.0f, 							// maxAngularVelocity;
			-135, 							// minOrientationAngle;
			-45, 							// maxOrientationAngle;
			2.0f, 							// minLifespan;
			3.0f, 							// maxLifespan;
			1.0f, 							// minSize;
			2.0f, 							// maxSize;
			0.5f, 							// minScaleGrowth
			1.0f 							// maxScaleGrowth
	);

	public enum AccelerationMode {
		Aligned, NonAligned
	};

	public enum EmissionMode {
		Burst, Continuous
	}

	// /////////////////////////////////////////////////////////////////////////
	// Particle Settings
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Texture to be used when drawing this particle system
	 */
	public String textureFilename;

	/**
	 * Define if particles are to be combined using normal alpha blending or
	 * additive blending
	 */
	public boolean additiveBlend;

	/**
	 * Define how many particles are emitted - are they emitted in a continuous
	 * manner over time, or in a sudden burst.
	 */
	public EmissionMode emissionMode;

	public float minBurstTime;
	public float maxBurstTime;

	public int minNumParticles;
	public int maxNumParticles;

	/**
	 * Define how the particles are subject to acceleration. If the acceleration
	 * mode is aligned, then particle accelerate in the same direction as their
	 * velocity. If non aligned then the acceleration direction is randomly
	 * selected within the define extents. A constant x and y gravitational
	 * acceleration can be defined.
	 */

	public AccelerationMode accelerationMode;

	public float minAccelerationDirection;
	public float maxAccelerationDirection;

	public float minAccelerationMagnitude;
	public float maxAccelerationMagnitude;

	public float gravityX;
	public float gravityY;

	/**
	 * Define the linear and rotational speed of the particle, alongside its
	 * starting direction.
	 */
	
	public float minInitialSpeed;
	public float maxInitialSpeed;

	public float minAngularVelocity;
	public float maxAngularVelocity;

	public float minOrientationAngle;
	public float maxOrientationAngle;

	/**
	 * Define how long each particle will live
	 */
	
	public float minLifespan;
	public float maxLifespan;

	/**
	 * Define how each particle will scale over time.
	 */
	
	public float minScale;
	public float maxScale;

	public float minScaleGrowth;
	public float maxScaleGrowth;

	// /////////////////////////////////////////////////////////////////////////
	// Constructor
	// /////////////////////////////////////////////////////////////////////////
		
	/**
	 * Create a new set of particle settings
	 * 
	 * @param textureFilename Texture filename
	 * @param additiveBlend Use additive blending
	 * @param emissionMode How are the particle emitted
	 * @param minBurstTime Minimum time between bursts
	 * @param maxBurstTime Maximum time between bursts
	 * @param accelerationMode Acceleration mode for the particles
	 * @param minAccelerationDirection Min acceleration direction
	 * @param maxAccelerationDirection Max acceleration direction
	 * @param minAccelerationMagnitude Min acceleration magnitude
	 * @param maxAccelerationMagnitude Max acceleration magnitude
	 * @param gravityX Constant x gravity value
	 * @param gravityY Constant y gravity value
	 * @param minNumParticles Minimum number of particles per burst/period
	 * @param maxNumParticles Maximum number of particles per burst/period
	 * @param minInitialSpeed Min initial speed of each particle
	 * @param maxInitialSpeed Max initial speed of each particle
	 * @param minAngularVelocity Min initial rotational speed of each particle
	 * @param maxAngularVelocity Max initial rotational speed of each particle
	 * @param minOrientationAngle Min initial direction of each particle
	 * @param maxOrientationAngle Max initial direction of each particle
	 * @param minLifespan Min lifespan for each particle
	 * @param maxLifespan Max lifespan for each particle
	 * @param minScale Min scale
	 * @param maxScale Max scale
	 * @param minScaleGrowth Min growth factor applied to the scale value
	 * @param maxScaleGrowth Max growth factor applied to the scale value
	 */
	public ParticleSettings(String textureFilename, boolean additiveBlend,
			EmissionMode emissionMode, float minBurstTime, float maxBurstTime,
			AccelerationMode accelerationMode, float minAccelerationDirection,
			float maxAccelerationDirection, float minAccelerationMagnitude,
			float maxAccelerationMagnitude, float gravityX, float gravityY,
			int minNumParticles, int maxNumParticles, float minInitialSpeed,
			float maxInitialSpeed, float minAngularVelocity,
			float maxAngularVelocity, float minOrientationAngle,
			float maxOrientationAngle, float minLifespan, float maxLifespan,
			float minScale, float maxScale, float minScaleGrowth,
			float maxScaleGrowth) {

		// Store the passed parameters
		
		this.textureFilename = textureFilename;
		this.additiveBlend = additiveBlend;
		this.emissionMode = emissionMode;
		
		this.minBurstTime = minBurstTime;
		this.maxBurstTime = maxBurstTime;

		this.accelerationMode = accelerationMode;

		this.minAccelerationDirection = minAccelerationDirection;
		this.maxAccelerationDirection = maxAccelerationDirection;
		this.minAccelerationMagnitude = minAccelerationMagnitude;
		this.maxAccelerationMagnitude = maxAccelerationMagnitude;

		this.gravityX = gravityX;
		this.gravityY = gravityY;

		this.minNumParticles = minNumParticles;
		this.maxNumParticles = maxNumParticles;

		this.minInitialSpeed = minInitialSpeed;
		this.maxInitialSpeed = maxInitialSpeed;

		this.minAngularVelocity = minAngularVelocity;
		this.maxAngularVelocity = maxAngularVelocity;

		this.minOrientationAngle = minOrientationAngle;
		this.maxOrientationAngle = maxOrientationAngle;

		this.minLifespan = minLifespan;
		this.maxLifespan = maxLifespan;

		this.minScale = minScale;
		this.maxScale = maxScale;

		this.minScaleGrowth = minScaleGrowth;
		this.maxScaleGrowth = minScaleGrowth;
	}
}
