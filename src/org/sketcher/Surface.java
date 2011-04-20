package org.sketcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public final class Surface extends SurfaceView implements Callback {
	public final class DrawThread extends Thread {
		private boolean mRun = true;
		private boolean mPause = false;

		@Override
		public void run() {
			waitForBitmap();

			final SurfaceHolder surfaceHolder = getHolder();
			Canvas canvas = null;

			while (mRun) {
				try {
					while (mRun && mPause) {
						Thread.sleep(100);
					}

					canvas = surfaceHolder.lockCanvas();
					if (canvas == null) {
						break;
					}

					synchronized (surfaceHolder) {
						controller.draw();
						canvas.drawBitmap(bitmap, 0, 0, null);
					}

					Thread.sleep(10);
				} catch (InterruptedException e) {
				} finally {
					if (canvas != null) {
						surfaceHolder.unlockCanvasAndPost(canvas);
					}
				}
			}
		}

		private void waitForBitmap() {
			while (bitmap == null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void stopDrawing() {
			mRun = false;
		}

		public void pauseDrawing() {
			mPause = true;
		}

		public void resumeDrawing() {
			mPause = false;
		}
	}

	private DrawThread drawThread;
	private final Canvas drawCanvas = new Canvas();
	private final Controller controller = new Controller(drawCanvas);
	private Bitmap initialBitmap;
	private Bitmap bitmap;

	public Surface(Context context, AttributeSet attributes) {
		super(context, attributes);

		getHolder().addCallback(this);
		setFocusable(true);
		setOnTouchListener(controller);
	}

	public void setStyle(Style style) {
		controller.setStyle(style);
	}

	public DrawThread getDrawThread() {
		if (drawThread == null) {
			drawThread = new DrawThread();
		}
		return drawThread;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.eraseColor(Color.WHITE);

		drawCanvas.setBitmap(bitmap);

		if (initialBitmap != null) {
			drawCanvas.drawBitmap(initialBitmap, 0, 0, null);
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		getDrawThread().start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		getDrawThread().stopDrawing();
		while (true) {
			try {
				getDrawThread().join();
				break;
			} catch (InterruptedException e) {
			}
		}
		drawThread = null;
	}

	public void clearBitmap() {
		bitmap.eraseColor(Color.WHITE);
		controller.clear();
	}

	public void setPaintColor(Paint color) {
		controller.setPaintColor(color);
	}

	public Paint getPaintColor() {
		return controller.getPaintColor();
	}

	public void setInitialBitmap(Bitmap initialBitmap) {
		this.initialBitmap = initialBitmap;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}
}
