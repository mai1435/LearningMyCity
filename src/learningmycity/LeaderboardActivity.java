package learningmycity;

import java.util.ArrayList;
import java.util.Collections;

import learningmycity.content.Path;
import learningmycity.content.Score;
import learningmycity.db.DbAdapter;
import learningmycity.util.JSONResponseHandler;
import learningmycity.util.TypefaceSpan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.tsivas061.learningmycity.R;

/**
 * The Leaderboard activity of the application.
 */
public class LeaderboardActivity extends ActionBarActivity implements
		OnItemSelectedListener {
	private static final String TAG = "LeaderboardActivity";

	// The dbAdapter for connecting to the database.
	private DbAdapter dbAdapter;

	// The Typeface for importing custom fonts.
	private Typeface typeFace;

	// The JSON object.
	private JSONResponseHandler obj;

	// The Spinner.
	private Spinner spinner;

	// The ArrayList with the available paths.
	private ArrayList<Path> pathList;

	// The ArrayList with the available names.
	private ArrayList<String> pathNameList;

	// The ArrayList with the available names.
	private ArrayList<Score> scoreList;

	// The ProgressBar.
	private ProgressBar progressBar;

	// The String for connection feedback.
	private String feedback;

	// The boolean for connection feedback.
	private boolean error = false;

	// The MediaPlayer.
	private MediaPlayer mp_click;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.leaderboard);

		// Creates a new adapter.
		dbAdapter = new DbAdapter(this);

		// Import external font style.
		typeFace = Typeface.createFromAsset(getAssets(),
				"fonts/Caudex-Italic.ttf");

		// Set the title.
		// Custom Title.
		SpannableString s = new SpannableString(
				getString(R.string.title_leaderboard));
		s.setSpan(new TypefaceSpan(LeaderboardActivity.this,
				"Caudex-Italic.ttf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new ForegroundColorSpan(Color.argb(255, 255, 211, 155)), 0,
				s.length(), 0);
		setTitle(s);

		// Creates a new spinner.
		spinner = (Spinner) findViewById(R.id.paths_spinner);
		spinner.setOnItemSelectedListener(this);

		// Execute the AsyncTask and fill in the spinner.
		new FetchListTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.leaderboard, menu);
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

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// Initialise the MediaPlayer.
		mp_click = MediaPlayer.create(LeaderboardActivity.this, R.raw.button);
		mp_click.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp_click) {
				mp_click.reset();
				mp_click.release();
				mp_click = null;
			}
		});
		mp_click.start();

		new FetchScoreTask().execute(position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	/**
	 * AsyncTask that fills the list with the available Paths.
	 */
	class FetchListTask extends AsyncTask<String, Integer, Boolean> {
		@Override
		protected void onPreExecute() {

			// Initialise the ProgressBar.
			progressBar = (ProgressBar) findViewById(R.id.progressBar);

			// Create a new JSON object.
			obj = new JSONResponseHandler();

			// Create a new ArrayList.
			pathList = new ArrayList<Path>();

			// Create a new ArrayList.
			pathNameList = new ArrayList<String>();

			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Boolean result) {

			if (error) {
				Toast.makeText(getApplicationContext(), feedback,
						Toast.LENGTH_LONG).show();
			} else {
				// Add the path names to the spinner.
				for (int i = 0; i < pathList.size(); i++)
					pathNameList.add(pathList.get(i).getPathName());

				ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
						LeaderboardActivity.this,
						android.R.layout.simple_spinner_item, pathNameList);
				spinnerArrayAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner.setAdapter(spinnerArrayAdapter);
			}
			super.onPostExecute(result);
		}

		@Override
		protected Boolean doInBackground(String... params) {

			// Fetch the list.
			try {
				obj.fetchJSONList();
			} catch (Exception e1) {
				error = true;
				feedback = getString(R.string.toast_server_error);
				Log.e(TAG, e1.getMessage());
				return null;
			}
			while (obj.parsingComplete)
				;

			JSONArray paths = obj.getList();

			for (int i = 0; i < paths.length(); i++) {
				JSONObject path;
				try {
					path = paths.getJSONObject(i);

					pathList.add(new Path(dbAdapter, path.getInt("pathId"),
							path.getString("pathName"), path
									.getString("pathDescription")));
				} catch (JSONException e) {
					error = true;
					feedback = getString(R.string.toast_server_error);
					Log.e(TAG, e.getMessage());
					return null;
				}
			}

			return null;
		}
	}

	/**
	 * AsyncTask that fills the list with the available Scores.
	 */
	class FetchScoreTask extends AsyncTask<Integer, Integer, Boolean> {
		@Override
		protected void onPreExecute() {
			Log.e(TAG, "onPreExecute");

			// Make the ProgressBar visible.
			progressBar.setVisibility(View.VISIBLE);
			// Create a new JSON object.
			obj = new JSONResponseHandler();

			// Create a new ArrayList.
			scoreList = new ArrayList<Score>();

			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Boolean result) {

			// Make the ProgressBar invisible.
			progressBar.setVisibility(View.GONE);

			if (error) {
				Toast.makeText(getApplicationContext(), feedback,
						Toast.LENGTH_LONG).show();
			} else {
				// Sort the scoreList in descending order.
				Collections.sort(scoreList);

				// Initialise the ScrollView.
				ScrollView sv = (ScrollView) LeaderboardActivity.this
						.findViewById(R.id.score_scrollView);
				LayoutParams params = new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

				// Create the LinearLayout for each score raw.
				LinearLayout outerLayout = new LinearLayout(
						LeaderboardActivity.this);
				outerLayout.setOrientation(LinearLayout.VERTICAL);
				outerLayout.setLayoutParams(params);

				sv.removeAllViews();
				sv.addView(outerLayout);

				// Add the scores to the ScrollView.
				for (int i = 0; i < scoreList.size(); i++) {

					// Create the LinearLayout for each score column.
					LinearLayout innerLayout = new LinearLayout(
							LeaderboardActivity.this);
					innerLayout.setOrientation(LinearLayout.HORIZONTAL);
					innerLayout.setLayoutParams(params);

					// Create the TextView.
					android.widget.TableRow.LayoutParams params2 = new TableRow.LayoutParams(
							0, LayoutParams.MATCH_PARENT, 1f);

					TextView tv1 = new TextView(LeaderboardActivity.this);
					tv1.setText(scoreList.get(i).getUserName());
					tv1.setLayoutParams(params2);
					tv1.setTypeface(typeFace);
					innerLayout.addView(tv1);

					TextView tv2 = new TextView(LeaderboardActivity.this);
					tv2.setText(scoreList.get(i).getScore());
					tv2.setLayoutParams(params2);
					tv2.setTypeface(typeFace);
					innerLayout.addView(tv2);

					TextView tv3 = new TextView(LeaderboardActivity.this);
					tv3.setText(scoreList.get(i).getDate());
					tv3.setLayoutParams(params2);
					tv3.setTypeface(typeFace);
					innerLayout.addView(tv3);

					outerLayout.addView(innerLayout);

				}
			}
			super.onPostExecute(result);
		}

		@Override
		protected Boolean doInBackground(Integer... params) {

			// Fetch the scores.
			try {
				obj.fetchJSONScore(pathList.get(params[0]).getPathId());
			} catch (Exception e1) {
				error = true;
				feedback = getString(R.string.toast_server_error);
				Log.e(TAG, e1.getMessage());
				return null;
			}
			while (obj.parsingComplete)
				;

			JSONArray scores = obj.getScores();

			for (int i = 0; i < scores.length(); i++) {
				JSONObject score;
				try {
					score = scores.getJSONObject(i);

					scoreList.add(new Score(score.getString("pathId"), score
							.getString("userName"), score.getString("score"),
							score.getString("date")));
				} catch (JSONException e) {
					error = true;
					feedback = getString(R.string.toast_server_error);
					Log.e(TAG, e.getMessage());
					return null;
				}
			}

			return null;
		}
	}
}
