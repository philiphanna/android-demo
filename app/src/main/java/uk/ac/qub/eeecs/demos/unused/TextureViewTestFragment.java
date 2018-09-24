package uk.ac.qub.eeecs.demos.unused;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import uk.ac.qub.eeecs.demos.R;
import android.app.Fragment;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

public class TextureViewTestFragment extends Fragment {

	
	private TextureView mTextureView;
	private TextureViewRenderer mTextureViewRenderer;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mTextureView = new TextureView(getActivity());
		mTextureViewRenderer = new TextureViewRenderer(mTextureView);
		
		
		mTextureViewRenderer.toString();
		//mTextureView.setOpaque(false);
		
		return mTextureView;		
	}
	
		
	
	class TextureViewRenderer implements TextureView.SurfaceTextureListener, Runnable {

		private TextureView mTextureView;

		Thread renderThread = null;
		volatile boolean running = false;
		
		
		// Draw related
		private Bitmap mImage;
		private Rect mRect;		
		private Random mRandom;
		private long mNumCalls;		
		private Paint mPaint;
		
		
			
		public TextureViewRenderer(TextureView textureView) {
			mTextureView = textureView;
			mTextureView.setSurfaceTextureListener(this);
			
			doSetup();
		}
				
		@Override
		public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
			start();
		}

		@Override
		public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
			stop();
			return false;
		}

		@Override
		public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
			// Ignore
			
		}

		@Override
		public void onSurfaceTextureUpdated(SurfaceTexture surface) {
			// Ignore			
		}

		
		
		@Override
		public void run() {
			while(running && !Thread.interrupted()) {
								
				final Canvas canvas = mTextureView.lockCanvas(null);
				try {
					doDraw(canvas);					
				} finally {
					mTextureView.unlockCanvasAndPost(canvas);
				}
			}
		}

		public void start() {
			running = true;
			
			if(renderThread == null) {
				renderThread = new Thread(this);
				renderThread.start();
			} else{
				renderThread.notify();
			}
		}				
		
		public void stop() {
			running = false;
			renderThread.interrupt();						
		}
				
		private void doSetup() {
			mNumCalls = 0;
			mRandom = new Random();			

			mRect = new Rect();
			mPaint = new Paint();
			
			try {
				AssetManager assetManager = getActivity().getAssets();
				InputStream inputStream = assetManager.open("img/ARGB_8888.png");
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.RGB_565;
				mImage = BitmapFactory.decodeStream(inputStream, null, options);
				//mImage = BitmapFactory.decodeStream(inputStream);
				inputStream.close();
				
				Log.d(getActivity().getResources().getString(R.string.LOG_TAG), 
						"Format = " + mImage.getConfig());				
			} catch (IOException e) {
				Log.d(getActivity().getResources().getString(R.string.LOG_TAG), 
						"Load error: " + e.getMessage());				
			}			
		}
		
		private void doDraw(Canvas canvas) {
			int width = canvas.getWidth();
			int height = canvas.getHeight();
			
			int batchSize = 10000;
			for (int drawIdx = 0; drawIdx < batchSize; drawIdx++) {
				mRect.left = mRandom.nextInt(width);
				mRect.right = mRect.left + mRandom.nextInt(width - mRect.left);
				mRect.top = mRandom.nextInt(height);
				mRect.bottom = mRect.top + mRandom.nextInt(height - mRect.top);

				canvas.drawBitmap(mImage, null, mRect, null);				
			}
			
			mNumCalls++;
			if (mNumCalls%1 == 0) {
				mPaint.setTextSize(36.0f);
				mPaint.setTextAlign(Paint.Align.LEFT);
				mPaint.setColor(Color.WHITE);
				canvas.drawText("Num="+mNumCalls, 50.0f, 50.0f, mPaint);				
			}						
		}			
	}
}
