package learningmycity;

import java.util.ArrayList;

import learningmycity.content.Path;
import learningmycity.db.DbAdapter;
import learningmycity.util.JSONResponseHandler;
import learningmycity.util.PathListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tsivas061.learningmycity.R;

/**
 * The List Activity with the available Paths.
 */
public class PathListActivity extends ListActivity {
	private static final String TAG = "PathListActivity";

	// The name of the SharedPreferences.
	public static final String PREFS_NAME = "MyPrefsFile";

	// The footer Views.
	private View footerView, loaderView;

	// The dbAdapter for the ListView.
	private PathListAdapter mAdapter;

	// The dbAdapter for connecting to the database.
	private DbAdapter dbAdapter;

	// The path the user chose.
	private Path path = null;

	// The JSON object.
	private JSONResponseHandler obj;

	// The ArrayList with the available paths.
	private ArrayList<Path> pathList;

	// The String for connection feedback.
	private String feedback;

	// The typeFace for importing custom fonts.
	private Typeface typeFace;

	// The MediaPlayer.
	private MediaPlayer mp_click;

	// The TextView for downloading info.
	private TextView download_info;

	// A boolean for active screen.
	private boolean activeScreen;

	// The SharedPreferences.
	private SharedPreferences prefs;

	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create a new DbAdapter.
		dbAdapter = new DbAdapter(this);

		// Import external font style.
		typeFace = Typeface.createFromAsset(this.getAssets(),
				"fonts/Caudex-Italic.ttf");

		// Initialise the SharedPreferences.
		prefs = PreferenceManager
				.getDefaultSharedPreferences(PathListActivity.this);

		// Check if it is to enable/disable the active screen.
		activeScreen = prefs.getBoolean("screen", false);
		if (activeScreen) {
			getWindow()
					.addFlags(
							android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			getWindow()
					.clearFlags(
							android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}

		// Initialise footerView.
		footerView = (View) getLayoutInflater().inflate(R.layout.footer_view,
				null);

		// Initialise TextView.
		TextView tv = (TextView) footerView.findViewById(R.id.footer);
		tv.setTypeface(typeFace);

		// Initialise loaderView.
		loaderView = (View) getLayoutInflater().inflate(R.layout.footer_loader,
				null);

		// Add a footerView to the ListView.
		getListView().addFooterView(footerView);

		// Create a new ListAdapter.
		mAdapter = new PathListAdapter(getApplicationContext());
		setListAdapter(mAdapter);

		getListView().setBackgroundResource(R.drawable.ic_background_path_list);
		getListView().setCacheColorHint(android.R.color.transparent);

		// Footer Listener.
		footerView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Initialise the MediaPlayer.
				mp_click = MediaPlayer.create(PathListActivity.this,
						R.raw.button);
				mp_click.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp_click) {
						mp_click.reset();
						mp_click.release();
						mp_click = null;
					}
				});
				mp_click.start();

				new FetchListTask().execute();
			}
		});
	}

	/**
	 * AsyncTask that fills the list with the available Paths.
	 */
	class FetchListTask extends AsyncTask<String, Integer, Boolean> {
		@Override
		protected void onPreExecute() {
			getListView().removeFooterView(footerView);
			getListView().addFooterView(loaderView, null, false);

			// Create a new JSON object.
			obj = new JSONResponseHandler();

			// Create a new ArrayList.
			pathList = new ArrayList<Path>();

			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// mAdapter.notifyDataSetChanged();
			getListView().removeFooterView(loaderView);
			getListView().addFooterView(footerView);

			// Add the paths to the list.
			for (int i = 0; i < pathList.size(); i++)
				addNewPlace(pathList.get(i));

			Toast.makeText(getApplicationContext(), feedback, Toast.LENGTH_LONG)
					.show();
			super.onPostExecute(result);
		}

		@Override
		protected Boolean doInBackground(String... params) {

			// Fetch the list.
			try {
				obj.fetchJSONList();
			} catch (Exception e1) {
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
					feedback = getString(R.string.toast_download_list_ok);
				} catch (JSONException e) {
					feedback = getString(R.string.toast_list_error);
					Log.e(TAG, e.getMessage());
					return null;
				}
			}

			return null;
		}
	}

	/**
	 * AsyncTask that downloads the selected Path.
	 */
	class FetchPathTask extends AsyncTask<Integer, Integer, Boolean> {
		@Override
		protected void onPreExecute() {
			getListView().removeFooterView(footerView);
			getListView().addFooterView(loaderView, null, false);

			download_info = (TextView) loaderView
					.findViewById(R.id.download_info);

			// Create a new JSON object.
			obj = new JSONResponseHandler();

			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// mAdapter.notifyDataSetChanged();
			getListView().removeFooterView(loaderView);
			getListView().addFooterView(footerView);

			if (path != null) {
				// Returning Intent.
				Intent returnIntent = new Intent();
				returnIntent.putExtra("path_id", path.getPathId());
				setResult(RESULT_OK, returnIntent);

				finish();
			}

			Toast.makeText(getApplicationContext(), feedback, Toast.LENGTH_LONG)
					.show();
			super.onPostExecute(result);
		}

		@Override
		protected Boolean doInBackground(Integer... params) {

			// Fetch the path.
			try {
				obj.fetchJSONPath(pathList.get(params[0]).getPathId());
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						download_info
								.setText(R.string.message_download_content);
					}
				});

				obj.fetchJSONImages(pathList.get(params[0]).getPathId());
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						download_info.setText(R.string.message_download_images);
					}
				});

			} catch (Exception e1) {
				feedback = getString(R.string.toast_server_error);
				Log.e(TAG, e1.getMessage());
				return null;
			}

			while (obj.parsingComplete)
				;

			try {
				// Reset the database
				dbAdapter.resetDatabase();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						download_info
								.setText(getString(R.string.message_reset_database));
					}
				});

				// Fill the database.
				dbAdapter.fillDatabase(params[0], obj);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						download_info.setText(R.string.message_fill_database);
					}
				});

				// Get the chosen path.
				path = (Path) getListAdapter().getItem(params[0]);

				// Store score_uploaded value to SharedPreferences.
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean("score_uploaded", false);

				// Commit the edits.
				editor.commit();

				feedback = getString(R.string.toast_download_path_ok);
			} catch (JSONException e) {
				feedback = getString(R.string.toast_path_error);
				Log.e(TAG, e.getMessage());
				return null;
			}

			return null;
		}
	}

	/**
	 * ListView click Listener.
	 */
	@Override
	protected void onListItemClick(ListView l, View v, final int position,
			long id) {
		// Initialise the MediaPlayer.
		mp_click = MediaPlayer.create(this, R.raw.button);
		mp_click.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp_click) {
				mp_click.reset();
				mp_click.release();
				mp_click = null;
			}
		});
		mp_click.start();

		// Ask the user to confirm answer
		new AlertDialog.Builder(this)
				.setCancelable(false)
				.setIcon(R.drawable.ic_action_download)
				.setTitle(pathList.get(position).getPathName())
				.setMessage(
						this.getString(R.string.dialog_text_confirm_download_path)
								+ " "
								+ pathList.get(position).getPathName()
								+ "?")
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								new FetchPathTask().execute(position);
							}
						}).setNegativeButton(R.string.no, null).create().show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.path_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Called when the back key is pressed. Shows a dialog to the user.
	 */
	/*
	 * @Override public void onBackPressed() { new AlertDialog.Builder(this)
	 * .setIcon(android.R.drawable.ic_dialog_alert)
	 * .setTitle(R.string.dialog_title_confirm_new_game)
	 * .setMessage(R.string.dialog_text_select_path)
	 * .setNegativeButton(R.string.ok, null).show(); }
	 */

	// Callback method used by PathDownloaderTask
	protected void addNewPlace(Path path) {
		// Log.i(TAG, "Entered addNewPlace()");
		if (path == null) {
		} else if (mAdapter.intersects(path.getPathName())) {
			Toast.makeText(getApplicationContext(),
					getString(R.string.toast_download_list_ok),
					Toast.LENGTH_LONG).show();
		} else {
			mAdapter.add(path);
		}
	}
}
