package learningmycity.content;

/**
 * A hint in the game.
 */
public class Hint {

	// The id of the hint.
	private int id;

	// The description of the hint.
	private String description;

	// A boolean denoting if the hint was used.
	private boolean used;

	// The penalty the user will get using this hint
	private int penalty;

	// The image id of the hint.
	private int imageId;

	// The task id this hint belongs to.
	private int taskId;

	public Hint(int id, String description, int penalty, boolean used,
			int imageId, int taskId) {
		this.id = id;
		this.description = description;
		this.used = used;
		this.penalty = penalty;
		this.imageId = imageId;
		this.taskId = taskId;
	}

	/**
	 * Returns the id of the task.
	 */
	public int getTaskId() {
		return taskId;
	}

	/**
	 * Returns the description of the hint.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns true if the hint was used, false otherwise.
	 */
	public boolean isUsed() {
		return used;
	}

	/**
	 * Returns the penalty for using this hint.
	 */
	public int getPenalty() {
		return penalty;
	}

	/**
	 * Returns the id of the image.
	 */
	public int getImageId() {
		return imageId;
	}

	/**
	 * Returns the id of the hint.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Set to true if the hint was used.
	 */
	public void setUsed() {
		used = true;
	}
}
