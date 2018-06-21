package learningmycity.content;

import java.util.Locale;

import learningmycity.db.DbAdapter;

/**
 * An Open task in the game.
 */
public class Open extends Task {

	/**
	 * Constructs a new Open task.
	 */
	public Open(int taskId, String description, Question question, int penalty,
			int imageId, Hint[] hints, boolean completed, int wrongAnswers,
			int questId, DbAdapter dbAdapter) {
		super(taskId, Task.OPEN_TASK, description, question, penalty, imageId,
				hints, completed, wrongAnswers, questId, dbAdapter);

	}

	/**
	 * Validates the given answer by the user against the real answer stored in
	 * the task.
	 */
	@Override
	public boolean validateTask(Object givenAnswer) {
		String solution = question.getSolution();
		solution = solution.toLowerCase(Locale.getDefault());
		solution = solution.replace(" ", "");
		solution = solution.replace(".", "");
		solution = solution.replace(",", "");

		String answer = givenAnswer.toString();
		answer = answer.toLowerCase(Locale.getDefault());
		answer = answer.replace(" ", "");
		answer = answer.replace(".", "");
		answer = answer.replace(",", "");

		return solution.equals(answer);
	}

}
