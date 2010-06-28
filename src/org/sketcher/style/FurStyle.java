package org.sketcher.style;

import java.util.ArrayList;

import org.sketcher.Style;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

class FurStyle implements Style {
	private float prevX;
	private float prevY;

	private ArrayList<PointF> points = new ArrayList<PointF>();

	private Paint paint = new Paint();

	{
		paint.setColor(Color.BLACK);
		paint.setAlpha(25);
		paint.setAntiAlias(true);
	}

	@Override
	public void stroke(Canvas c, float x, float y) {
		PointF current = new PointF(x, y);
		points.add(current);

		c.drawLine(prevX, prevY, x, y, paint);

		float dx = 0;
		float dy = 0;
		float length = 0;

		for (int i = 0, max = points.size(); i < max; i++) {
			PointF point = points.get(i);
			dx = point.x - current.x;
			dy = point.y - current.y;

			length = dx * dx + dy * dy;

			if (length < 2000 && Math.random() > length / 2000) {
				float ddx = dx * 0.5F;
				float ddy = dy * 0.5F;
				c.drawLine(point.x + ddx, point.y + ddy, point.x - ddx, point.y
						- ddy, paint);
			}
		}

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
		paint.setColor(color);
		paint.setAlpha(25);
	}
}
