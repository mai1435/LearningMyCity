package learningmycity.ui;

import java.lang.ref.WeakReference;

import learningmycity.DescriptionActivity;
import learningmycity.content.GPS;
import learningmycity.content.Quest;
import learningmycity.content.Task;
import learningmycity.db.DbAdapter;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.tsivas061.learningmycity.R;

/**
 * Implementation of TaskUI for the GPS task type.
 */
public class GPSTaskUI extends TaskUI {

	private boolean placeFound = false;

	// The start time for the thread.
	private double startTime;

	// Thread for checking GPS updates.
	private Thread mThread;

	// The Typeface for importing custom fonts.
	private Typeface typeFace;

	// The Animation for answerButton.
	private Animation animScale;

	// The dbAdapter for connecting to the database.
	private DbAdapter dbAdapter;

	// Views for the GPS Task
	private ProgressDialog progressDialog;
	private TextView gpsButton;
	private TextView answerButton;

	// The MediaPlayers for the sounds.
	private MediaPlayer mp_countdown;
	private MediaPlayer mp_answer;

	/**
	 * Message handler for retrieving messages from the GPS thread.
	 */
	static class messageHandler extends Handler {
		WeakReference<GPSTaskUI> mParent;

		public messageHandler(WeakReference<GPSTaskUI> parent) {
			mParent = parent;
		}

		@Override
		public void handleMessage(Message msg) {
			GPSTaskUI parent = mParent.get();
			switch (msg.what) {
			// If the user is not inside the radius.
			case 0:
				((GPS) parent.task).stopGPS();
				parent.gpsButton.setEnabled(true);
				parent.placeFound = false;
				parent.task.incWrongAnswers();
				parent.progressDialog.dismiss();

				// Initialise the MediaPlayer.
				parent.mp_answer = MediaPlayer.create(parent.activity,
						R.raw.wrong_answer);
				parent.mp_answer
						.setOnCompletionListener(new OnCompletionListener() {
							@Override
							public void onCompletion(MediaPlayer mp_answer) {
								mp_answer.reset();
								mp_answer.release();
								mp_answer = null;
							}
						});
				parent.mp_answer.start();

				Toast.makeText(parent.activity, R.string.GPS_wrong_location,
						Toast.LENGTH_LONG).show();
				break;
			// If the user is inside the radius.
			case 1:
				((GPS) parent.task).stopGPS();
				parent.placeFound = true;
				parent.progressDialog.dismiss();

				Intent intent = new Intent(parent.activity,
						DescriptionActivity.class);
				intent.putExtra("currentQuestId", parent.quest.getQuestId());
				parent.activity.startActivity(intent);

				// Initialise the MediaPlayer.
				parent.mp_answer = MediaPlayer.create(parent.activity,
						R.raw.correct_answer);
				parent.mp_answer
						.setOnCompletionListener(new OnCompletionListener() {
							@Override
							public void onCompletion(MediaPlayer mp_answer) {
								mp_answer.reset();
								mp_answer.release();
								mp_answer = null;
							}
						});
				parent.mp_answer.start();

				Toast.makeText(parent.activity, R.string.GPS_correct_location,
						Toast.LENGTH_LONG).show();

				// Change the image.
				parent.changeImage();

				parent.gpsButton.setVisibility(View.GONE);

				break;
			// If no GPS updates could be retrieved.
			case 2:
				((GPS) parent.task).stopGPS();
				parent.progressDialog.dismiss();
				parent.gpsButton.setEnabled(true);

				// Initialise the MediaPlayer.
				parent.mp_answer = MediaPlayer.create(parent.activity,
						R.raw.wrong_answer);
				parent.mp_answer
						.setOnCompletionListener(new OnCompletionListener() {
							@Override
							public void onCompletion(MediaPlayer mp_answer) {
								mp_answer.reset();
								mp_answer.release();
								mp_answer = null;
							}
						});
				parent.mp_answer.start();

				Toast.makeText(parent.activity, R.string.GPS_unavailable,
						Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}
	};

	Handler handler = new messageHandler(new WeakReference<GPSTaskUI>(this));

	public GPSTaskUI(Task task, int[] taskIds, Quest quest,
			ActionBarActivity activity) {
		super(task, taskIds, quest, activity);
		startTime = 0;
		progressDialog = new ProgressDialog(activity);

		// Initialise the DbAdapter.
		dbAdapter = new DbAdapter(activity);
	}

	/**
	 * Creates and returns the UI of the task.
	 */
	@Override
	public void createView() {

		createTitle();
		buildDescription();
		buildImage();
		buildQuestion();

		// Import external font style.
		typeFace = Typeface.createFromAsset(activity.getAssets(),
				"fonts/Caudex-Italic.ttf");

		animScale = AnimationUtils.loadAnimation(activity, R.anim.anim_scale);

		// Sets the answer button.
		answerButton = (TextView) activity
				.findViewById(R.id.button_task_validate_answer);
		answerButton.setTypeface(typeFace);

		// Sets the GPS button.
		LinearLayout mView = (LinearLayout) activity
				.findViewById(R.id.layout_custom);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL;

		gpsButton = new TextView(activity);
		gpsButton.setText(R.string.GPS_check_location);
		gpsButton.setTypeface(typeFace);
		gpsButton.setGravity(Gravity.CENTER);
		gpsButton.setLayoutParams(params);
		gpsButton.setAnimation(animScale);
		gpsButton.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.ic_action_location, 0, 0, 0);
		mView.addView(gpsButton);

		// Sets the progress dialog.
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMessage(activity.getString(R.string.GPS_progressBar));
		progressDialog.setMax(30);
		progressDialog.setCancelable(false);

		// Calls the setListeners method
		setListeners();
	}

	/**
	 * Creates an AlertDialog prompting the user to enable the GPS
	 */
	private void buildAlertMessageNoGps() {
		new AlertDialog.Builder(activity)
				.setCancelable(false)
				.setIcon(R.drawable.ic_action_location_off)
				.setTitle(R.string.GPS_prompt_title)
				.setMessage(activity.getString(R.string.GPS_prompt_message))
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								activity.startActivity(new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						}).setNegativeButton(R.string.no, null).show();
	}

	/**
	 * Returns the answer given by the user.
	 */
	@Override
	public Object getGivenAnswer() {
		return placeFound;
	}

	/**
	 * Changes the image view and adds it to layout.
	 */
	public void changeImage() {
		if (!(quest.getImageId() == 0)) {
			ImageView iv = (ImageView) activity.findViewById(R.id.image_task);

			Bitmap bmp = dbAdapter.getImage(quest.getImageId());

			iv.setImageBitmap(bmp);
			// iv.setAdjustViewBounds(true);
			// iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		}
	}

	/**
	 * Sets the button listeners.
	 */
	@Override
	public void setListeners() {

		// Listener for the continue button.
		answerButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (placeFound) {
					solveTask();
				} else {
					Toast.makeText(
							activity,
							activity.getString(R.string.toast_gps_answer_button),
							Toast.LENGTH_LONG).show();
				}
			}
		});

		// Listener for the GPS button.
		gpsButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// If GPS is disabled
				if (!((GPS) task).checkGPS()) {
					buildAlertMessageNoGps();
					// If GPS is enabled
				} else {
					// Sets the start time for the updates to system time.
					startTime = SystemClock.elapsedRealtime();

					// Disables the gpsButton.
					gpsButton.setEnabled(false);

					// Starts GPS updates.
					((GPS) task).startGPS();

					progressDialog.setProgress(0);
					progressDialog.show();

					// Creates a new Thread.
					mThread = new Thread() {
						public void run() {
							boolean stop = false;
							int oldProgress = 0;

							// Start the MediaPlayer.
							mp_countdown = MediaPlayer.create(activity,
									R.raw.countdown_answer);
							mp_countdown.setLooping(true);
							mp_countdown.start();

							while (!stop) {
								while ((SystemClock.elapsedRealtime() - startTime) < 30000) {
									int progress = (int) ((SystemClock
											.elapsedRealtime() - startTime) / 1000);
									if (progress > oldProgress) {
										oldProgress = progress;
										progressDialog.setProgress(progress);
									}
									// If the gpsHandler has received a new
									// location.
									if (((GPS) task).getOnLocationChanged()) {

										((GPS) task)
												.setOnLocationChanged(false);

										// Pause the MediaPlayer.
										mp_countdown.reset();
										mp_countdown.release();
										mp_countdown = null;

										// If the user is in range of the task.
										// Sends a true-message to
										// messageHandler.
										// Stops the thread.
										if (((GPS) task).isInsideTaskRange()) {
											handler.sendMessage(handler
													.obtainMessage(1));
											stop = true;
										} else {
											// If not in range of the task.
											// Sends a false-message to
											// messageHandler.
											// Stops the thread.
											handler.sendMessage(handler
													.obtainMessage(0));
											stop = true;
										}
										break;
									}
								}
								// If no GPS coordinates could be retrieved.
								// Sends a could not be retrieved message to
								// messageHandler.
								// Stops the thread.
								if (!stop) {
									// Pause the MediaPlayer.
									mp_countdown.reset();
									mp_countdown.release();
									mp_countdown = null;

									handler.sendMessage(handler
											.obtainMessage(2));
									stop = true;
								}
							}

						}
					};
					// Starts the thread if it is not running.
					if (!mThread.isAlive())
						mThread.start();
				}
			}
		});
	}
}
