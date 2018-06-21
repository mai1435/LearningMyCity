package learningmycity.ui;

import learningmycity.content.Quest;
import learningmycity.content.Task;
import android.app.ActionBar.LayoutParams;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.tsivas061.learningmycity.R;

/**
 * Implementation of TaskUI for the Open task type.
 */
public class OpenTaskUI extends TaskUI {

	// The input field the user can type the answer into.
	private EditText inputText;

	// The Typeface for importing custom fonts.
	private Typeface typeFace;

	/**
	 * Creates a new instance of the UI handler.
	 */
	public OpenTaskUI(Task task, int[] taskIds, Quest quest,
			ActionBarActivity activity) {
		super(task, taskIds, quest, activity);
	}

	/**
	 * Creates and returns the UI of the task.
	 */
	public void createView() {

		createTitle();
		buildDescription();
		buildImage();
		buildQuestion();
		setListeners();

		// Import external font style.
		typeFace = Typeface.createFromAsset(activity.getAssets(),
				"fonts/Caudex-Italic.ttf");

		LinearLayout mView = (LinearLayout) activity
				.findViewById(R.id.layout_custom);
		inputText = new EditText(activity);
		inputText.setTypeface(typeFace);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		inputText.setLayoutParams(params);
		inputText.setHint(R.string.edittext_task_answerfield_hint);
		inputText.setLines(2);
		mView.addView(inputText);

	}

	/**
	 * Returns the answer given by the user.
	 */
	public Object getGivenAnswer() {
		return inputText.getText();
	}
}
