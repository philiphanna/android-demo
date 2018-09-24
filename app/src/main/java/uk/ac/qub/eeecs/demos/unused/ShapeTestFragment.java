package uk.ac.qub.eeecs.demos.unused;

import java.util.Random;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ShapeTestFragment extends Fragment {

	private class RenderView extends View {
		private Paint mPaint;
		private Random mRandom;
		private long mNumCalls;
		
		public RenderView(Context context) {
			super(context);
			mPaint = new Paint();
			mRandom = new Random();
			mNumCalls = 0;
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			int width = canvas.getWidth();
			int height = canvas.getHeight();
			
			mPaint.setARGB(255, mRandom.nextInt(256), mRandom.nextInt(256), mRandom.nextInt(256));
			canvas.drawLine(mRandom.nextInt(width), mRandom.nextInt(height), 
					mRandom.nextInt(width), mRandom.nextInt(height), mPaint);
	
			mPaint.setStyle(Paint.Style.FILL);
			mPaint.setARGB(255, mRandom.nextInt(256), mRandom.nextInt(256), mRandom.nextInt(256));
			canvas.drawRect(mRandom.nextInt(width), mRandom.nextInt(height), 
					mRandom.nextInt(width), mRandom.nextInt(height), mPaint);

			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			mPaint.setARGB(255, mRandom.nextInt(256), mRandom.nextInt(256), mRandom.nextInt(256));
			canvas.drawCircle(mRandom.nextInt(width), mRandom.nextInt(height), 
					mRandom.nextInt(width)/2, mPaint);
						
			mNumCalls++;
			if (mNumCalls%100 == 0) {
				mPaint.setColor(Color.BLACK);
				mPaint.setStyle(Paint.Style.FILL);
				canvas.drawRect(50f, 50f, 150f, 90f, mPaint);				
				mPaint.setColor(Color.WHITE);
				canvas.drawText("Num="+mNumCalls, 50.0f, 50.0f, mPaint);				
			}
			
			invalidate();
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return new RenderView(getActivity());
	}
}
