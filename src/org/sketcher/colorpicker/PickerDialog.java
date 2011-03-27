package org.sketcher.colorpicker;

import org.sketcher.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PickerDialog extends Dialog {

	private Picker.OnColorChangedListener mListener;
	private final Paint mPaint;
	private int mAlpha = 0;

	public PickerDialog(Context context,
			Picker.OnColorChangedListener listener, Paint initialPaint) {
		super(context);

		mListener = listener;
		mPaint = new Paint(initialPaint);
		mAlpha = initialPaint.getAlpha();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.pick_a_color);
		setContentView(R.layout.color_picker);

		final PreviewView previewView = (PreviewView) findViewById(R.id.preview_new);
		previewView.setPaint(mPaint);

		final Picker alphaBar = (Picker) findViewById(R.id.alpha_picker);
		alphaBar.setColor(mPaint.getColor());
		alphaBar.setOnColorChangedListener(new Picker.OnColorChangedListener() {
			@Override
			public void colorChanged(Paint paint) {
				mAlpha = paint.getAlpha();
				mPaint.setColor(paint.getColor());
				mPaint.setAlpha(mAlpha);
				previewView.invalidate();
			}
		});

		final Picker satValPicker = (Picker) findViewById(R.id.satval_picker);
		Picker.OnColorChangedListener satValLstr = new Picker.OnColorChangedListener() {
			public void colorChanged(Paint paint) {
				alphaBar.setColor(paint.getColor());
				mPaint.setColor(paint.getColor());
				mPaint.setAlpha(mAlpha);
				previewView.invalidate();
			}
		};
		satValPicker.setOnColorChangedListener(satValLstr);
		satValPicker.setColor(mPaint.getColor());

		Picker huePicker = (Picker) findViewById(R.id.hue_picker);
		Picker.OnColorChangedListener hueLstr = new Picker.OnColorChangedListener() {
			@Override
			public void colorChanged(Paint paint) {
				alphaBar.setColor(paint.getColor());
				satValPicker.setColor(paint.getColor());
				mPaint.setColor(paint.getColor());
				mPaint.setAlpha(mAlpha);
				previewView.invalidate();
			}
		};
		huePicker.setOnColorChangedListener(hueLstr);
		huePicker.setColor(mPaint.getColor());

		Button acceptButton = (Button) findViewById(R.id.picker_button_accept);
		acceptButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.colorChanged(mPaint);
				dismiss();
			}
		});

		Button cancelButton = (Button) findViewById(R.id.picker_button_cancel);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
}