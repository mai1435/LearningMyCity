package learningmycity.content;

/**
 * A Question in the game.
 */
public class Question {

	// The id of the question.
	private int id;

	// The id of the task presenting this question.
	private int taskId;

	// The text of the question.
	private String question;

	// The answer to the question.
	private String solution;

	public Question(int id, String question, String solution, int taskId) {
		this.id = id;
		this.question = question;
		this.solution = solution;
		this.taskId = taskId;
	}

	/**
	 * Returns the id of the question.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the id of the task.
	 */
	public int getTaskId() {
		return taskId;
	}

	/**
	 * Returns the question.
	 */
	public String getQuestion() {
		return question;
	}

	/**
	 * Returns the correct answer.
	 */
	public String getSolution() {
		return solution;
	}

}
