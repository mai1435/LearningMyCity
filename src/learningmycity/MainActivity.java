package learningmycity;

import learningmycity.content.Path;
import learningmycity.content.Quest;
import learningmycity.db.DbAdapter;
import learningmycity.util.TypefaceSpan;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tsivas061.learningmycity.R;

/**
 * The main activity of the application.
 */
public class MainActivity extends ActionBarActivity {

	private static final String TAG = "MainActivity";

	// Codes for the Intents.
	private static final int GET_NEXT_TASK_REQUEST_CODE = 1;
	private static final int GET_OVERVIEW_REQUEST_CODE = 2;
	private static final int GET_CHOOSE_PATH_REQUEST_CODE = 3;

	// The dbAdapter for connecting to the database.
	private DbAdapter dbAdapter;

	// The path the user has chosen.
	private Path path = null;

	// The quest the user has to solve.
	private Quest quest = null;

	// A boolean for activity focus.
	private boolean hasFocus = true;

	// A boolean for music preference.
	private boolean playMusic;

	// A boolean for active screen.
	private boolean activeScreen;

	// A boolean for battery check.
	private boolean batteryCheck;

	// The currentPathId
	private int currentPathId;

	// The currentTaskId of the path.
	private int currentTaskId;

	// The currentQuestId of the path.
	private int currentQuestId;

	// The Buttons.
	private Button continueButton, newGameButton, leaderboardButton;

	// The typeFace for importing custom fonts.
	private Typeface typeFace;

	// The MediaPlayers.
	private MediaPlayer mp_click, mp_main;

	// The SharedPreferences.
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.i(TAG, "onCreate");

		// Import external font style.
		typeFace = Typeface.createFromAsset(getAssets(),
				"fonts/Caudex-Italic.ttf");

		// Initialise the ActionBar.
		// Custom Background.
		ActionBar actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.ic_background_actionbar));

		// Custom Title.
		SpannableString s = new SpannableString("");
		s.setSpan(new TypefaceSpan(this, "Caudex-Italic.ttf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new ForegroundColorSpan(Color.argb(255, 0, 127, 255)), 0,
				s.length(), 0);
		actionBar.setTitle(s);

		// Initialise the Continue Button.
		continueButton = (Button) findViewById(R.id.button_continue_game);
		continueButton.setTypeface(typeFace);

		// Initialise the NewGame Button.
		newGameButton = (Button) findViewById(R.id.button_new_game);
		newGameButton.setTypeface(typeFace);

		// Initialise the Leaderboard Button.
		leaderboardButton = (Button) findViewById(R.id.button_leaderboard);
		leaderboardButton.setTypeface(typeFace);

		// Initialise the SharedPreferences.
		prefs = PreferenceManager
				.getDefaultSharedPreferences(MainActivity.this);

		// Connect to the database.
		openDatabase();

		// Check if it is to enable/disable the battery checking.
		batteryCheck = prefs.getBoolean("battery", true);
		if (batteryCheck) {
			// Check for battery level.
			checkBattery();
		}

		// Try Load Game State.
		loadGameState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume");

		hasFocus = true;

		// Check if it is to enable/disable the Continue button.
		if (!(currentPathId == 0)) {
			continueButton.setEnabled(true);
		} else {
			continueButton.setEnabled(false);
		}

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

		// Check if it is to enable/disable the music.
		playMusic = prefs.getBoolean("music", true);
		if (playMusic) {
			// Create a 2000ms delay.
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					if (hasFocus) {
						// Initialise the MediaPlayer.
						mp_main = MediaPlayer.create(MainActivity.this,
								R.raw.main);
						mp_main.setLooping(true);
						mp_main.start();
					}
				}
			}, 2000);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i(TAG, "onStop");

		hasFocus = false;

		if (playMusic) {
			// Release the MediaPlayer.
			if (!(mp_main == null)) {
				mp_main.reset();
				mp_main.release();
				mp_main = null;
			}
		}
	}

	/**
	 * Button Listeners.
	 */
	public void onClick(View view) {
		switch (view.getId()) {
		// If the user starts a new game.
		case R.id.button_new_game:
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

			if (!(currentPathId == 0)) {
				// Dialog to confirm new game.
				new AlertDialog.Builder(this)
						.setCancelable(false)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle(R.string.dialog_title_confirm_new_game)
						.setMessage(
								this.getString(R.string.dialog_text_confirm_new_game))
						.setPositiveButton(R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// Intent for the PathListActivity.
										if (isNetworkAvailable()) {
											Intent i = new Intent(
													MainActivity.this,
													PathListActivity.class);
											startActivityForResult(i,
													GET_CHOOSE_PATH_REQUEST_CODE);
										} else {
											Toast.makeText(
													getApplicationContext(),
													R.string.toast_no_internet_new_game,
													Toast.LENGTH_LONG).show();
										}
									}
								})
						.setNegativeButton(R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).show();
			} else {
				if (isNetworkAvailable()) {
					Intent i = new Intent(MainActivity.this,
							PathListActivity.class);
					startActivityForResult(i, GET_CHOOSE_PATH_REQUEST_CODE);
				} else {
					Toast.makeText(getApplicationContext(),
							R.string.toast_no_internet_new_game,
							Toast.LENGTH_LONG).show();
				}
			}

			break;
		// If the user continues a game.
		case R.id.button_continue_game:
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

			// Load the task.
			loadTask();
			break;
		// If the user opens the leaderboard.
		case R.id.button_leaderboard:
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

			if (isNetworkAvailable()) {
				Intent i = new Intent(MainActivity.this,
						LeaderboardActivity.class);
				startActivity(i);
				break;
			} else {
				Toast.makeText(getApplicationContext(),
						R.string.toast_no_internet_leaderboard,
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
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

			return true;
		} else if (id == R.id.settings) {
			Intent i = new Intent(MainActivity.this, PrefsActivity.class);
			startActivity(i);
		} else if (id == R.id.about) {
			Intent i = new Intent(MainActivity.this, AboutActivity.class);
			startActivity(i);
		} else if (id == R.id.feedback) {
			Intent i = new Intent(MainActivity.this, FeedbackActivity.class);
			startActivity(i);
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Creates the database (it is only run one time for each installation of
	 * the application).
	 */
	private void openDatabase() {
		dbAdapter = new DbAdapter(this);
		dbAdapter.open();
		dbAdapter.close();
	}

	/**
	 * Resets/Updates the game state.
	 */
	public void updateGameState(int chosen_pathId) {
		// Reset the Path to the new chosen one.
		path = dbAdapter.getPath(chosen_pathId);
		currentPathId = path.getPathId();

		// Reset the Quest to the current path's first quest.
		currentQuestId = path.getFirstQuestId();
		quest = dbAdapter.getQuest(currentQuestId);

		// Reset the Task to the current quest's first task.
		currentTaskId = quest.getFirstTaskId();

		// Update the game state.
		dbAdapter.updateGameState(currentPathId, currentQuestId, currentTaskId);
	}

	/**
	 * Loads the current game state or creates one if the game has just started.
	 */
	private void loadGameState() throws CursorIndexOutOfBoundsException {
		// Load the path.
		// Load the path id from the database.
		currentPathId = dbAdapter.getGameStatePathId();
		// Load the path stored in the game state table.
		path = dbAdapter.getPath(currentPathId);

		// Load the quest.
		// Load the quest id from the database.
		currentQuestId = dbAdapter.getGameStateQuestId();
		// Load the quest stored in the game state table.
		quest = dbAdapter.getQuest(currentQuestId);

		// Load the task.
		// Load the task id from the database.
		currentTaskId = dbAdapter.getGameStateTaskId();
	}

	/**
	 * Loads the UI for the current task.
	 */
	private void loadTask() {
		// If there are more quests in the game to be solved.
		if (!(currentQuestId == -1)) {
			// Intent to start TaskActivity.
			Intent i = new Intent(MainActivity.this, TaskActivity.class);
			i.putExtra("pathId", currentPathId);
			i.putExtra("currentQuestId", currentQuestId);
			i.putExtra("currentTaskId", currentTaskId);

			startActivityForResult(i, GET_NEXT_TASK_REQUEST_CODE);

			// Else the path is finished
		} else {
			Intent i = new Intent(MainActivity.this, PathOverviewActivity.class);
			i.putExtra("pathId", currentPathId);
			startActivity(i);

			Toast.makeText(getApplicationContext(), "Path Finished",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Loads the UI for the next task.
	 */
	private void loadNextTask() {
		// Update the penalties table with the penalties the user acquired in
		// the task that just solved.
		dbAdapter.setPenalties(currentTaskId);

		// Load the next task id from the quest.
		int nextTaskId = quest.getNextTaskId(currentTaskId);

		// If this Quest has still Tasks to be solved.
		if (!(nextTaskId == -1)) {
			currentTaskId = nextTaskId;

			// Update the database
			dbAdapter.updateGameState(currentPathId, currentQuestId,
					currentTaskId);

			// Load next Task.
			loadTask();

			// Else if this Quest is solved loads the Overview Activity.
		} else {
			// Load next Quest.
			loadNextQuest();
		}
	}

	/**
	 * Loads the next quest.
	 */
	private void loadNextQuest() {
		quest.setCompleted(true);

		// Start the Overview Activity.
		Intent i = new Intent(MainActivity.this, QuestOverviewActivity.class);
		i.putExtra("currentQuestId", currentQuestId);
		startActivityForResult(i, GET_OVERVIEW_REQUEST_CODE);

		currentQuestId = path.getNextQuestId(currentQuestId);

		// If the Path has still Quests to be solved.
		if (!(currentQuestId == -1)) {
			// Get the next quest.
			quest = dbAdapter.getQuest(currentQuestId);

			// Set the task id to the new quest's first task id.
			currentTaskId = quest.getFirstTaskId();
		} else {
			// No more quests - Game is finished
			Toast.makeText(getApplicationContext(), "Path Finished",
					Toast.LENGTH_SHORT).show();
		}

		// Update the database
		dbAdapter.updateGameState(currentPathId, currentQuestId, currentTaskId);
	}

	/**
	 * Called when an activity has been finished. The user will then be
	 * presented with a new task.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK
				&& requestCode == GET_NEXT_TASK_REQUEST_CODE) {
			loadNextTask();
		} else if (resultCode == Activity.RESULT_OK
				&& requestCode == GET_OVERVIEW_REQUEST_CODE) {
			loadTask();
		} else if (resultCode == Activity.RESULT_OK
				&& requestCode == GET_CHOOSE_PATH_REQUEST_CODE) {
			// Get the pathId the user chose and reset the game
			Bundle b = data.getExtras();
			int chosen_pathId = b.getInt("path_id");

			// Set game state to default values.
			updateGameState(chosen_pathId);

			// Load next tast.
			loadTask();
		}
	}

	/**
	 * Checks for battery level upon startup.
	 */
	private void checkBattery() {
		Intent batteryIntent = registerReceiver(null, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
		int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		float status = ((float) level / (float) scale) * 100.0f;

		if (status < 80) {
			// Ask the user to confirm answer
			new AlertDialog.Builder(this)
					.setCancelable(false)
					.setIcon(R.drawable.ic_action_battery)
					.setTitle(R.string.dialog_title_confirm_battery)
					.setMessage(
							this.getString(R.string.dialog_text_confirm_battery))
					.setPositiveButton(R.string.yes, null)
					.setNegativeButton(R.string.no,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
									System.exit(0);
								}
							}).show();
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
