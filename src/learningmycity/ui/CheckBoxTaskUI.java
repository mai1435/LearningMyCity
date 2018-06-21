package learningmycity.ui;

import learningmycity.content.Quest;
import learningmycity.content.Task;
import android.app.ActionBar.LayoutParams;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.tsivas061.learningmycity.R;

public class CheckBoxTaskUI extends TaskUI {

	// the array containing the checkboxes representing alternatives
	private CheckBox[] checkboxArray;

	// The Typeface for importing custom fonts.
	private Typeface typeFace;

	/**
	 * Creates a new instance of the UI handler.
	 */
	public CheckBoxTaskUI(Task task, int[] taskIds, Quest quest,
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
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		checkboxArray = new CheckBox[((learningmycity.content.CheckBox) task)
				.getAlternatives().length];

		for (int i = 0; i < ((learningmycity.content.CheckBox) task)
				.getAlternatives().length; i++) {

			CheckBox cb = new CheckBox(activity);
			cb.setText(((learningmycity.content.CheckBox) task)
					.getAlternatives()[i]);
			cb.setTypeface(typeFace);
			checkboxArray[i] = cb;
			cb.setLayoutParams(params);
			mView.addView(cb);
		}
	}

	@Override
	/**
	 * Returns the answer given by the user.
	 */
	public Object getGivenAnswer() {
		String answers = "";
		for (int i = 0; i < checkboxArray.length; i++) {
			if (checkboxArray[i].isChecked())
				answers = answers + (String) checkboxArray[i].getText();
		}

		return answers;
	}
}
