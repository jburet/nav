package fr.jburet.nav.map.component;

import fr.jburet.nav.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author jburet
 * 
 *         Navbox is a component on 1 or 2 line(s) One for label One for value
 *         and unit For each line you should configure : background and
 *         forground color You should define transparency
 */
public class NavBox extends View implements ValueListener {
	
	public static final int NAV_BOX_2_LINE_HEIGHT = 50;
	public static final int NAV_BOX_STANDARD_WIDTH = 70;
	public static final int NAV_BOX_LINE_1 = 15;
	public static final int NAV_BOX_LINE_2 = 37;
	
	private final String title;
	private String value;
	private String unit;

	private Paint paintTitleForground;
	private Paint paintValueForground;

	private RectF rectf = new RectF(0, 0, this.getWidth() - 1, this.getHeight() - 1);

	/**
	 * Used for programmatic creatiob
	 * 
	 * @param context
	 * @param attrs
	 */
	public NavBox(Context context, String title, String unit) {
		super(context, null);
		this.title = title;
		this.unit = unit;
		initPaint();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public NavBox(Context context, AttributeSet attrs, String title, String unit) {
		super(context, attrs);
		this.title = title;
		this.unit = unit;
		initPaint();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public NavBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray sa = context.obtainStyledAttributes(attrs, R.styleable.NavBox);
		title = sa.getString(R.styleable.NavBox_title);
		initPaint();
	}

	private void initPaint() {
		setBackgroundResource(R.drawable.navbox);

		paintTitleForground = new Paint();
		paintTitleForground.setTextSize(12);
		paintTitleForground.setColor(Color.WHITE);
		paintTitleForground.setTextAlign(Align.CENTER);
		paintTitleForground.setTypeface(Typeface.DEFAULT_BOLD);

		paintValueForground = new Paint();
		paintValueForground.setTextSize(12);
		paintValueForground.setColor(Color.BLACK);
		paintValueForground.setTextAlign(Align.CENTER);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// Background
		// Set bitmap
		// Draw title text
		if (title != null) {
			canvas.drawText(title, getWidth() / 2, NAV_BOX_LINE_1, paintTitleForground);
		}
		// Draw value
		if (value != null) {
			canvas.drawText(value, getWidth() / 2, NAV_BOX_LINE_2, paintValueForground);
		}
	}

	public String getValue() {
		return value;
	}

	public void updateValue(String value) {
		this.value = value;
		invalidate();
	}

}
