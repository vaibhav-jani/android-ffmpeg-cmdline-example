package za.jamie.androidffmpegcmdline;

import java.io.File;
import java.io.IOException;
import java.util.List;

import za.jamie.androidffmpegcmdline.ffmpeg.CropVideoFfmpegJob;
import za.jamie.androidffmpegcmdline.ffmpeg.Utils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Video;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int CAPTURE_RETURN = 1;
	private static final int GALLERY_RETURN = 2;
	private static final int SUBMIT_RETURN = 3;

	private static final String TAG = "MainActivity";

	private Button mSelectButton;
	private Button mCaptureButton;
	private Button mStartButton;
	private EditText et;

	private String mFfmpegInstallPath;
	private Uri fileUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		findViews();

		installFfmpeg();

	}

	private void findViews() {

		mStartButton = (Button) findViewById(R.id.button1);
		mStartButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				start();

			}
		});

		mSelectButton = (Button) findViewById(R.id.buttonSelect);
		mSelectButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				select();

			}
		});

		mCaptureButton = (Button) findViewById(R.id.buttonCapture);
		mCaptureButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				capture();

			}
		});

		et = (EditText) findViewById(R.id.et);
	}

	protected void select() {

		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_PICK);
		intent.setType("video/*");

		List<ResolveInfo> list = getPackageManager().queryIntentActivities(
				intent, PackageManager.MATCH_DEFAULT_ONLY);
		if (list.size() <= 0) {
			Log.d(TAG, "no video picker intent on this hardware");
			return;
		}

		startActivityForResult(intent, GALLERY_RETURN);
	}

	protected void capture() {

		Intent i = new Intent();
		i.setAction("android.media.action.VIDEO_CAPTURE");
		startActivityForResult(i, CAPTURE_RETURN);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case CAPTURE_RETURN:
		case GALLERY_RETURN:
			if (resultCode == RESULT_OK) {

				/*
				 * Intent intent = new Intent(this, SubmitActivity.class);
				 * intent.setData(data.getData());
				 * startActivityForResult(intent, SUBMIT_RETURN);
				 */

				fileUri = data.getData();
			}
			break;
		case SUBMIT_RETURN:
			if (resultCode == RESULT_OK) {
				Toast.makeText(MainActivity.this, "thank you!",
						Toast.LENGTH_LONG).show();
			} else {
				// Toast.makeText(DetailsActivity.this,
				// "submit failed or cancelled",
				// Toast.LENGTH_LONG).show();
			}
			break;
		}
	}

	private void installFfmpeg() {

		File ffmpegFile = new File(getFilesDir(), "ffmpeg");

		mFfmpegInstallPath = ffmpegFile.toString();

		Log.d(TAG, "ffmpeg install path: " + mFfmpegInstallPath);

		if (!ffmpegFile.exists()) {

			try {

				ffmpegFile.createNewFile();

			} catch (IOException e) {

				Log.e(TAG, "Failed to create new file!", e);
			}

			Utils.installBinaryFromRaw(this, R.raw.ffmpeg, ffmpegFile);
		}

		ffmpegFile.setExecutable(true);
	}

	private void start() {

		File file = getFileFromUri(fileUri);
		String input = file.getAbsolutePath();
		String ourString = new File(file.getParent(), "out.mp4").getAbsolutePath();
		
		et.setText("Input : " + input + "\n\n Output : " + ourString);
		
		// final FfmpegJob job = new FfmpegJob(mFfmpegInstallPath);
		final CropVideoFfmpegJob job = new CropVideoFfmpegJob(
				mFfmpegInstallPath);
		
		job.inputPath = input;
		job.outputPath = ourString;
		// loadJob(job);

		final ProgressDialog progressDialog = ProgressDialog.show(this,
				"Loading", "Please wait.", true, false);

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... arg0) {
				job.create().run();
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				progressDialog.dismiss();
				Toast.makeText(MainActivity.this, "Ffmpeg job complete.",
						Toast.LENGTH_SHORT).show();
			}

		}.execute();
	}

	
	private File getFileFromUri(Uri uri) {

        try {
            String filePath = null;

            String[] proj = { Video.VideoColumns.DATA };

            Cursor cursor = getContentResolver().query(uri, proj, null, null, null);

            if(cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(Video.VideoColumns.DATA);
                filePath = cursor.getString(column_index);
            }

            cursor.close();

            //String filePath = cursor.getString(cursor.getColumnIndex(Video.VideoColumns.DATA));

            File file = new File(filePath);
            cursor.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
