package learningmycity.ui;

import learningmycity.content.MultipleChoice;
import learningmycity.content.Quest;
import learningmycity.content.Task;
import android.app.ActionBar.LayoutParams;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.tsivas061.learningmycity.R;

/**
 * Implementation of TaskUI for the Multiple Choice task type.
 */
public class MultipleChoiceTaskUI extends TaskUI {

	// The active radio button.
	private RadioButton activeRadioButton;

	// The Typeface for importing custom fonts.
	private Typeface typeFace;

	/**
	 * Creates a new instance of the UI handler.
	 */
	public MultipleChoiceTaskUI(Task task, int[] taskIds, Quest quest,
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
		RadioGroup rg = new RadioGroup(activity);
		rg.setOrientation(RadioGroup.VERTICAL);

		for (int i = 0; i < ((MultipleChoice) task).getAlternatives().length; i++) {

			RadioButton radioButton = new RadioButton(activity);
			radioButton.setText(((MultipleChoice) task).getAlternatives()[i]);
			radioButton.setTypeface(typeFace);
			radioButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					activeRadioButton = (RadioButton) v;
				}
			});
			rg.addView(radioButton);

			// Toggles the first button of the radiogroup by default.
			if (i == 0) {
				activeRadioButton = radioButton;
				radioButton.toggle();
			}
		}

		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		rg.setLayoutParams(params);
		mView.addView(rg);

	}

	@Override
	/**
	 * Returns the answer given by the user.
	 */
	public Object getGivenAnswer() {
		return activeRadioButton.getText();
	}

}
