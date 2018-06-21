package learningmycity.content;

import learningmycity.db.DbAdapter;

/**
 * A Quest of the game.
 */
public class Quest {

	// The database questId of this quest.
	private int questId;

	// The database taskIds of all the tasks for this quest.
	private int[] taskIds;

	// The name of this quest.
	private String questName;

	// The database imageId for this quest, 0 if the task does not have an
	// image.
	protected int imageId;

	// The description of this quest.
	private String questDescription;

	// A boolean which denotes if the quest is completed by the user or not.
	protected boolean questCompleted;

	// The longitude of the quest
	private Double longitude;

	// The latitude of the quest
	private Double latitude;

	// The database pathId for this task.
	protected int pathId;

	// A dbAdapter for connecting to the database.
	protected DbAdapter dbAdapter;

	/**
	 * Creates a new quest according to the game state table in the database.
	 */
	public Quest(int questId, String questName, int imageId,
			String questDescription, boolean questCompleted, double latitude,
			double longitude, int pathId, DbAdapter dbAdapter) {
		this.questId = questId;
		this.dbAdapter = dbAdapter;
		this.questName = questName;
		this.imageId = imageId;
		this.questDescription = questDescription;
		this.questCompleted = questCompleted;
		this.longitude = longitude;
		this.latitude = latitude;
		this.pathId = pathId;

		dbAdapter.open();
		taskIds = dbAdapter.getTaskIds(questId);
		dbAdapter.close();
	}

	/**
	 * Returns the questId for this quest.
	 */
	public int getQuestId() {
		return questId;
	}

	/**
	 * Returns the name of the quest.
	 */
	public String getQuestName() {
		return questName;
	}

	/**
	 * Returns the R.java-reference of the image of the quest.
	 */
	public int getImageId() {
		return imageId;
	}

	/**
	 * Returns the description of the quest.
	 */
	public String getQuestDescription() {
		return questDescription;
	}

	/**
	 * Set the boolean which denotes if the quest is completed or not.
	 */
	public void setCompleted(boolean questCompleted) {
		this.questCompleted = questCompleted;
		dbAdapter.open();
		dbAdapter.updateQuest(questId, questCompleted);
		dbAdapter.close();
	}

	/**
	 * Returns true if the quest is completed, false if not.
	 */
	public boolean getCompleted() {
		return questCompleted;
	}

	/**
	 * Returns the longitude of the quest.
	 */
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * Returns the latitude of the quest.
	 */
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * Returns the next taskIds for this quest.
	 */
	public int getNextTaskId(int currentTaskId) {
		if (currentTaskId == 0)
			return taskIds[0];

		for (int i = 0; i < taskIds.length; i++) {
			if (taskIds[i] == currentTaskId && i + 1 < taskIds.length)
				return taskIds[i + 1];
		}

		return -1;
	}

	/**
	 * Returns the first task in the quest.
	 */
	public int getFirstTaskId() {
		return taskIds[0];
	}
}
