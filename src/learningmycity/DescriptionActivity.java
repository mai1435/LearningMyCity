package learningmycity;

import learningmycity.content.Quest;
import learningmycity.db.DbAdapter;
import learningmycity.util.TypefaceSpan;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.tsivas061.learningmycity.R;

/**
 * The Description activity of the application.
 */
public class DescriptionActivity extends ActionBarActivity {

	// The TextView.
	private TextView descriptionTextView;

	// The ImageView.
	private ImageView descriptionImageView;

	// The quest the user has chosen.
	private Quest quest;

	// The quest_id.
	private int quest_id;

	// The dbAdapter for connecting to the database.
	private DbAdapter dbAdapter;

	// The Typeface for importing custom fonts.
	private Typeface typeFace;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.description);

		// Creates a new adapter
		dbAdapter = new DbAdapter(this);

		// Import external font style.
		typeFace = Typeface.createFromAsset(getAssets(),
				"fonts/Caudex-Italic.ttf");

		// Initialise the description TextView.
		descriptionTextView = (TextView) findViewById(R.id.textview_description);

		// Initialise the description ImageView.
		descriptionImageView = (ImageView) findViewById(R.id.imageview_description);

		// Extract currentQuestId from Intent.
		Bundle b = getIntent().getExtras();
		quest_id = b.getInt("currentQuestId");

		// Load quest from database.
		quest = dbAdapter.getQuest(quest_id);

		// Set the text.
		descriptionTextView.setText(quest.getQuestDescription());
		descriptionTextView.setTypeface(typeFace);

		// Set the image.
		if (!(quest.getImageId() == 0)) {
			Bitmap bmp = dbAdapter.getImage(quest.getImageId());

			descriptionImageView.setImageBitmap(bmp);
			// iv.setAdjustViewBounds(true);
			// iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		}

		// Set the title.
		// Custom Title.
		SpannableString s = new SpannableString(quest.getQuestName());
		s.setSpan(new TypefaceSpan(DescriptionActivity.this,
				"Caudex-Italic.ttf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new ForegroundColorSpan(Color.argb(255, 255, 178, 102)), 0,
				s.length(), 0);
		setTitle(s);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.description, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		// If the user clicked on map menu button.
		if (id == R.id.open_map) {
			Intent i = new Intent(DescriptionActivity.this, MapActivity.class);
			i.putExtra("currentQuestId", quest_id);
			startActivity(i);
		} else if (id == R.id.exit_activity) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

}
