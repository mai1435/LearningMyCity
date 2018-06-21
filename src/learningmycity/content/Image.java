package learningmycity.content;

/**
 * An image in the game.
 */
public class Image {

	// The id of the image.
	private int id;

	// The name of the image.
	private String imageName;

	// The string of the image.
	private String imageString;

	public Image(int id, String imageName, String imageString) {
		this.id = id;
		this.imageName = imageName;
		this.imageString = imageString;

	}

	/**
	 * Returns the id of the task.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the name of the task.
	 */
	public String getImageName() {
		return imageName;
	}

	/**
	 * Returns the string of the task.
	 */
	public String getImageString() {
		return imageString;
	}

}
