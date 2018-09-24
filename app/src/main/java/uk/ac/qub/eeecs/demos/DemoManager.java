package uk.ac.qub.eeecs.demos;

import java.util.ArrayList;

import android.content.Context;

public class DemoManager {

	// ////////////////////////////////////////////////////////////////////////
	// Properties
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * List of available demos
	 */
	private ArrayList<DemoDetails> mDemoDetails;

	/**
	 * Context for the demo manager (from which the demos will be run)
	 */
	private Context mContext;

	// ////////////////////////////////////////////////////////////////////////
	// Constructors
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Create a new demo manager
	 * 
	 * @param context
	 *            Context from which demos will be run
	 */
	public DemoManager(Context context) {
		mContext = context;
		mDemoDetails = new ArrayList<DemoDetails>();
	}

	// ////////////////////////////////////////////////////////////////////////
	// Methods
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Add the set of demo details to the manager
	 * 
	 * @param demoDetail
	 *            Demo details
	 */
	public void add(DemoDetails demoDetail) {
		mDemoDetails.add(demoDetail);
	}

	/**
	 * Return all available demos
	 * 
	 * @return List of all available demos
	 */
	public ArrayList<DemoDetails> getDemoDetails() {
		return mDemoDetails;
	}

	/**
	 * Return the context from which the demos will be run
	 * 
	 * @return Context from which the demos will be run
	 */
	public Context getContext() {
		return mContext;
	}
}