package fr.jburet.nav.map.component;

import fr.jburet.nav.TouchConstant;
import fr.jburet.nav.map.MapActivity;
import android.graphics.PointF;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;

public class MapViewTouchListener implements OnTouchListener, OnLongClickListener {

	private static final float MAX_SCALE = 200;
	private static final float MIN_SCALE = 1;

	private PointF start = new PointF();
	private PointF mid = new PointF();
	private float oldDist = 1f;
	private int mode = TouchConstant.NONE;
	private float initialeScaleX = 0;
	private boolean managed = true;

	private MapActivity activity;

	public MapViewTouchListener(MapActivity mapActivity) {
		this.activity = mapActivity;
	}

	public boolean onTouch(View view, MotionEvent motionEvent) {
		MapView mapView = (MapView) view;
		if (Log.isLoggable("NAV", Log.DEBUG)) {
			dumpEvent(motionEvent);
		}
		switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			start.set(motionEvent.getX(), motionEvent.getY());
			mode = TouchConstant.NONE;
			break;

		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(motionEvent);
			initialeScaleX = mapView.scaleX;
			Log.d("NAV", "oldDist=" + oldDist);
			if (oldDist > 10f) {
				midPoint(mid, motionEvent);
				mode = TouchConstant.ZOOM;
				Log.d("NAV", "mode=ZOOM");
			}
			break;

		case MotionEvent.ACTION_MOVE:
			if (mode == TouchConstant.ZOOM) {
				float newDist = spacing(motionEvent);
				Log.i("TOUCH", "olddist=" + oldDist);
				Log.i("TOUCH", "newDist=" + newDist);
				if (newDist > 20f && initialeScaleX > 0) {
					float scale = newDist / oldDist;
					Log.i("TOUCH", "" + initialeScaleX / scale);
					if (MIN_SCALE < (initialeScaleX / scale) && (initialeScaleX / scale) < MAX_SCALE) {
						mapView.updateScale(initialeScaleX / scale);
					}
				}
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = TouchConstant.NONE;
			Log.d("NAV", "mode=NONE");
			break;

		}

		return managed;
	}

	private void dumpEvent(MotionEvent event) {
		String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION_").append(names[actionCode]);
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
			sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			sb.append(")");
		}
		sb.append("[");
		for (int i = 0; i < event.getPointerCount(); i++) {
			sb.append("#").append(i);
			sb.append("(pid ").append(event.getPointerId(i));
			sb.append(")=").append((int) event.getX(i));
			sb.append(",").append((int) event.getY(i));
			if (i + 1 < event.getPointerCount())
				sb.append(";");
		}
		sb.append("]");
		Log.d("NAV", sb.toString());
	}

	/** Determine the space between the first two fingers */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	public boolean onLongClick(View view) {
		if (mode != TouchConstant.ZOOM) {
			activity.registerForContextMenu(view);
			activity.openContextMenu(view);
			return true;
		}
		return false;
	}

}
