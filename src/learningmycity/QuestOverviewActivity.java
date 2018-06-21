package learningmycity;

import learningmycity.content.Quest;
import learningmycity.db.DbAdapter;
import learningmycity.util.TypefaceSpan;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsivas061.learningmycity.R;

/**
 * The overview activity of the application.
 */
public class QuestOverviewActivity extends ActionBarActivity {
	// Max score the user can achieve for a task.
	private final static int MAX_SCORE = 100;

	// The dbAdapter for connecting to the database.
	private DbAdapter dbAdapter;

	// Array with task IDs representing the quest.
	private int[] taskIds;

	// The quest the user has chosen.
	private Quest quest;

	// The currentQuestId of the path.
	private int currentQuestId;

	// The Typeface for importing custom fonts.
	private Typeface typeFace;

	// The TextView continueButton.
	private TextView continueButton;

	// Score Views.
	private TextView answerTextView;
	private TextView hintTextView;
	// private TextView totalPenaltyTextView;
	private TextView totalScoreTextView;
	private TextView messageTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quest_overview);

		// Creates a new adapter
		dbAdapter = new DbAdapter(this);

		// Import external font style.
		typeFace = Typeface.createFromAsset(getAssets(),
				"fonts/Caudex-Italic.ttf");

		// Set the title.
		// Custom Title.
		SpannableString s = new SpannableString(
				getString(R.string.title_quest_overview));
		s.setSpan(new TypefaceSpan(QuestOverviewActivity.this,
				"Caudex-Italic.ttf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new ForegroundColorSpan(Color.argb(255, 255, 211, 155)), 0,
				s.length(), 0);
		setTitle(s);

		// Creates a new map object.
		// mMap = ((MapFragment)
		// getFragmentManager().findFragmentById(R.id.map))
		// .getMap();

		// Instantiates the score views.
		answerTextView = (TextView) findViewById(R.id.textview_wrong_ans_penalty);
		answerTextView.setTypeface(typeFace);
		hintTextView = (TextView) findViewById(R.id.textview_hints_penalty);
		hintTextView.setTypeface(typeFace);
		// totalPenaltyTextView = (TextView)
		// findViewById(R.id.textview_total_penalty);
		// totalPenaltyTextView.setTypeface(typeFace);
		totalScoreTextView = (TextView) findViewById(R.id.textview_total_score);
		totalScoreTextView.setTypeface(typeFace);
		messageTextView = (TextView) findViewById(R.id.textview_penalty_message);
		messageTextView.setTypeface(typeFace);

		// Extract currentQuestId from the Intent.
		Bundle b = getIntent().getExtras();
		currentQuestId = b.getInt("currentQuestId");
		quest = dbAdapter.getQuest(currentQuestId);

		// Build the image.
		buildImage();

		// Show the penalties on the screen.
		showPenalties();

		// Shows the map on the screen.
		// setUpMapIfNeeded();

		// Initialise continueButton.
		continueButton = (TextView) findViewById(R.id.button_next_quest);
		continueButton.setTypeface(typeFace);

		// continueButton Listener.
		continueButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				setResult(Activity.RESULT_OK);
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.quest_overview, menu);
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
	public void showPenalties() {

		taskIds = dbAdapter.getTaskIds(currentQuestId);

		int wa_penalty = 0;
		int h_penalty = 0;
		int t_penalty = 0;

		for (int i = 0; i < taskIds.length; i++) {
			wa_penalty = wa_penalty
					+ dbAdapter.getWrongAnswerPenalty(taskIds[i]);
			h_penalty = h_penalty + dbAdapter.getUsedHintsPenalty(taskIds[i]);
		}

		t_penalty = wa_penalty + h_penalty;

		answerTextView.setText("-"
				+ getApplicationContext().getString(
						R.string.message_wrong_answers) + wa_penalty);
		hintTextView.setText("-"
				+ getApplicationContext()
						.getString(R.string.message_used_hints) + h_penalty);
		totalScoreTextView.setText("-"
				+ getApplicationContext().getString(
						R.string.message_total_score) + (MAX_SCORE - t_penalty)
				+ "/100");

		if (t_penalty == 0) {
			messageTextView.setText(R.string.message_perfect);
		} else if (t_penalty < 10) {
			messageTextView.setText(R.string.message_very_good);
		} else if (t_penalty < 20) {
			messageTextView.setText(R.string.message_good);
		} else {
			messageTextView.setText(R.string.message_bad);
		}
	}

	/**
	 * Builds the image view and adds it to layout.
	 */
	public void buildImage() {
		if (!(quest.getImageId() == 0)) {
			ImageView iv = (ImageView) findViewById(R.id.image_quest);

			Bitmap bmp = dbAdapter.getImage(quest.getImageId());

			iv.setImageBitmap(bmp);
			// iv.setAdjustViewBounds(true);
			// iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		}
	}

	/**
	 * Shows the map on the screen.
	 */
	/*
	 * private void setUpMapIfNeeded() { // Do a null check to confirm that we
	 * have not already instantiated the // map. if (mMap == null) { mMap =
	 * ((MapFragment) getFragmentManager().findFragmentById(
	 * R.id.map)).getMap();
	 * 
	 * // Check if we were successful in obtaining the map. if (mMap != null) {
	 * // The Map is verified. It is now safe to manipulate the map.
	 * mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
	 * 
	 * mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(quest
	 * .getLatitude(), quest.getLongitude())));
	 * 
	 * mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
	 * 
	 * mMap.addMarker(new MarkerOptions() // Set the Marker's position
	 * .position( new LatLng(quest.getLatitude(), quest .getLongitude())) // Set
	 * the title of the Marker's information window
	 * .title(quest.getQuestName())); } } else { // The Map is verified. It is
	 * now safe to manipulate the map.
	 * mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
	 * 
	 * mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(quest
	 * .getLatitude(), quest.getLongitude())));
	 * 
	 * mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
	 * 
	 * mMap.addMarker(new MarkerOptions() // Set the Marker's position
	 * .position( new LatLng(quest.getLatitude(), quest .getLongitude())) // Set
	 * the title of the Marker's information window
	 * .title(quest.getQuestName())); } }
	 */
}
