package learningmycity;

import learningmycity.content.Quest;
import learningmycity.content.Task;
import learningmycity.db.DbAdapter;
import learningmycity.ui.CheckBoxTaskUI;
import learningmycity.ui.GPSTaskUI;
import learningmycity.ui.MultipleChoiceTaskUI;
import learningmycity.ui.OpenTaskUI;
import learningmycity.ui.TaskUI;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.tsivas061.learningmycity.R;

/**
 * The activity handling the UI of a given task.
 */
public class TaskActivity extends ActionBarActivity {

	// The taskUI for the current task;
	private TaskUI taskUI;

	// The quest the user is trying to solve.
	private Quest quest;

	// The task the user is trying to solve.
	private Task task;

	// The current quest id.
	private int currentQuestId;

	// The current task id.
	private int currentTaskId;

	// Array with quest IDs representing the path.
	// private int[] questIds;

	// Array with task IDs representing the quest.
	private int[] taskIds;

	// The dbAdapter for connecting to the database.
	private DbAdapter dbAdapter;

	// A boolean for active screen.
	private boolean activeScreen;

	// The SharedPreferences.
	private SharedPreferences prefs;

	/**
	 * Called when the activity is created. Fetches the current task (which is
	 * the same as the next task if a previous task was just solved, determines
	 * the task type, lays out the UI and awaits the user solving the task.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task);

		// Initialise the ActionBar.
		// ActionBar actionBar = getSupportActionBar();
		// actionBar.setBackgroundDrawable(getResources().getDrawable(
		// R.drawable.ic_actionbar_bg));

		// Initialise the DbAdapter.
		dbAdapter = new DbAdapter(this);

		// Initialise the SharedPreferences.
		prefs = PreferenceManager
				.getDefaultSharedPreferences(TaskActivity.this);

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

		// Fetch the current task.
		fetchTask();

		// Fetch the current view.
		fetchView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.task, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		// If the user clicked on overview menu button.
		if (id == R.id.open_overview) {

			if (!(task.getTaskType() == Task.GPS_TASK)) {
				// Start the Overview Activity.
				Intent i = new Intent(TaskActivity.this,
						DescriptionActivity.class);
				i.putExtra("currentQuestId", currentQuestId);
				startActivity(i);
			} else {
				Toast.makeText(getApplicationContext(),
						R.string.toast_no_description, Toast.LENGTH_SHORT)
						.show();
			}
			return true;
			// If the user clicked on hint menu button.
		} else if (id == R.id.open_hints) {

			Intent i = new Intent(TaskActivity.this, HintsActivity.class);
			i.putExtra("currentTaskId", currentTaskId);
			startActivity(i);
		} else if (id == R.id.exit_activity) {

			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.dialog_title_confirm_exit)
					.setMessage(R.string.dialog_text_confirm_exit)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}

							}).setNegativeButton(R.string.no, null).show();
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Called when the back key is pressed. Shows a dialog to the user.
	 */
	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.dialog_title_confirm_exit)
				.setMessage(R.string.dialog_text_confirm_exit)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}

						}).setNegativeButton(R.string.no, null).show();
	}

	/**
	 * Fetches the current task the user should solve from the database.
	 */
	private void fetchTask() {
		Bundle b = getIntent().getExtras();

		// Extract the currentQuestId.
		currentQuestId = b.getInt("currentQuestId");

		// Extract the currentTaskId.
		currentTaskId = b.getInt("currentTaskId");

		// Load quest from database.
		quest = dbAdapter.getQuest(currentQuestId);

		// Load task from database.
		task = dbAdapter.getTask(currentTaskId);

		// Load taskIds from database.
		taskIds = dbAdapter.getTaskIds(currentQuestId);
	}

	/**
	 * Creates the UI of the given task.
	 */
	private void fetchView() {
		switch (task.getTaskType()) {
		case Task.MULTIPLE_CHOICE_TASK:
			taskUI = new MultipleChoiceTaskUI(task, taskIds, quest, this);
			break;
		case Task.OPEN_TASK:
			taskUI = new OpenTaskUI(task, taskIds, quest, this);
			break;
		case Task.GPS_TASK:
			taskUI = new GPSTaskUI(task, taskIds, quest, this);
			break;
		case Task.GOOGLE_GOOGLES_TASK:
			break;
		case Task.CHECKBOX_TASK:
			taskUI = new CheckBoxTaskUI(task, taskIds, quest, this);
			break;
		default:
			break;
		}

		taskUI.createView();
	}

}
