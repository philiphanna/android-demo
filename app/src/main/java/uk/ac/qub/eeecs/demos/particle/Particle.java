package uk.ac.qub.eeecs.demos.particle;

/**
 * Single particle instance
 * 
 * @version 1.0
 */
public class Particle {
	
	// /////////////////////////////////////////////////////////////////////////
	// Properties: [[Declared public for speed of access]]
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Position of the particle
	 */
	public Vector2 position = new Vector2();
	
	/**
	 * Velocity of the particle
	 */
	public Vector2 velocity = new Vector2();
	
	/**
	 * Acceleration of the particle
	 */
	public Vector2 acceleration = new Vector2();

	/**
	 * Orientation of the particle
	 */
	public float orientation;
	
	/**
	 * Angular velocity of the particle (assumed to be in degrees/second)
	 */
	public float angularVelocity;

	/**
	 * Scaling factor to applied to the particle 
	 */
	public float scale;
	
	/**
	 * Growth factor determining how the scale changes over time
	 */
	public float scaleGrowth;

	/**
	 * Length of time this particle will remain alive
	 */
	public float lifeSpan;
	
	/**
	 * Length of time since the birth of this particle
	 */
	public float timeSinceBirth;
	
	
	// /////////////////////////////////////////////////////////////////////////
	// Methods: 
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Determine if this particle is still alive
	 * 
	 * @return Boolean true if alive, otherwise false
	 */	
	public boolean isAlive() {
		return timeSinceBirth < lifeSpan;
	}

	/**
	 * Initialise the particle using the specified values 
	 * 
	 * @param position Position
	 * @param velocity Velocity
	 * @param acceleration Acceleration
	 * @param orientation Orientation
	 * @param angularVelocity Angular velocity
	 * @param scale Scale
	 * @param scaleGrowth Scale growth
	 * @param lifeSpan Life span
	 */
	public void initialize(Vector2 position, Vector2 velocity,
			Vector2 acceleration, float orientation, float angularVelocity,
			float scale, float scaleGrowth, float lifeSpan) {

		this.position.x = position.x;
		this.position.y = position.y;
		
		this.velocity.x = velocity.x;
		this.velocity.y = velocity.y;

		this.acceleration.x = acceleration.x;
		this.acceleration.y = acceleration.y;

		this.orientation = orientation;
		this.angularVelocity = angularVelocity;

		this.scale = scale;
		this.scaleGrowth = scaleGrowth;

		this.lifeSpan = lifeSpan;
		this.timeSinceBirth = 0.0f;

	}

	/**
	 * Evolve the particle
	 * 
	 * @param dt Amount of time elapsed (in seconds) from the last update call
	 */
	public void update(float dt) {
		
		velocity.x += acceleration.x * dt;
		velocity.y += acceleration.y * dt;

		position.x += velocity.x * dt;
		position.y += velocity.y * dt;

		orientation += angularVelocity * dt;

		scale += scaleGrowth * dt;

		timeSinceBirth += dt;
	}
}