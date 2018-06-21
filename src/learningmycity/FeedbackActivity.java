package learningmycity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tsivas061.learningmycity.R;

/**
 * The Feedback activity of the application.
 */
public class FeedbackActivity extends ActionBarActivity {

	private static final String TAG = "FeedbackActivity";

	// The EditText for the name.
	EditText name;

	// The EditText for the subject.
	EditText subject;

	// The EditText for the message.
	EditText message;

	// The Button for sending the mail.
	Button sendMail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback);

		sendMail = (Button) findViewById(R.id.button_send_feedback);

		sendMail.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				name = (EditText) findViewById(R.id.name);
				subject = (EditText) findViewById(R.id.subject);
				message = (EditText) findViewById(R.id.message);

				// Intent for the Mail.
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri
						.fromParts("mailto", "tsivas061@gmail.com", null));
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject.getText()
						.toString());
				emailIntent.putExtra(Intent.EXTRA_TEXT, "From "
						+ name.getText().toString() + "\n"
						+ message.getText().toString());
				try {
					startActivity(Intent.createChooser(emailIntent,
							"Send mail..."));
				} catch (android.content.ActivityNotFoundException ex) {
					Toast.makeText(FeedbackActivity.this,
							"There are no email clients installed.",
							Toast.LENGTH_SHORT).show();
					Log.e(TAG, "There are no email clients installed.");
				}

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.feedback, menu);
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
}
