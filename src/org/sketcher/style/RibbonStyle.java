package org.sketcher.style;

import org.sketcher.Style;

import android.graphics.Canvas;
import android.graphics.Paint;

class RibbonStyle implements Style {
	private Paint paint = new Paint();
	private Painter[] painters = new Painter[50];

	private float x;
	private float y;

	private class Painter {
		private static final int SCREEN_WIDTH = 480;
		private static final int SCREEN_HEIGHT = 600;

		float dx = SCREEN_WIDTH / 2;
		float dy = SCREEN_HEIGHT / 2;
		float ax = 0;
		float ay = 0;
		float div = 0.1F;
		float ease = (float) (Math.random() * 0.2 + 0.6);
	}

	{
		paint.setARGB(25, 0, 0, 0);
		paint.setAntiAlias(true);

		for (int i = 0; i < 50; i++) {
			painters[i] = new Painter();
		}
	}

	@Override
	public void draw(Canvas c) {
		float startX = 0;
		float startY = 0;
		for (int i = 0; i < painters.length; i++) {
			startX = painters[i].dx;
			startY = painters[i].dy;
			painters[i].dx -= painters[i].ax = (painters[i].ax + (painters[i].dx - x)
					* painters[i].div)
					* painters[i].ease;
			painters[i].dy -= painters[i].ay = (painters[i].ay + (painters[i].dy - y)
					* painters[i].div)
					* painters[i].ease;
			c.drawLine(startX, startY, painters[i].dx, painters[i].dy, paint);
		}
	}

	@Override
	public void stroke(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void strokeStart(float x, float y) {
		this.x = x;
		this.y = y;

		for (int i = 0, max = painters.length; i < max; i++) {
			Painter painter = painters[i];
			painter.dx = x;
			painter.dy = y;
		}
	}
}
