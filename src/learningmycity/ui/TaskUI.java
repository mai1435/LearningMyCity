package learningmycity.ui;

import learningmycity.content.Quest;
import learningmycity.content.Task;
import learningmycity.db.DbAdapter;
import learningmycity.util.TypefaceSpan;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tsivas061.learningmycity.R;

/**
 * The abstract class defining the structure of all Task UIs.
 */
public abstract class TaskUI {

	// The task to be solved.
	protected Task task;

	// Array of task IDs representing the quest.
	protected int[] taskIds;

	// The quest to be solved.
	protected Quest quest;

	// The activity requesting this UI.
	protected ActionBarActivity activity;

	// The dbAdapter for connecting to the database.
	private DbAdapter dbAdapter;

	// The Typeface for importing custom font.
	private Typeface typeFace;

	// The Animation for answerButton.
	private Animation animScale;

	// The TextView answerButton.
	private TextView answerButton;

	// The MediaPlayers for the sounds.
	private MediaPlayer mp_countdown;
	private MediaPlayer mp_answer;

	/**
	 * Creates a new instance of the UI handler.
	 */
	public TaskUI(Task task, int[] taskIds, Quest quest,
			ActionBarActivity activity) {
		this.task = task;
		this.activity = activity;
		this.taskIds = taskIds;
		this.quest = quest;

		// Initialise the DbAdapter.
		dbAdapter = new DbAdapter(this.activity);

		// Import external font style.
		typeFace = Typeface.createFromAsset(this.activity.getAssets(),
				"fonts/Caudex-Italic.ttf");

		// Initialise the Animation.
		animScale = AnimationUtils.loadAnimation(this.activity,
				R.anim.anim_scale);
	}

	/**
	 * Sets the layout title for the task activity.
	 */
	protected void createTitle() {

		// Initialise the ActionBar.
		ActionBar actionBar = activity.getSupportActionBar();

		int current = 0;
		for (int i = 0; i < taskIds.length; i++) {
			if (taskIds[i] == task.getId())
				current = i + 1;
		}

		if (task.getTaskType() == 3) {
			// Custom Title.
			SpannableString count = new SpannableString(current + "/"
					+ taskIds.length);
			count.setSpan(new TypefaceSpan(activity, "Caudex-Italic.ttf"), 0,
					count.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			count.setSpan(
					new ForegroundColorSpan(Color.argb(255, 255, 211, 155)), 0,
					count.length(), 0);

			SpannableString place = new SpannableString(
					activity.getString(R.string.message_target_location));
			place.setSpan(new TypefaceSpan(activity, "Caudex-Italic.ttf"), 0,
					place.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			place.setSpan(
					new ForegroundColorSpan(Color.argb(255, 255, 211, 155)), 0,
					place.length(), 0);

			actionBar.setTitle(place);
			actionBar.setSubtitle(count);
		} else {
			// Custom Title.
			SpannableString count = new SpannableString(current + "/"
					+ taskIds.length);
			count.setSpan(new TypefaceSpan(activity, "Caudex-Italic.ttf"), 0,
					count.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			count.setSpan(
					new ForegroundColorSpan(Color.argb(255, 255, 211, 155)), 0,
					count.length(), 0);

			SpannableString place = new SpannableString(quest.getQuestName());
			place.setSpan(new TypefaceSpan(activity, "Caudex-Italic.ttf"), 0,
					place.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			place.setSpan(
					new ForegroundColorSpan(Color.argb(255, 255, 211, 155)), 0,
					place.length(), 0);

			actionBar.setTitle(place);
			actionBar.setSubtitle(count);
		}

	}

	/**
	 * Called when a task is solved and the Android activity shall finish
	 * successfully.
	 */
	public void solveTask() {
		task.setCompleted(true);

		activity.setResult(Activity.RESULT_OK);
		activity.finish();
	}

	/**
	 * Creates and returns the UI of the task.
	 */
	public abstract void createView();

	/**
	 * Returns the answer given by the user.
	 */
	public abstract Object getGivenAnswer();

	/**
	 * Builds the question(s) view and adds them to layout.
	 */
	public void buildQuestion() {
		String question = task.getQuestion().getQuestion();

		TextView tv = (TextView) activity
				.findViewById(R.id.textview_task_question);
		tv.setText(question);
		tv.setTypeface(typeFace);
	}

	/**
	 * Builds the description view and adds it to layout.
	 */
	public void buildDescription() {
		String description = task.getDescription();

		TextView tv = (TextView) activity
				.findViewById(R.id.textview_task_description);
		tv.setText(description);
		tv.setTypeface(typeFace);
	}

	/**
	 * Builds the image view and adds it to layout.
	 */
	public void buildImage() {
		if (!(task.getImageId() == 0)) {
			ImageView iv = (ImageView) activity.findViewById(R.id.image_task);

			Bitmap bmp = dbAdapter.getImage(task.getImageId());

			iv.setImageBitmap(bmp);
			// iv.setAdjustViewBounds(true);
			// iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		}
	}

	/**
	 * Sets the listeners for the buttons in the layout.
	 */
	public void setListeners() {

		// Listener for the continue button.
		answerButton = (TextView) activity
				.findViewById(R.id.button_task_validate_answer);

		answerButton.setTypeface(typeFace);
		answerButton.startAnimation(animScale);

		answerButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {

				// Ask the user to confirm answer
				new AlertDialog.Builder(activity)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle(R.string.dialog_title_confirm_answer)
						.setMessage(
								activity.getString(R.string.dialog_text_confirm_answer)
										+ " " + task.getPenalty())
						// If user clicks Yes.
						.setPositiveButton(R.string.yes,
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {

										// Initialise the animation.
										animScale = AnimationUtils
												.loadAnimation(activity,
														R.anim.anim_alpha);
										answerButton.startAnimation(animScale);

										// Set answerButton unclickable.
										answerButton.setClickable(false);

										// Start the MediaPlayer.
										mp_countdown = MediaPlayer.create(
												activity,
												R.raw.countdown_answer);
										mp_countdown
												.setOnCompletionListener(new OnCompletionListener() {
													@Override
													public void onCompletion(
															MediaPlayer mp_countdown) {
														mp_countdown.reset();
														mp_countdown.release();
														mp_countdown = null;
													}
												});
										mp_countdown.start();

										// Create a 5000ms delay.
										Handler handler = new Handler();
										handler.postDelayed(new Runnable() {
											public void run() {

												// Pause the MediaPlayer.
												mp_countdown.reset();
												mp_countdown.release();
												mp_countdown = null;

												// Set answerButton clickable.
												answerButton.setClickable(true);

												// If the answer was correct.
												if (task.validateTask(getGivenAnswer())) {

													// Solve the task.
													solveTask();

													// Initialise the
													// MediaPlayer.
													mp_answer = MediaPlayer
															.create(activity,
																	R.raw.correct_answer);
													mp_answer
															.setOnCompletionListener(new OnCompletionListener() {
																@Override
																public void onCompletion(
																		MediaPlayer mp_answer) {
																	mp_answer
																			.reset();
																	mp_answer
																			.release();
																	mp_answer = null;
																}
															});
													mp_answer.start();

													Toast.makeText(
															activity,
															R.string.toast_correct_answer,
															Toast.LENGTH_LONG)
															.show();

													// If the answer was
													// incorrect.
												} else {
													// Increase wrong answers.
													task.incWrongAnswers();

													// Initialise the
													// MediaPlayer.
													mp_answer = MediaPlayer
															.create(activity,
																	R.raw.wrong_answer);
													mp_answer
															.setOnCompletionListener(new OnCompletionListener() {
																@Override
																public void onCompletion(
																		MediaPlayer mp_answer) {
																	mp_answer
																			.reset();
																	mp_answer
																			.release();
																	mp_answer = null;
																}
															});
													mp_answer.start();

													Toast.makeText(
															activity,
															R.string.toast_wrong_answer,
															Toast.LENGTH_LONG)
															.show();

												}
											}
										}, 5000);
									}
									// If user clicks No.
								}).setNegativeButton(R.string.no, null).show();
			}
		});
	}
}
