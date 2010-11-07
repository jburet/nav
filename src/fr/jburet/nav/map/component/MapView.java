package fr.jburet.nav.map.component;

import java.util.Collection;

import fr.jburet.nav.R;
import fr.jburet.nav.database.point.Waypoint;
import fr.jburet.nav.gps.PositionData;
import fr.jburet.nav.map.MapActivity;
import fr.jburet.nav.utils.ConversionUtils;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MapView extends SurfaceView implements SurfaceHolder.Callback {

	/** Map activity controller */
	/** Handle to the application context, used to e.g. fetch Drawables. */
	private MapActivity mContext;

	// Drawer
	private WaypointDrawer waypointDrawer = new WaypointDrawer(mContext, this);

	/**
	 * Current height of the surface/canvas.
	 * 
	 * @see #setSurfaceSize
	 */
	private int mCanvasHeight = -1;

	/**
	 * Current width of the surface/canvas.
	 * 
	 * @see #setSurfaceSize
	 */
	private int mCanvasWidth = -1;

	// Externally setted variable
	/** Current plane status */
	private float currentPlaneBearing = 0;

	private double currentLatitude = -1;

	private double currentLongitude = -1;

	/** Current map bearing and size */
	private double mapBearing;

	private double mapTopLatitude;

	private double mapLeftLongitude;

	private double mapBottomLatitude;

	private double mapRightLongitude;

	int scaleX = 50;

	int scaleY;

	// Waypoint to draw
	private Collection<Waypoint> waypoints = null;

	class MapThread extends Thread {

		/** Handle to the surface manager object we interact with */
		private SurfaceHolder mSurfaceHolder;

		private Handler mHandler;

		/** Icon */
		private Drawable mPlaneIcon;

		/** plane icon position. */
		private int mPlaneIconX;

		private int mPlaneIconY;

		private int mPlaneIconWidth;

		private int mPlaneIconHeigth;

		/** map bound */
		private boolean mapInitialized = false;

		/** Indicate whether the surface has been created & is ready to draw */
		private boolean mRunning = false;

		private boolean mPaused = false;

		public MapThread(SurfaceHolder surfaceHolder, Handler handler) {
			mSurfaceHolder = surfaceHolder;
			mHandler = handler;

			mPlaneIcon = mContext.getResources().getDrawable(R.drawable.icon_planeur);
			mPlaneIconHeigth = mPlaneIcon.getIntrinsicHeight();
			mPlaneIconWidth = mPlaneIcon.getIntrinsicWidth();
		}

		/* Callback invoked when the surface dimensions change. */
		public void setSurfaceSize(int width, int height) {
			// synchronized to make sure these all change atomically
			synchronized (mSurfaceHolder) {
				mCanvasWidth = width;
				mCanvasHeight = height;
				mPlaneIconX = width / 2;
				mPlaneIconY = height / 2;
				updateScale(scaleX);
				updateMapBound();
			}
		}

		// On applatit... L'echelle longitude en bas de l'ecran est la meme que
		// la longitude en haut de l'ecran
		private void updateMapBound() {
			if (currentLatitude >= 0 && currentLongitude >= 0) {
				// Nb de deg ˆ gauche
				// TODO Take in care 180¡ limit...
				mapLeftLongitude = currentLongitude
						- ConversionUtils.distanceToLongitude(
								((double) mPlaneIconX / (double) mCanvasWidth * (double) scaleX), currentLatitude);
				mapRightLongitude = currentLongitude
						+ ConversionUtils.distanceToLongitude((((double) mCanvasWidth - (double) mPlaneIconX)
								/ (double) mCanvasWidth * (double) scaleX), currentLatitude);
				// TODO Take in care sud hemisphere
				mapTopLatitude = currentLatitude
						+ ConversionUtils.distanceToLatitude((double) mPlaneIconY / (double) mCanvasHeight
								* (double) scaleY);
				mapBottomLatitude = currentLatitude
						- ConversionUtils.distanceToLatitude((((double) mCanvasHeight - (double) mPlaneIconY)
								/ (double) mCanvasHeight * (double) scaleY));
				Log.d("MapView", "left = " + mapLeftLongitude);
				Log.d("MapView", "right = " + mapRightLongitude);
				Log.d("MapView", "top = " + mapTopLatitude);
				Log.d("MapView", "bottom = " + mapBottomLatitude);
				mapInitialized = true;
				mContext.onMapBoundChange(mapLeftLongitude, mapTopLatitude, mapRightLongitude, mapBottomLatitude);
			}

		}

		public void setRunning(boolean running) {
			this.mRunning = running;
		}

		public void setPaused(boolean paused) {
			this.mPaused = paused;
		}

		public void pauseThread() {
			synchronized (this) {
				mPaused = true;
				this.notify();
			}
		}

		public void resumeThread() {
			synchronized (this) {
				mPaused = false;
				this.notify();
			}
		}

		private boolean waitForResume() {
			synchronized (this) {
				if (!mRunning) {
					return false;
				}

				while (mPaused) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (!mRunning) {
					return false;
				}
				return true;
			}
		}

		@Override
		public void run() {
			mRunning = true;
			mPaused = true;
			while (mRunning) {
				if (waitForResume()) {
					Canvas c = null;
					try {
						c = mSurfaceHolder.lockCanvas(null);
						synchronized (mSurfaceHolder) {
							doDraw(c);
						}
					} finally {
						// do this in a finally so that if an exception is
						// thrown
						// during the above, we don't leave the Surface in an
						// inconsistent state
						if (c != null) {
							mSurfaceHolder.unlockCanvasAndPost(c);
						}
					}
					try {
						// Refresh 2 seconds
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// Do nothing
					}
				}
			}
		}

		private void doDraw(Canvas c) {
			if (mapInitialized) {
				cleanMap(c);
				drawMap(c);
				drawPlaneIcon(c);
			}
		}

		private void cleanMap(Canvas c) {
			Paint paint = new Paint();
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.WHITE);
			c.drawRect(0, 0, mCanvasWidth, mCanvasHeight, paint);
		}

		private void drawMap(Canvas c) {
			drawWaypoint(c);
		}

		private void drawWaypoint(Canvas c) {
			if (waypoints != null) {
				waypointDrawer.drawWaypoint(c, waypoints);
			}
		}

		private void drawPlaneIcon(Canvas canvas) {

			// Draw the ship with its current rotation
			canvas.save();
			// canvas.rotate((float) mHeading, (float) mX, mCanvasHeight -
			// (float) mY);
			canvas.rotate(currentPlaneBearing, mPlaneIconX, mPlaneIconY);
			mPlaneIcon.setBounds(mPlaneIconX - (mPlaneIconWidth / 2), mPlaneIconY - (mPlaneIconHeigth / 2), mPlaneIconX
					+ (mPlaneIconWidth / 2), mPlaneIconY + (mPlaneIconHeigth / 2));
			mPlaneIcon.draw(canvas);

			canvas.restore();
		}

	}

	/** The thread that actually draws the animation */
	private MapThread thread;

	public MapView(MapActivity context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;

		// register our interest in hearing about changes to our surface
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		// create thread only; it's started in surfaceCreated()
		thread = new MapThread(holder, new Handler() {
			@Override
			public void handleMessage(Message m) {

			}
		});
		thread.start();
		setFocusable(true); // make sure we get key events
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		thread.setSurfaceSize(width, height);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		thread.resumeThread();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		thread.pauseThread();
	}

	// Public map view update

	public void updateScale(int scaleX) {
		this.scaleX = scaleX;
		// update scaleY with resolution
		this.scaleY = (int) (scaleX / getScreenFactor());
	}

	// Inernal method
	public float getScreenFactor() {
		return (float) mCanvasWidth / (float) mCanvasHeight;
	}

	public void setPositionData(PositionData positionData) {
		this.currentLatitude = positionData.getLatitute();
		this.currentLongitude = positionData.getLongitude();
		this.currentPlaneBearing = positionData.getBearing();
		updateScale(scaleX);
		thread.updateMapBound();
	}

	public void setWaypointToDraw(Collection<Waypoint> waypoints) {
		this.waypoints = waypoints;
	}

	float convertLatitudeToPixel(float latitude) {
		return (float) ((((double) latitude - mapLeftLongitude) / (mapRightLongitude - mapLeftLongitude)) * mCanvasWidth);
	}

	float convertLongitudeToPixel(float longitude) {
		return (float) ((((double) longitude - mapTopLatitude) / (mapBottomLatitude - mapTopLatitude)) * mCanvasHeight);
	}

}
