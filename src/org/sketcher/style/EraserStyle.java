package org.sketcher.style;

import org.sketcher.Style;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

class EraserStyle implements Style {
	private float prevX;
	private float prevY;

	private Paint paint = new Paint();

	{
		paint.setColor(Color.WHITE);
		paint.setAlpha(255);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(5);
	}

	@Override
	public void stroke(Canvas c, float x, float y) {
		c.drawLine(prevX, prevY, x, y, paint);

		prevX = x;
		prevY = y;
	}

	@Override
	public void strokeStart(float x, float y) {
		prevX = x;
		prevY = y;
	}

	@Override
	public void draw(Canvas c) {
	}

	@Override
	public void setColor(int color) {
	}
}
