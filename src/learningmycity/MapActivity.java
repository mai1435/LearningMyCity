package learningmycity;

import learningmycity.content.Quest;
import learningmycity.db.DbAdapter;
import learningmycity.util.TypefaceSpan;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tsivas061.learningmycity.R;

/**
 * The map activity of the application.
 */
public class MapActivity extends ActionBarActivity {

	// The Google Map.
	private GoogleMap mMap;

	// The quest the user has chosen.
	private Quest quest;

	// The quest_id.
	private int quest_id;

	// The dbAdapter for connecting to the database.
	private DbAdapter dbAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		// Create a new adapter
		dbAdapter = new DbAdapter(this);

		// Create a new map object.
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		// Extract currentQuestId from Intent.
		Bundle b = getIntent().getExtras();
		quest_id = b.getInt("currentQuestId");

		// Load quest from database.
		quest = dbAdapter.getQuest(quest_id);

		// Set the title.
		// Custom Title.
		SpannableString s = new SpannableString(getString(R.string.title_map));
		s.setSpan(new TypefaceSpan(MapActivity.this, "Caudex-Italic.ttf"), 0,
				s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new ForegroundColorSpan(Color.argb(255, 255, 211, 155)), 0,
				s.length(), 0);
		setTitle(s);

		// Show the map on the screen.
		setUpMapIfNeeded();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
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
	 * Show the map on the screen.
	 */
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			mMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();

			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				// The Map is verified. It is now safe to manipulate the map.
				mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

				mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(quest
						.getLatitude(), quest.getLongitude())));

				mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);

				mMap.addMarker(new MarkerOptions()
						// Set the Marker's position
						.position(
								new LatLng(quest.getLatitude(), quest
										.getLongitude()))
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.ic_action_map_spot))
						// Set the title of the Marker's information window
						.title(quest.getQuestName()));
			}
		} else {
			// The Map is verified. It is now safe to manipulate the map.
			mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

			mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(quest
					.getLatitude(), quest.getLongitude())));

			mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);

			mMap.addMarker(new MarkerOptions()
					// Set the Marker's position
					.position(
							new LatLng(quest.getLatitude(), quest
									.getLongitude()))
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.ic_action_map_spot))
					// Set the title of the Marker's information window
					.title(quest.getQuestName()));
		}
	}
}
