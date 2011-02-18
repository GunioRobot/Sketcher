package org.sketcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

public class FileHelper {
	private static final String FILENAME_PATTERN = "image_%d.png";

	private final Sketcher context;
	boolean isSaved = false;

	/* package */FileHelper(Sketcher context) {
		this.context = context;
	}

	private File getSDDir() {
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/sketcher/";

		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}

		return file;
	}

	Bitmap getSavedBitmap() {
		if (!isStorageAvailable()) {
			return null;
		}

		File lastFile = getLastFile(getSDDir());
		if (lastFile == null) {
			return null;
		}

		Bitmap savedBitmap = null;
		try {
			FileInputStream fis = new FileInputStream(lastFile);
			savedBitmap = BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		if (savedBitmap.isRecycled()) {
			return null;
		}
		return savedBitmap;
	}

	File getLastFile(File dir) {
		int suffix = 1;

		File newFile = null;
		File file = null;
		do {
			file = newFile;
			newFile = new File(dir, String.format(FILENAME_PATTERN, suffix));
			suffix++;
		} while (newFile.exists());

		return file;
	}

	private File getUniqueFilePath(File dir) {
		int suffix = 1;

		while (new File(dir, String.format(FILENAME_PATTERN, suffix)).exists()) {
			suffix++;
		}

		return new File(dir, String.format(FILENAME_PATTERN, suffix));
	}

	private void saveBitmap(File file) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			context.getSurface().getBitmap()
					.compress(CompressFormat.PNG, 100, fos);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean isStorageAvailable() {
		String externalStorageState = Environment.getExternalStorageState();
		if (!externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(context, R.string.sd_card_is_not_available,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	void share() {
		if (!isStorageAvailable()) {
			return;
		}

		new SaveTask() {
			protected void onPostExecute(File file) {
				isSaved = true;

				Uri uri = Uri.fromFile(file);

				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("image/png");
				i.putExtra(Intent.EXTRA_STREAM, uri);
				context.startActivity(Intent.createChooser(i,
						context.getString(R.string.send_image_to)));

				super.onPostExecute(file);
			}
		}.execute();
	}

	void saveToSD() {
		if (!isStorageAvailable()) {
			return;
		}

		new SaveTask().execute();
	}

	File saveBitmap() {
		File newFile = getUniqueFilePath(getSDDir());
		saveBitmap(newFile);
		notifyMediaScanner(newFile);
		return newFile;
	}

	private void notifyMediaScanner(File file) {
		Uri uri = Uri.fromFile(file);
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				uri));
	}

	private class SaveTask extends AsyncTask<Void, Void, File> {
		private ProgressDialog dialog = ProgressDialog.show(context, "",
				context.getString(R.string.saving_to_sd_please_wait), true);

		protected File doInBackground(Void... none) {
			context.getSurface().getDrawThread().pauseDrawing();
			return saveBitmap();
		}

		protected void onPostExecute(File file) {
			dialog.hide();
			context.getSurface().getDrawThread().resumeDrawing();
		}
	}

}
