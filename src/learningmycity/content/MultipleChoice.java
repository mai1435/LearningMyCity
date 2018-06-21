package learningmycity.content;

import learningmycity.db.DbAdapter;

/**
 * A MultipleChoice task in the game.
 */
public class MultipleChoice extends Task {

	// The alternatives for this task.
	private String[] alternatives;

	/**
	 * Constructs a new Open task.
	 */
	public MultipleChoice(int taskId, String description, Question question,
			int penalty, int imageId, Hint[] hints, boolean completed,
			int wrongAnswers, int questId, String[] alternatives,
			DbAdapter dbAdapter) {
		super(taskId, Task.MULTIPLE_CHOICE_TASK, description, question,
				penalty, imageId, hints, completed, wrongAnswers, questId,
				dbAdapter);

		this.alternatives = alternatives;
	}

	/**
	 * Returns a String array containing the alternatives for this task.
	 */
	public String[] getAlternatives() {
		return alternatives;
	}

	/**
	 * Validates the given answer by the user against the real answer stored in
	 * the task.
	 */
	@Override
	public boolean validateTask(Object givenAnswer) {
		return question.getSolution().equals(givenAnswer.toString());
	}

}
