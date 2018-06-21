package learningmycity.content;

/**
 * A score in the game.
 */
public class Score implements Comparable<Score> {

	private String pathId;
	private String userName;
	private String score;
	private String date;

	public Score(String pathId, String userName, String score, String date) {
		this.pathId = pathId;
		this.userName = userName;
		this.score = score;
		this.date = date;
	}

	public String getPathId() {
		return pathId;
	}

	public String getUserName() {
		return userName;
	}

	public String getScore() {
		return score;
	}

	public void setPathId(String pathId) {
		this.pathId = pathId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * compareTo method sorts an ArrayList<Score>.
	 */	
	@Override
	public int compareTo(Score comparescore) {
		int score = Integer.parseInt(((Score) comparescore).getScore());
		/* For Ascending order */
		return score - Integer.parseInt(this.score);

		/* For Descending order do like this */
		//return Integer.parseInt(this.score) - score;
	}

}
