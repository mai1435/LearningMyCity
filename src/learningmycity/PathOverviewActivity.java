package learningmycity;

import java.text.SimpleDateFormat;
import java.util.Date;

import learningmycity.content.Quest;
import learningmycity.db.DbAdapter;
import learningmycity.util.JSONResponseHandler;
import learningmycity.util.TypefaceSpan;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tsivas061.learningmycity.R;

public class PathOverviewActivity extends ActionBarActivity {
	private static final String TAG = "PathOverviewActivity";

	// Max score the user can achieve for a task.
	private final static int MAX_SCORE = 100;

	// The dbAdapter for connecting to the database.
	private DbAdapter dbAdapter;

	// The Typeface for importing custom fonts.
	private Typeface typeFace;

	// Array with quest IDs representing the path.
	private int[] questIds;

	// Array with task IDs representing the quest.
	private int[] taskIds;

	// The currentPathId of the path.
	private int currentPathId;

	// The TextView uploadButton.
	private TextView uploadButton;

	// The total score.
	private int t_score;

	// The JSONResponseHandler object.
	private JSONResponseHandler obj;

	// The String for connection feedback.
	private String feedback;

	// The boolean for disabling uploadButton.
	private boolean score_uploaded;

	// The SharedPreferences.
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.path_overview);

		// Creates a new adapter
		dbAdapter = new DbAdapter(this);

		// Import external font style.
		typeFace = Typeface.createFromAsset(getAssets(),
				"fonts/Caudex-Italic.ttf");

		// Extract currentQuestId from the Intent.
		Bundle b = getIntent().getExtras();
		currentPathId = b.getInt("pathId");

		// Show the penalties on the screen.
		showPenalties();

		// Initialise continueButton.
		uploadButton = (TextView) findViewById(R.id.button_score_upload);
		uploadButton.setTypeface(typeFace);

		// Initialise the SharedPreferences.
		prefs = PreferenceManager
				.getDefaultSharedPreferences(PathOverviewActivity.this);

		// Check if the score has been uploaded so as to disable uploadButton.
		score_uploaded = prefs.getBoolean("score_uploaded", false);
		if (score_uploaded) {
			uploadButton.setEnabled(false);
		}

		// Set the title.
		// Custom Title.
		SpannableString s = new SpannableString(
				getString(R.string.title_path_overview));
		s.setSpan(new TypefaceSpan(PathOverviewActivity.this,
				"Caudex-Italic.ttf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new ForegroundColorSpan(Color.argb(255, 255, 211, 155)), 0,
				s.length(), 0);
		setTitle(s);

		// uploadButton Listener.
		uploadButton.setOnClickListener(new OnClickListener() {
			@SuppressLint("InflateParams")
			public void onClick(View view) {

				if (isNetworkAvailable()) {
					// EditText for username.
					final EditText input = new EditText(
							PathOverviewActivity.this);

					// Ask the user to confirm answer
					new AlertDialog.Builder(PathOverviewActivity.this)
							.setCancelable(false)
							.setView(input)
							.setTitle(R.string.dialog_title_confirm_upload)
							.setPositiveButton(R.string.yes,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {

											String username = input.getText()
													.toString();

											new UploadScoreTask()
													.execute(username);
										}
									}).setNegativeButton(R.string.no, null)
							.show();
				} else {
					Toast.makeText(getApplicationContext(),
							R.string.toast_no_internet_upload_score,
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.path_overview, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if (id == R.id.exit_activity) {
			finish();
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Shows the penalties on the screen.
	 */
	@SuppressLint("InflateParams")
	public void showPenalties() {

		questIds = dbAdapter.getQuestIds(currentPathId);

		int wa_penalty = 0;
		int h_penalty = 0;
		int t_penalty = 0;
		t_score = 0;

		LinearLayout mLayout = (LinearLayout) this
				.findViewById(R.id.layout_path_list);

		// For each quest.
		for (int i = 0; i < questIds.length; i++) {

			taskIds = dbAdapter.getTaskIds(questIds[i]);

			// Calculate penalties for each task.
			for (int j = 0; j < taskIds.length; j++) {
				wa_penalty = wa_penalty
						+ dbAdapter.getWrongAnswerPenalty(taskIds[j]);
				h_penalty = h_penalty
						+ dbAdapter.getUsedHintsPenalty(taskIds[j]);
			}

			// Calculate total score for each quest.
			t_penalty = wa_penalty + h_penalty;
			t_score = t_score + (MAX_SCORE - t_penalty);

			View mView = (View) getLayoutInflater().inflate(
					R.layout.quest_list, null);
			Quest mQuest = dbAdapter.getQuest(questIds[i]);

			TextView quest_name = (TextView) mView
					.findViewById(R.id.quest_name);
			quest_name.setTypeface(typeFace);
			quest_name.setText(mQuest.getQuestName());

			TextView quest_score = (TextView) mView
					.findViewById(R.id.quest_score);
			quest_score.setTypeface(typeFace);
			quest_score.setText(getString(R.string.message_score)
					+ ((MAX_SCORE - t_penalty)) + "/100");

			ImageView quest_image = (ImageView) mView
					.findViewById(R.id.quest_image);
			Bitmap bmp = dbAdapter.getImage(mQuest.getImageId());
			quest_image.setImageBitmap(bmp);

			mLayout.addView(mView);

			wa_penalty = 0;
			h_penalty = 0;
			t_penalty = 0;

		}

		TextView total_score = (TextView) findViewById(R.id.textview_total_score);
		total_score.setTypeface(typeFace);
		total_score.setText(getString(R.string.message_total_score) + " "
				+ t_score);

	}

	/**
	 * AsyncTask that uploads the user's score.
	 */
	@SuppressLint("SimpleDateFormat")
	class UploadScoreTask extends AsyncTask<String, Integer, Boolean> {
		@Override
		protected void onPreExecute() {
			// Create a new JSON object.
			obj = new JSONResponseHandler();

			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			Toast.makeText(PathOverviewActivity.this, feedback,
					Toast.LENGTH_LONG).show();

			if (feedback == getString(R.string.toast_score_upload_ok)) {
				uploadButton.setEnabled(false);

				// Store score_uploaded value to SharedPreferences.
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean("score_uploaded", true);

				// Commit the edits.
				editor.commit();
			}

			super.onPostExecute(result);
		}

		@Override
		protected Boolean doInBackground(String... params) {

			// Fetch the list.
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
				String date = sdf.format(new Date());

				obj.postJSONScore(currentPathId, params[0], t_score, date);
			} catch (Exception e1) {
				feedback = getString(R.string.toast_server_error);
				Log.e(TAG, e1.getMessage());
				return null;
			}

			feedback = getString(R.string.toast_score_upload_ok);
			return null;
		}
	}

	/**
	 * Checks for any active network connection.
	 */
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
