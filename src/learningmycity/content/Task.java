package learningmycity.content;

import learningmycity.db.DbAdapter;

/**
 * A Task in the game.
 */
public abstract class Task {

	// Denotes that the task is a multiple choice question.
	public static final int MULTIPLE_CHOICE_TASK = 1;

	// Denotes that the task is an open answer task.
	public static final int OPEN_TASK = 2;

	// Denotes that the task is a GPS task.
	public static final int GPS_TASK = 3;

	// Denotes that the task is a checkbox task.
	public static final int CHECKBOX_TASK = 4;

	// Denotes that the task is a Google Googles task.
	public static final int GOOGLE_GOOGLES_TASK = 5;

	// The id of the task.
	protected int taskId;

	// The description of the task.
	protected String description;

	// A boolean which denotes if the task is completed by the user or not.
	protected boolean completed;

	// The database imageId for this task, 0 if the task does not have an image.
	protected int imageId;

	// A constant denoting which type of task this task is.
	protected int taskType;

	// The time penalty in minutes for wrong answers.
	protected int penalty;

	// The number of times the user has provided a wrong answer to the task.
	protected int wrongAnswers = 0;

	// The database questId for this task.
	protected int questId;

	// The question for this task.
	protected Question question;

	// The list of hints belonging to this task.
	protected Hint[] hints;

	// A dbAdapter for connecting to the database.
	protected DbAdapter dbAdapter;

	/**
	 * Constructs a new task.
	 */
	public Task(int taskId, int taskType, String description,
			Question question, int penalty, int imageId, Hint[] hints,
			boolean completed, int wrongAnswers, int questId,
			DbAdapter dbAdapter) {
		this.taskId = taskId;
		this.taskType = taskType;
		this.description = description;
		this.question = question;
		this.penalty = penalty;
		this.imageId = imageId;
		this.hints = hints;
		this.completed = completed;
		this.wrongAnswers = wrongAnswers;
		this.questId = questId;
		this.dbAdapter = dbAdapter;
	}

	/**
	 * Returns the id of the task.
	 */
	public int getId() {
		return taskId;
	}

	/**
	 * Returns a constant denoting which type of task this task is.
	 */
	public int getTaskType() {
		return taskType;
	}

	/**
	 * Returns the description of the task.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the time penalty for wrong answers.
	 */
	public int getPenalty() {
		return penalty;
	}

	/**
	 * Gets the questions for this task.
	 */
	public Question getQuestion() {
		return question;
	}

	/**
	 * Returns the number of times the user has provided a wrong answer to the
	 * task.
	 */
	public int getWrongAnswers() {
		return wrongAnswers;
	}

	/**
	 * Increases the number of answers with one each time the user answers
	 * wrong.
	 */
	public void incWrongAnswers() {
		wrongAnswers++;
		dbAdapter.open();
		dbAdapter.updateTask(taskId, wrongAnswers, completed);
		dbAdapter.close();
	}

	/**
	 * Returns the R.java-reference of the image of the task.
	 */
	public int getImageId() {
		return imageId;
	}

	/**
	 * Set the boolean which denotes if the task is completed or not.
	 */
	public void setCompleted(boolean completed) {
		this.completed = completed;
		dbAdapter.open();
		dbAdapter.updateTask(taskId, wrongAnswers, completed);
		dbAdapter.close();
	}

	/**
	 * Returns true if the task is completed, false if not.
	 */
	public boolean getCompleted() {
		return completed;
	}

	/**
	 * Gets the remaining number of unused hints.
	 */
	public int getRemainingHints() {
		int counter = 0;
		for (int i = 0; i < hints.length; i++) {
			if (!hints[i].isUsed())
				counter++;
		}
		return counter;
	}

	/**
	 * Retrieves the next unused hint.
	 */
	public Hint getNextUnusedHint() {
		if (hints == null) {
			return null;
		}
		for (int i = 0; i < hints.length; i++) {
			if (!hints[i].isUsed())
				return hints[i];
		}
		return null;
	}

	/**
	 * Retrieves hints that have been used for this task.
	 */
	public Hint[] getUsedHints() {
		if (hints == null)
			return null;
		int counter = 0;
		for (int i = 0; i < hints.length; i++) {
			if (hints[i].isUsed())
				counter++;
			else
				break;
		}
		Hint[] result = new Hint[counter];
		for (int i = 0; i < counter; i++)
			result[i] = hints[i];
		return result;
	}

	/**
	 * Sets the used state for the hint in the application and database to true.
	 */
	public void setHintUsed(Hint hint) {
		dbAdapter.open();
		for (int i = 0; i < hints.length; i++) {
			if (hints[i].getId() == hint.getId()) {
				hints[i].setUsed();

				dbAdapter.setHintUsed(hint.getId());
				break;
			}
		}
		dbAdapter.close();
	}

	/**
	 * Validates the given answer by the user against the real answer stored in
	 * the task.
	 */
	public abstract boolean validateTask(Object givenAnswer);

	/**
	 * Checks whether or not the task has hints.
	 */
	public boolean hasHints() {
		if (hints == null || hints.length == 0)
			return false;
		return true;
	}

}
