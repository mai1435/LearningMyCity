package learningmycity;

import learningmycity.content.Hint;
import learningmycity.content.Task;
import learningmycity.db.DbAdapter;
import learningmycity.util.TypefaceSpan;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsivas061.learningmycity.R;

/**
 * The Hints activity of the application.
 */
public class HintsActivity extends ActionBarActivity {

	// The task the user is trying to solve.
	private Task task;

	// The dbAdapter for connecting to the database.
	private DbAdapter dbAdapter;

	// The Typeface for importing custom fonts.
	private Typeface typeFace;

	// The LinearLayout for importing views.
	private LinearLayout mView;

	// The current Task Id.
	private int currentTaskId;

	// The TextView hintButton.
	private TextView hintButton;

	// The Animation for hintButton.
	private Animation animScale;

	// The MediaPlayers.
	private MediaPlayer mp_hint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hints);

		// Creates a new adapter.
		dbAdapter = new DbAdapter(this);

		// Extract currentTaskId from Intent.
		Bundle b = getIntent().getExtras();
		currentTaskId = b.getInt("currentTaskId");

		// Load the current task from database.
		task = dbAdapter.getTask(currentTaskId);

		// Initialise LinearLayout.
		mView = (LinearLayout) findViewById(R.id.layout_hints);

		// Import external font style.
		typeFace = Typeface.createFromAsset(getAssets(),
				"fonts/Caudex-Italic.ttf");

		// Initialise the Animation.
		animScale = AnimationUtils.loadAnimation(HintsActivity.this,
				R.anim.anim_scale);

		// Set the title.
		// Custom Title.
		SpannableString s = new SpannableString(getString(R.string.title_hints));
		s.setSpan(new TypefaceSpan(HintsActivity.this, "Caudex-Italic.ttf"), 0,
				s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new ForegroundColorSpan(Color.argb(255, 255, 211, 155)), 0,
				s.length(), 0);
		setTitle(s);

		// Build the hints.
		buildHints();

		// Listener for the hint button.
		hintButton = (TextView) findViewById(R.id.button_task_get_hint);
		hintButton.setTypeface(typeFace);
		hintButton.startAnimation(animScale);

		hintButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {

				// Ask the user to confirm answer.
				new AlertDialog.Builder(HintsActivity.this)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle(R.string.dialog_title_confirm_hint)
						.setMessage(
								getString(R.string.dialog_text_confirm_hint)
										+ task.getNextUnusedHint().getPenalty())
						.setPositiveButton(R.string.yes,
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										Hint hint = task.getNextUnusedHint();
										if (hint != null) {
											// Initialise the MediaPlayer.
											mp_hint = MediaPlayer.create(
													HintsActivity.this,
													R.raw.hint);
											mp_hint.setOnCompletionListener(new OnCompletionListener() {
												@Override
												public void onCompletion(
														MediaPlayer mp_hint) {
													mp_hint.reset();
													mp_hint.release();
													mp_hint = null;
												}
											});
											mp_hint.start();

											task.setHintUsed(hint);
											buildHints();
										}
									}
								}).setNegativeButton(R.string.no, null).show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hints, menu);
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
	 * Builds the view for representing the hint.
	 */
	public void buildHints() {
		// Check for used hints and build them if any found.
		Hint[] hints = task.getUsedHints();

		if (hints.length == 1) {
			mView.removeAllViews();

			addTextView(hints[0].getDescription());

			// Import image if hint has any.
			if (!(hints[0].getImageId() == 0)) {
				addImageView(hints[0].getImageId());
			}
		} else if (hints.length == 2) {
			mView.removeAllViews();

			addTextView(hints[0].getDescription());

			// Import image if hint has any.
			if (!(hints[0].getImageId() == 0)) {
				addImageView(hints[0].getImageId());
				;
			}

			addTextView(hints[1].getDescription());

			// Import image if hint has any.
			if (!(hints[1].getImageId() == 0)) {
				addImageView(hints[1].getImageId());
			}

			hintButton = (TextView) findViewById(R.id.button_task_get_hint);
			hintButton.setEnabled(false);
		}
	}

	/**
	 * Add TextView to the LinearLayout.
	 */
	public void addTextView(String text) {

		// Custom parameters.
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		TextView tv = new TextView(this);
		tv.setText("- " + text);
		tv.setTypeface(typeFace);
		tv.setLayoutParams(params);
		mView.addView(tv);
	}

	/**
	 * Add ImageView to the LinearLayout.
	 */
	public void addImageView(int imageId) {

		// Custom parameters.
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		ImageView iv = new ImageView(this);
		Bitmap bmp = dbAdapter.getImage(imageId);

		iv.setImageBitmap(bmp);
		iv.setLayoutParams(params);
		mView.addView(iv);
	}
}
