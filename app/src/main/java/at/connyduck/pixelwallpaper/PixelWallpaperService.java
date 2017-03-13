package at.connyduck.pixelwallpaper;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.WindowManager;

public class PixelWallpaperService extends WallpaperService {

	@Override
	public Engine onCreateEngine() {
		return new PixelWallpaperEngine();
	}

	public class PixelWallpaperEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener {
		private final Handler handler = new Handler();
		private final Runnable drawRunner = new Runnable() {
			@Override
			public void run() {
				draw();
			}

		};

		private boolean border;
		private int touchmode;
		private int pixelsize;
		private int framerate;
		
		private boolean borderDefault;
        private boolean blackWhiteDefault;
		private String touchmodeDefault;
		private String pixelsizeDefault;
		private String framerateDefault;
		
		private int width;
		private int height;
		
		private PixelArray pa;
		
		private SharedPreferences prefs;
		
		private ShutdownReceiver shutdownReceiver;

		public PixelWallpaperEngine() {
			
			prefs = PreferenceManager.getDefaultSharedPreferences(PixelWallpaperService.this);

			Resources res = getResources();

            borderDefault = res.getBoolean(R.bool.pref_border_default);
            blackWhiteDefault = res.getBoolean(R.bool.pref_black_white_default);
			touchmodeDefault = res.getString(R.string.pref_touch_default);
			pixelsizeDefault = res.getString(R.string.pref_pixel_default);
			framerateDefault = res.getString(R.string.pref_frame_default);

            boolean blackWhiteMode = prefs.getBoolean(PreferencesActivity.PREF_BLACK_WHITE, blackWhiteDefault);
			border = prefs.getBoolean(PreferencesActivity.PREF_BORDER, borderDefault);
			touchmode = Integer.valueOf(prefs.getString(PreferencesActivity.PREF_TOUCH, touchmodeDefault));
			pixelsize = Integer.valueOf(prefs.getString(PreferencesActivity.PREF_PIXEL, pixelsizeDefault));
			framerate = Integer.valueOf(prefs.getString(PreferencesActivity.PREF_FRAME, framerateDefault));
			prefs.registerOnSharedPreferenceChangeListener(this);

			IntentFilter filter = new IntentFilter(Intent.ACTION_SHUTDOWN);
			shutdownReceiver = new ShutdownReceiver(this);
			registerReceiver(shutdownReceiver, filter);

			WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			Display d = wm.getDefaultDisplay();
			DisplayMetrics dm = new DisplayMetrics();
			d.getMetrics(dm);

			height = dm.heightPixels;
			width = dm.widthPixels;

			pa = new PixelArray(touchmode, blackWhiteMode);

		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sp, String key) {

			if(sp == prefs) {
                if(key.equals(PreferencesActivity.PREF_BLACK_WHITE)) {
                    pa.setBlackWhiteMode(sp.getBoolean(PreferencesActivity.PREF_BLACK_WHITE, false));
                }

				if(key.equals(PreferencesActivity.PREF_FRAME)) {
					framerate = Integer.valueOf(sp.getString(PreferencesActivity.PREF_FRAME, framerateDefault));
				}
				if(key.equals(PreferencesActivity.PREF_TOUCH)) {
					touchmode = Integer.valueOf(sp.getString(PreferencesActivity.PREF_TOUCH, touchmodeDefault));
					pa.setTouchmode(touchmode);
				}
				if(key.equals(PreferencesActivity.PREF_PIXEL)) {
					int newpixelsize = Integer.valueOf(sp.getString(PreferencesActivity.PREF_PIXEL, pixelsizeDefault));
					if(newpixelsize != pixelsize) {
						pixelsize = newpixelsize;
						pa.reset(width, height, pixelsize, border);
					}
				}
				if(key.equals(PreferencesActivity.PREF_BORDER)) {
					boolean newborder = sp.getBoolean(PreferencesActivity.PREF_BORDER, borderDefault);
					if(newborder != border) {
						border = newborder;
						pa.reset(width, height, pixelsize, border);
					}
				}
			}
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
            pa.initialize(PixelWallpaperService.this, width, height, pixelsize, border, isPreview());
			handler.post(drawRunner);
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			if (visible) {
				handler.post(drawRunner);
			} else {
				handler.removeCallbacks(drawRunner);
			}
		}

		@Override
		public void onTouchEvent(MotionEvent event) {

			if (touchmode != 0) {

				double x = event.getX();
				double y = event.getY();

				pa.touch(x, y);
				SurfaceHolder holder = getSurfaceHolder();
				Canvas canvas = null;
				try {
					canvas = holder.lockCanvas();
					if (canvas != null) {
						pa.draw(canvas);
					}
				} finally {
					if (canvas != null)
						holder.unlockCanvasAndPost(canvas);
				}

				super.onTouchEvent(event);
			}
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			if(!isPreview()) pa.save();
			unregisterReceiver(shutdownReceiver);
			handler.removeCallbacks(drawRunner); 
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			if((this.width != width || this.height != height)) {
				this.width = width;
				this.height = height;
				pa.reinitialize(width, height);

				Canvas canvas = null;

				try {
					canvas = holder.lockCanvas();

					if (canvas != null) {

						pa.draw(canvas);
					}
				} finally {
					if (canvas != null)
						holder.unlockCanvasAndPost(canvas);
				}

			}

		}

		private void draw() {

			SurfaceHolder holder = getSurfaceHolder();
			Canvas canvas = null;

			pa.update();

			try {
				canvas = holder.lockCanvas();

				if (canvas != null) {

					pa.draw(canvas);
				}
			} finally {
				if (canvas != null)
					holder.unlockCanvasAndPost(canvas);
			}
			handler.removeCallbacks(drawRunner);
			if (isVisible()) {
				handler.postDelayed(drawRunner, framerate);
			}

		}

		public void onShutdown() {
			pa.save();
		}


	}

}