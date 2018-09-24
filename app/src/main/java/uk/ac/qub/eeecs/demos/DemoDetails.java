package uk.ac.qub.eeecs.demos;

/**
 * Overview details for a single demo
 * 
 * @version 1.0
 */
public class DemoDetails {

	// ////////////////////////////////////////////////////////////////////////
	// Properties
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Descriptive name of the demo
	 */
	private String mName;

	/**
	 * Demo class name
	 */
	private String mClassName;

	/**
	 * Location of the demo class relative to demo manager
	 */
	private String mClassLocation;

	// ////////////////////////////////////////////////////////////////////////
	// Constructors
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Create overview details of a demo
	 * 
	 * @param name
	 *            Descriptive name of the demo
	 * @param className
	 *            Class name for the demo
	 * @param classLocation
	 *            Location of the demo relative to the manager
	 */
	public DemoDetails(String name, String className, String classLocation) {
		mName = name;
		mClassName = className;
		mClassLocation = classLocation;
	}

	// ////////////////////////////////////////////////////////////////////////
	// Methods
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Get the descriptive name of this demo
	 * 
	 * @return Descriptive name of this demo
	 */
	public String getName() {
		return mName;
	}

	/**
	 * Get the class name of this demo
	 * 
	 * @return Class name of this demo
	 */
	public String getClassName() {
		return mClassName;
	}

	/**
	 * Get the location of the demo class relative to demo manager
	 * 
	 * @return Location of the demo class relative to demo manager
	 */
	public String getClassLocation() {
		return mClassLocation;
	}
}