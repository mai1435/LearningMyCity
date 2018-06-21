package learningmycity.content;

import learningmycity.db.DbAdapter;

/**
 * The path of the game.
 */
public class Path {

	// The database pathId of this path.
	private int pathId;

	// The name of this path.
	private String pathName;

	// The description of this path.
	private String pathDescription;

	// The database questIds of all the quests for this path.
	private int[] questIds;

	/**
	 * Creates a new path according to the game state table in the database.
	 */
	public Path(DbAdapter dbAdapter, int pathId, String pathName,
			String pathDescription) {
		this.pathId = pathId;
		this.pathName = pathName;
		this.pathDescription = pathDescription;

		dbAdapter.open();
		questIds = dbAdapter.getQuestIds(pathId);
		dbAdapter.close();
	}

	/**
	 * Returns the pathId for this path.
	 */
	public int getPathId() {
		return pathId;
	}

	/**
	 * Returns the pathName for this path.
	 */
	public String getPathName() {
		return pathName;
	}

	/**
	 * Returns the pathDescription for this path.
	 */
	public String getPathDescription() {
		return pathDescription;
	}

	/**
	 * Returns the next questId for this path.
	 */
	public int getNextQuestId(int currentQuestId) {
		if (currentQuestId == 0)
			return questIds[0];

		for (int i = 0; i < questIds.length; i++) {
			if (questIds[i] == currentQuestId && i + 1 < questIds.length)
				return questIds[i + 1];
		}

		return -1;
	}

	/**
	 * Returns the first quest in the path.
	 */
	public int getFirstQuestId() {
		return questIds[0];
	}

	/**
	 * Method used by the List Adapter.
	 */
	public boolean intersects(String pathName) {
		return this.pathName.equals(pathName);
	}
}
