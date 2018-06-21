package learningmycity.db;

import java.util.ArrayList;

import learningmycity.content.CheckBox;
import learningmycity.content.GPS;
import learningmycity.content.Hint;
import learningmycity.content.Image;
import learningmycity.content.MultipleChoice;
import learningmycity.content.Open;
import learningmycity.content.Path;
import learningmycity.content.Quest;
import learningmycity.content.Question;
import learningmycity.content.Task;
import learningmycity.util.GPSHandler;
import learningmycity.util.JSONResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.tsivas061.learningmycity.R;

/**
 * An adapter for communicating with the application's database.
 */
public class DbAdapter {

	private static final String TAG = "DbAdapter";

	// The current activity.
	private Context mApplicationContext;

	// The database connection helper.
	private DbHelper dbHelper;

	// The database object to run queries on.
	private SQLiteDatabase db = null;

	/**
	 * Constructs a new database adapter.
	 */
	public DbAdapter(Context mApplicationContext) {
		this.mApplicationContext = mApplicationContext;
		dbHelper = new DbHelper(mApplicationContext);
	}

	/**
	 * Opens the database and connects to it.
	 */
	public DbAdapter open() throws SQLException {
		db = dbHelper.getWritableDatabase();
		return this;
	}

	/**
	 * Closes the connection to the database.
	 */
	public void close() {
		dbHelper.close();
	}

	/**
	 * Resets the database.
	 */
	public void resetDatabase() {
		open();
		dbHelper.resetDatabase(db);
		close();
	}

	/**
	 * Fills the database with content.
	 */
	public void fillDatabase(int pathId, JSONResponseHandler obj)
			throws JSONException {

		open();

		// Import the Path.
		importPath(obj);

		// Import the Quests.
		importQuests(obj);

		// Import the Tasks.
		importTasks(obj);

		// Import the Images.
		importImages(obj);

		// Import the Questions.
		importQuestions(obj);

		// Import the Alternatives.
		importAlternatives(obj);

		// Import the Hints.
		importHints(obj);

		close();
	}

	/**
	 * Extract the Path from the JSON Object and import to database.
	 */
	public void importPath(JSONResponseHandler obj) throws JSONException {
		Log.i(TAG, "importPath");

		JSONObject path = obj.getPath();

		dbHelper.createPath(db, path.getInt("pathId"),
				path.getString("pathName"), path.getString("pathDescription"));
	}

	/**
	 * Extract the Quests from the JSON Object and import to database.
	 */
	public void importQuests(JSONResponseHandler obj) throws JSONException {
		Log.i(TAG, "importQuests");

		JSONArray quests = obj.getQuests();

		for (int i = 0; i < quests.length(); i++) {
			JSONObject quest = quests.getJSONObject(i);
			dbHelper.createQuest(db, quest.getInt("questId"),
					quest.getString("questName"), quest.getInt("questImageID"),
					quest.getString("questDescription"),
					quest.getDouble("latitude"), quest.getDouble("longitude"),
					quest.getInt("pathId"), quest.getInt("questCompleted"));
		}
	}

	/**
	 * Extract the Tasks from the JSON Object and import to database.
	 */
	public void importTasks(JSONResponseHandler obj) throws JSONException {
		Log.i(TAG, "importTasks");

		JSONArray tasks = obj.getTasks();

		for (int i = 0; i < tasks.length(); i++) {
			JSONObject task = tasks.getJSONObject(i);
			dbHelper.createTask(db, task.getString("taskDescription"),
					task.getInt("taskType"), task.getInt("taskCompleted"),
					task.getInt("taskImageID"), task.getInt("taskPenalty"),
					task.getInt("taskWrongAnswers"), task.getInt("questId"),
					task.getDouble("latitude"), task.getDouble("longitude"),
					task.getDouble("radius"));
		}
	}

	/**
	 * Extract the Images from the JSON Object and import to database.
	 */
	public void importImages(JSONResponseHandler obj) throws JSONException {
		Log.i(TAG, "importImages");

		ArrayList<Image> images = obj.getImages();

		for (int i = 0; i < images.size(); i++) {
			dbHelper.createImage(db, images.get(i).getId(), images.get(i)
					.getImageName(), images.get(i).getImageString());
		}
	}

	/**
	 * Extract the Questions from the JSON Object and import to database.
	 */
	public void importQuestions(JSONResponseHandler obj) throws JSONException {
		Log.i(TAG, "importQuestions");

		JSONArray questions = obj.getQuestions();

		for (int i = 0; i < questions.length(); i++) {
			JSONObject question = questions.getJSONObject(i);
			dbHelper.addQuestion(db, question.getInt("taskId"),
					question.getString("questionQuestion"),
					question.getString("questionSolution"));
		}
	}

	/**
	 * Extract the Alternatives from the JSON Object and import to database.
	 */
	public void importAlternatives(JSONResponseHandler obj)
			throws JSONException {
		Log.i(TAG, "importAlternatives");

		JSONArray alternatives = obj.getAlternatives();

		for (int i = 0; i < alternatives.length(); i++) {
			JSONObject alternative = alternatives.getJSONObject(i);
			dbHelper.addAlternative(db, alternative.getInt("taskId"),
					alternative.getString("alternativeAlternative"));
		}
	}

	/**
	 * Extract the Hints from the JSON Object and import to database.
	 */
	public void importHints(JSONResponseHandler obj) throws JSONException {
		Log.i(TAG, "importHints");

		JSONArray hints = obj.getHints();

		for (int i = 0; i < hints.length(); i++) {
			JSONObject hint = hints.getJSONObject(i);
			dbHelper.addHint(db, hint.getInt("taskId"),
					hint.getString("hintDescription"),
					hint.getInt("hintImageId"), hint.getInt("hintPenalty"));
		}
	}

	/**
	 * Retrieves the path with the given id from the database.
	 */
	public Path getPath(int pathId) throws SQLException {
		Log.i(TAG, "getPath");

		open();
		String[] rowNames = { mApplicationContext.getString(R.string.dbPathId),
				mApplicationContext.getString(R.string.dbPathName),
				mApplicationContext.getString(R.string.dbPathDescription) };

		Cursor cursor = db
				.query(true, mApplicationContext.getString(R.string.dbPath),
						rowNames,
						mApplicationContext.getString(R.string.dbPathId) + "="
								+ pathId, null, null, null, null, null);
		cursor.moveToFirst();

		if (cursor.getCount() == 0)
			return null;

		String pathName = cursor.getString(cursor
				.getColumnIndexOrThrow(mApplicationContext
						.getString(R.string.dbPathName)));
		String pathDescription = cursor.getString(cursor
				.getColumnIndexOrThrow(mApplicationContext
						.getString(R.string.dbPathDescription)));

		Path path = new Path(this, pathId, pathName, pathDescription);

		cursor.close();
		close();

		return path;
	}

	/**
	 * Retrieves the quest with the given id from the database.
	 */
	public Quest getQuest(int questId) throws SQLException {
		Log.i(TAG, "getQuest");

		open();
		String[] rowNames = {
				mApplicationContext.getString(R.string.dbQuestId),
				mApplicationContext.getString(R.string.dbQuestName),
				mApplicationContext.getString(R.string.dbQuestImage),
				mApplicationContext.getString(R.string.dbQuestDescription),
				mApplicationContext.getString(R.string.dbQuestCompleted),
				mApplicationContext.getString(R.string.dbQuestLatitude),
				mApplicationContext.getString(R.string.dbQuestLongitude),
				mApplicationContext.getString(R.string.dbQuestPathId) };

		Cursor cursor = db.query(true,
				mApplicationContext.getString(R.string.dbQuest), rowNames,
				mApplicationContext.getString(R.string.dbQuestId) + "="
						+ questId, null, null, null, null, null);
		cursor.moveToFirst();

		if (cursor.getCount() == 0)
			return null;

		String questName = cursor.getString(cursor
				.getColumnIndexOrThrow(mApplicationContext
						.getString(R.string.dbQuestName)));
		int imageId = cursor.getInt(cursor
				.getColumnIndexOrThrow(mApplicationContext
						.getString(R.string.dbQuestImage)));
		String questDescription = cursor.getString(cursor
				.getColumnIndexOrThrow(mApplicationContext
						.getString(R.string.dbQuestDescription)));
		boolean questCompleted = cursor.getInt(cursor
				.getColumnIndexOrThrow(mApplicationContext
						.getString(R.string.dbQuestName))) > 0;
		String latitude = cursor.getString(cursor
				.getColumnIndexOrThrow(mApplicationContext
						.getString(R.string.dbQuestLatitude)));
		String longitude = cursor.getString(cursor
				.getColumnIndexOrThrow(mApplicationContext
						.getString(R.string.dbQuestLongitude)));
		int questPathId = cursor.getInt(cursor
				.getColumnIndexOrThrow(mApplicationContext
						.getString(R.string.dbQuestPathId)));

		Quest quest = new Quest(questId, questName, imageId, questDescription,
				questCompleted, Double.parseDouble(latitude),
				Double.parseDouble(longitude), questPathId, this);

		cursor.close();
		close();

		return quest;
	}

	/**
	 * Retrieves the task with the given id from the database.
	 */
	public Task getTask(int taskId) throws SQLException {
		Log.i(TAG, "getTask");

		open();
		String[] rowNames = { mApplicationContext.getString(R.string.dbTaskId),
				mApplicationContext.getString(R.string.dbTaskDescription),
				mApplicationContext.getString(R.string.dbTaskCompleted),
				mApplicationContext.getString(R.string.dbTaskImage),
				mApplicationContext.getString(R.string.dbTaskType),
				mApplicationContext.getString(R.string.dbTaskPenalty),
				mApplicationContext.getString(R.string.dbTaskWrongAnswers),
				mApplicationContext.getString(R.string.dbTaskQuestId) };

		Cursor cursor = db
				.query(true, mApplicationContext.getString(R.string.dbTask),
						rowNames,
						mApplicationContext.getString(R.string.dbTaskId) + "="
								+ taskId, null, null, null, null, null);
		cursor.moveToFirst();

		// returns null if the task has a id that is not of any task in the
		// database
		// this basicly means that the game is finished
		if (cursor.getCount() == 0)
			return null;

		// The fields retrieved from the database
		String description = cursor.getString(cursor
				.getColumnIndexOrThrow(mApplicationContext
						.getString(R.string.dbTaskDescription)));
		boolean completed = cursor.getInt(cursor
				.getColumnIndexOrThrow(mApplicationContext
						.getString(R.string.dbTaskCompleted))) > 0;
		int imageId = cursor.getInt(cursor
				.getColumnIndexOrThrow(mApplicationContext
						.getString(R.string.dbTaskImage)));
		int type = cursor.getInt(cursor
				.getColumnIndexOrThrow(mApplicationContext
						.getString(R.string.dbTaskType)));
		int penalty = cursor.getInt(cursor
				.getColumnIndexOrThrow(mApplicationContext
						.getString(R.string.dbTaskPenalty)));
		int wrongAnswers = cursor.getInt(cursor
				.getColumnIndexOrThrow(mApplicationContext
						.getString(R.string.dbTaskWrongAnswers)));
		int questId = cursor.getInt(cursor
				.getColumnIndexOrThrow(mApplicationContext
						.getString(R.string.dbTaskQuestId)));

		Task task = null;

		switch (type) {

		case Task.OPEN_TASK:
			task = new Open(taskId, description, getQuestion(taskId), penalty,
					imageId, getHints(taskId), completed, wrongAnswers,
					questId, this);
			break;
		case Task.MULTIPLE_CHOICE_TASK:
			task = new MultipleChoice(taskId, description, getQuestion(taskId),
					penalty, imageId, getHints(taskId), completed,
					wrongAnswers, questId, getAlternatives(taskId), this);
			break;
		case Task.CHECKBOX_TASK:
			task = new CheckBox(taskId, description, getQuestion(taskId),
					penalty, imageId, getHints(taskId), completed,
					wrongAnswers, questId, getAlternatives(taskId), this);
			break;
		case Task.GPS_TASK:
			String[] rowNamesGPS = {
					mApplicationContext.getString(R.string.dbGPSLatitude),
					mApplicationContext.getString(R.string.dbGPSLongitude),
					mApplicationContext.getString(R.string.dbGPSRadius) };

			cursor = db.query(true,
					mApplicationContext.getString(R.string.dbGPS), rowNamesGPS,
					mApplicationContext.getString(R.string.dbGPSQuestId) + "="
							+ taskId, null, null, null, null, null);

			cursor.moveToFirst();

			String longitude = cursor.getString(cursor
					.getColumnIndexOrThrow(mApplicationContext
							.getString(R.string.dbGPSLongitude)));
			String latitude = cursor.getString(cursor
					.getColumnIndexOrThrow(mApplicationContext
							.getString(R.string.dbGPSLatitude)));
			int radius = cursor.getInt(cursor
					.getColumnIndexOrThrow(mApplicationContext
							.getString(R.string.dbGPSRadius)));

			task = new GPS(taskId, description, getQuestion(taskId), penalty,
					imageId, getHints(taskId), completed, wrongAnswers,
					questId, this, new GPSHandler(mApplicationContext),
					Double.parseDouble(latitude),
					Double.parseDouble(longitude), radius);
			break;
		default:
			break;
		}

		cursor.close();
		close();

		return task;
	}

	/**
	 * Gets the question belonging to a task.
	 */
	public Question getQuestion(int taskId) {
		Log.i(TAG, "getQuestion");

		String[] rowNames = {
				mApplicationContext.getString(R.string.dbQuestionId),
				mApplicationContext.getString(R.string.dbQuestionQuestion),
				mApplicationContext.getString(R.string.dbQuestionSolution) };

		Cursor cursor = db.query(true,
				mApplicationContext.getString(R.string.dbQuestion), rowNames,
				mApplicationContext.getString(R.string.dbQuestionTaskId) + "="
						+ taskId, null, null, null,
				mApplicationContext.getString(R.string.dbQuestionId), null);

		cursor.moveToFirst();

		int questionId = cursor.getInt(cursor
				.getColumnIndexOrThrow(mApplicationContext
						.getString(R.string.dbQuestionId)));
		String question_question = cursor.getString(cursor
				.getColumnIndexOrThrow(mApplicationContext
						.getString(R.string.dbQuestionQuestion)));
		String solution = cursor.getString(cursor
				.getColumnIndexOrThrow(mApplicationContext
						.getString(R.string.dbQuestionSolution)));

		cursor.moveToNext();

		Question question = new Question(questionId, question_question,
				solution, taskId);

		cursor.close();
		return question;
	}

	/**
	 * Gets the answer alternatives for a task. Applicable for multiple choice
	 * and checkbox tasks.
	 */
	public String[] getAlternatives(int taskid) {
		Log.i(TAG, "getAlternatives");

		String[] rowNames = { mApplicationContext
				.getString(R.string.dbAlternativeAlternative) };

		Cursor cursor = db.query(true,
				mApplicationContext.getString(R.string.dbAlternative),
				rowNames,
				mApplicationContext.getString(R.string.dbAlternativeTaskId)
						+ "=" + taskid, null, null, null, null, null);

		cursor.moveToFirst();

		String[] alternatives = new String[cursor.getCount()];

		for (int i = 0; i < cursor.getCount(); i++) {
			alternatives[i] = cursor.getString(cursor
					.getColumnIndexOrThrow(mApplicationContext
							.getString(R.string.dbAlternativeAlternative)));
			cursor.moveToNext();
		}

		cursor.close();
		return alternatives;
	}

	/**
	 * Retrieves all hints belonging to a given task
	 */
	public Hint[] getHints(int taskId) {
		Log.i(TAG, "getHints");

		String[] rownames = new String[] {
				mApplicationContext.getString(R.string.dbHintId),
				mApplicationContext.getString(R.string.dbHintDescription),
				mApplicationContext.getString(R.string.dbHintTaskId),
				mApplicationContext.getString(R.string.dbHintPenalty),
				mApplicationContext.getString(R.string.dbHintUsed),
				mApplicationContext.getString(R.string.dbHintImage) };
		Cursor cursor = db.query(true,
				mApplicationContext.getString(R.string.dbHint), rownames,
				mApplicationContext.getString(R.string.dbHintTaskId) + "="
						+ taskId, null, null, null,
				mApplicationContext.getString(R.string.dbHintId), // order by
				null);

		Hint[] hints = new Hint[cursor.getCount()];
		if (cursor.getCount() != 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				hints[i] = new Hint(
						cursor.getInt(cursor
								.getColumnIndexOrThrow(mApplicationContext
										.getString(R.string.dbHintId))),
						cursor.getString(cursor
								.getColumnIndexOrThrow(mApplicationContext
										.getString(R.string.dbHintDescription))),
						cursor.getInt(cursor
								.getColumnIndexOrThrow(mApplicationContext
										.getString(R.string.dbHintPenalty))),
						cursor.getInt(cursor
								.getColumnIndexOrThrow(mApplicationContext
										.getString(R.string.dbHintUsed))) != 0,
						cursor.getInt(cursor
								.getColumnIndexOrThrow(mApplicationContext
										.getString(R.string.dbHintImage))),
						taskId);
				cursor.moveToNext();
			}
		}

		cursor.close();
		return hints;
	}

	/**
	 * Retrieves an image
	 */
	public Bitmap getImage(int imageId) {
		Log.i(TAG, "getImage");

		open();
		String[] rownames = new String[] {
				mApplicationContext.getString(R.string.dbImageId),
				mApplicationContext.getString(R.string.dbImageName),
				mApplicationContext.getString(R.string.dbImageString) };

		Cursor cursor = db.query(true,
				mApplicationContext.getString(R.string.dbImage), rownames,
				mApplicationContext.getString(R.string.dbImageId) + "="
						+ imageId, null, null, null, null, null);

		Bitmap decodedByte = null;
		if (cursor.getCount() != 0) {
			cursor.moveToFirst();
			String jsonString = cursor.getString(cursor
					.getColumnIndexOrThrow(mApplicationContext
							.getString(R.string.dbImageString)));

			// This Function converts the String back to Bitmap
			byte[] decodedString = Base64.decode(jsonString, Base64.DEFAULT);
			decodedByte = BitmapFactory.decodeByteArray(decodedString, 0,
					decodedString.length);

			cursor.moveToNext();
		}

		cursor.close();
		close();

		return decodedByte;
	}

	/**
	 * Retrieves the number of paths in Path table.
	 */
	public int getNumberOfPaths() throws SQLException {

		Cursor cursor = db.query(true,
				mApplicationContext.getString(R.string.dbPath), null, null,
				null, null, null, null, null);

		int count = cursor.getCount();
		cursor.close();
		return count;
	}

	/**
	 * Retrieves all the paths for the ListView.
	 */
	public Path[] getPaths() throws SQLException {
		Path[] paths = new Path[getNumberOfPaths()];

		String[] rowNames = { mApplicationContext.getString(R.string.dbPathId),
				mApplicationContext.getString(R.string.dbPathName),
				mApplicationContext.getString(R.string.dbPathDescription) };

		Cursor cursor = db.query(false,
				mApplicationContext.getString(R.string.dbPath), rowNames, null,
				null, null, null,
				mApplicationContext.getString(R.string.dbPathId), null);
		cursor.moveToFirst();

		for (int i = 0; i < cursor.getCount(); i++) {
			paths[i] = new Path(this, cursor.getInt(cursor
					.getColumnIndexOrThrow(mApplicationContext
							.getString(R.string.dbPathId))),
					cursor.getString(cursor
							.getColumnIndexOrThrow(mApplicationContext
									.getString(R.string.dbPathName))),
					cursor.getString(cursor
							.getColumnIndexOrThrow(mApplicationContext
									.getString(R.string.dbPathDescription))));
			cursor.moveToNext();
		}

		cursor.close();
		return paths;
	}

	/**
	 * Retrieves the number of quests in Quest table where questId = pathId.
	 */
	public int getNumberOfQuests(int pathId) throws SQLException {
		String[] rowNames = { mApplicationContext.getString(R.string.dbQuestId) };

		Cursor cursor = db.query(true,
				mApplicationContext.getString(R.string.dbQuest), rowNames,
				mApplicationContext.getString(R.string.dbQuestPathId) + " = "
						+ pathId, null, null, null, null, null);

		int count = cursor.getCount();
		cursor.close();
		return count;
	}

	/**
	 * Retrieves questIds belonging to a path from the database.
	 */
	public int[] getQuestIds(int pathId) throws SQLException {

		open();
		int[] questIds = new int[getNumberOfQuests(pathId)];

		String[] rowNames = { mApplicationContext.getString(R.string.dbQuestId) };

		Cursor cursor = db.query(false,
				mApplicationContext.getString(R.string.dbQuest), rowNames,
				mApplicationContext.getString(R.string.dbQuestPathId) + "="
						+ pathId, null, null, null,
				mApplicationContext.getString(R.string.dbQuestId), null);
		cursor.moveToFirst();

		for (int i = 0; i < cursor.getCount(); i++) {
			questIds[i] = (cursor.getInt(cursor
					.getColumnIndexOrThrow(mApplicationContext
							.getString(R.string.dbQuestId))));
			cursor.moveToNext();
		}

		cursor.close();
		close();

		return questIds;
	}

	/**
	 * Retrieves the number of tasks in Task table where taskId = questId.
	 */
	public int getNumberOfTasks(int questId) throws SQLException {
		String[] rowNames = { mApplicationContext.getString(R.string.dbTaskId) };

		Cursor cursor = db.query(true,
				mApplicationContext.getString(R.string.dbTask), rowNames,
				mApplicationContext.getString(R.string.dbTaskQuestId) + " = "
						+ questId, null, null, null, null, null);

		int count = cursor.getCount();
		cursor.close();
		return count;
	}

	/**
	 * Retrieves taskIds belonging to a quest from the database.
	 */
	public int[] getTaskIds(int questId) throws SQLException {

		open();
		int[] taskIds = new int[getNumberOfTasks(questId)];

		String[] rowNames = { mApplicationContext.getString(R.string.dbTaskId) };

		Cursor cursor = db.query(false,
				mApplicationContext.getString(R.string.dbTask), rowNames,
				mApplicationContext.getString(R.string.dbTaskQuestId) + "="
						+ questId, null, null, null,
				mApplicationContext.getString(R.string.dbTaskId), null);
		cursor.moveToFirst();

		for (int i = 0; i < cursor.getCount(); i++) {
			taskIds[i] = (cursor.getInt(cursor
					.getColumnIndexOrThrow(mApplicationContext
							.getString(R.string.dbTaskId))));
			cursor.moveToNext();
		}

		cursor.close();
		close();

		return taskIds;
	}

	/**
	 * Retrieves the current path from the GameState table.
	 */
	public int getGameStatePathId() throws SQLException {
		open();

		String[] rowNames = { mApplicationContext
				.getString(R.string.dbGameStatePathId) };
		Cursor cursor = db
				.query(true,
						mApplicationContext.getString(R.string.dbGameState),
						rowNames,
						mApplicationContext.getString(R.string.dbGameStateId)
								+ "=" + 1, null, null, null, null, null);
		cursor.moveToFirst();

		int pathId = cursor.getInt(cursor
				.getColumnIndexOrThrow(mApplicationContext
						.getString(R.string.dbGameStatePathId)));
		cursor.close();
		close();

		return pathId;
	}

	/**
	 * Retrieves the current quest from the GameState table.
	 */
	public int getGameStateQuestId() throws SQLException {
		open();

		String[] rowNames = { mApplicationContext
				.getString(R.string.dbGameStateQuestId) };
		Cursor cursor = db
				.query(true,
						mApplicationContext.getString(R.string.dbGameState),
						rowNames,
						mApplicationContext.getString(R.string.dbGameStateId)
								+ "=" + 1, null, null, null, null, null);
		cursor.moveToFirst();

		int questId = cursor.getInt(cursor
				.getColumnIndexOrThrow(mApplicationContext
						.getString(R.string.dbGameStateQuestId)));
		cursor.close();
		close();

		return questId;
	}

	/**
	 * Retrieves the current task from the GameState table.
	 */
	public int getGameStateTaskId() throws SQLException {
		open();

		String[] rowNames = { mApplicationContext
				.getString(R.string.dbGameStateTaskId) };
		Cursor cursor = db
				.query(true,
						mApplicationContext.getString(R.string.dbGameState),
						rowNames,
						mApplicationContext.getString(R.string.dbGameStateId)
								+ "=" + 1, null, null, null, null, null);
		cursor.moveToFirst();

		int taskId = cursor.getInt(cursor
				.getColumnIndexOrThrow(mApplicationContext
						.getString(R.string.dbGameStateTaskId)));
		cursor.close();
		close();

		return taskId;
	}

	/**
	 * Gets the penalty acquired by a wrong answer.
	 */
	public int getWrongAnswerPenalty(int taskId) {

		open();
		// Get penalties from wrong answers given.
		String[] rowNames = {
				mApplicationContext.getString(R.string.dbPenaltiesId),
				mApplicationContext.getString(R.string.dbPenaltiesWrongAnswers) };

		Cursor cursor = db.query(true,
				mApplicationContext.getString(R.string.dbPenalties), rowNames,
				mApplicationContext.getString(R.string.dbPenaltiesTaskId) + "="
						+ taskId, null, null, null, null, null);

		int penalty = 0;

		if (cursor.getCount() != 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				penalty = penalty
						+ cursor.getInt(cursor.getColumnIndexOrThrow(mApplicationContext
								.getString(R.string.dbPenaltiesWrongAnswers)));
			}
		}
		cursor.close();
		close();
		return penalty;
	}

	/**
	 * Gets the penalty acquired by a used hint.
	 */
	public int getUsedHintsPenalty(int taskId) {

		open();
		// Get penalties from used hints.
		String[] rowNames = {
				mApplicationContext.getString(R.string.dbPenaltiesId),
				mApplicationContext.getString(R.string.dbPenaltiesUsedHints) };

		Cursor cursor = db.query(true,
				mApplicationContext.getString(R.string.dbPenalties), rowNames,
				mApplicationContext.getString(R.string.dbPenaltiesTaskId) + "="
						+ taskId, null, null, null, null, null);

		int penalty = 0;

		if (cursor.getCount() != 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				penalty = penalty
						+ cursor.getInt(cursor.getColumnIndexOrThrow(mApplicationContext
								.getString(R.string.dbPenaltiesUsedHints)));
			}
		}
		cursor.close();
		close();
		return penalty;
	}

	/**
	 * Completes the quest with id questId.
	 */
	public void completeQuest(int questId) throws SQLException {
		ContentValues cv = new ContentValues();

		cv.put(mApplicationContext.getString(R.string.dbQuestCompleted), 1);
		db.update(mApplicationContext.getString(R.string.dbQuest), cv,
				mApplicationContext.getString(R.string.dbQuestId) + "="
						+ questId, null);
	}

	/**
	 * Completes the task with id taskId and reset hints and wrong answers.
	 */
	public void completeTask(int taskId) throws SQLException {
		ContentValues cv = new ContentValues();

		cv.put(mApplicationContext.getString(R.string.dbTaskWrongAnswers), 0);
		cv.put(mApplicationContext.getString(R.string.dbTaskCompleted), 1);
		db.update(
				mApplicationContext.getString(R.string.dbTask),
				cv,
				mApplicationContext.getString(R.string.dbTaskId) + "=" + taskId,
				null);

		cv.clear();
		cv.put(mApplicationContext.getString(R.string.dbHintUsed), 0);
		db.update(mApplicationContext.getString(R.string.dbHint), cv,
				mApplicationContext.getString(R.string.dbHintTaskId) + "="
						+ taskId, null);

	}

	/**
	 * Updates the GameState table.
	 */
	public void updateGameState(int pathId, int questId, int taskId)
			throws SQLException {
		open();

		ContentValues cv = new ContentValues();
		cv.put(mApplicationContext.getString(R.string.dbGameStateTaskId),
				taskId);
		cv.put(mApplicationContext.getString(R.string.dbGameStateQuestId),
				questId);
		cv.put(mApplicationContext.getString(R.string.dbGameStatePathId),
				pathId);

		db.update(
				mApplicationContext.getString(R.string.dbGameState),
				cv,
				mApplicationContext.getString(R.string.dbGameStateId) + "=" + 1,
				null);
		close();
	}

	/**
	 * Updates the quest in the database.
	 */
	public void updateQuest(int questId, boolean completed) throws SQLException {
		ContentValues cv = new ContentValues();

		if (completed) {
			cv.put(mApplicationContext.getString(R.string.dbQuestCompleted), 1);
		} else {
			cv.put(mApplicationContext.getString(R.string.dbQuestCompleted), 0);
		}

		db.update(mApplicationContext.getString(R.string.dbQuest), cv,
				mApplicationContext.getString(R.string.dbQuestId) + "="
						+ questId, null);

	}

	/**
	 * Updates the task in the database.
	 */
	public void updateTask(int taskId, int wrongAnswers, boolean completed)
			throws SQLException {
		ContentValues cv = new ContentValues();

		cv.put(mApplicationContext.getString(R.string.dbTaskWrongAnswers),
				wrongAnswers);
		if (completed) {
			cv.put(mApplicationContext.getString(R.string.dbTaskCompleted), 1);
		} else {
			cv.put(mApplicationContext.getString(R.string.dbTaskCompleted), 0);
		}

		db.update(
				mApplicationContext.getString(R.string.dbTask),
				cv,
				mApplicationContext.getString(R.string.dbTaskId) + "=" + taskId,
				null);

	}

	/**
	 * Sets the state of the hint to used
	 */
	public void setHintUsed(int hintId) {
		ContentValues cv = new ContentValues();

		cv.put(mApplicationContext.getString(R.string.dbHintUsed), 1);
		db.update(
				mApplicationContext.getString(R.string.dbHint),
				cv,
				mApplicationContext.getString(R.string.dbHintId) + "=" + hintId,
				null);
	}

	/**
	 * Sets the penalty acquired by a task.
	 */
	public void setPenalties(int taskId) {

		open();

		// Get penalties from wrong answers given.
		String[] rowNames = { mApplicationContext.getString(R.string.dbTaskId),
				mApplicationContext.getString(R.string.dbTaskWrongAnswers),
				mApplicationContext.getString(R.string.dbTaskPenalty) };

		Cursor cursor = db
				.query(true, mApplicationContext.getString(R.string.dbTask),
						rowNames,
						mApplicationContext.getString(R.string.dbTaskId) + "="
								+ taskId, null, null, null, null, null);
		cursor.moveToFirst();

		int result = cursor.getInt(cursor
				.getColumnIndexOrThrow(mApplicationContext
						.getString(R.string.dbTaskPenalty)))
				* cursor.getInt(cursor
						.getColumnIndexOrThrow(mApplicationContext
								.getString(R.string.dbTaskWrongAnswers)));

		// Get penalties from used hints.
		String[] rowNames2 = {
				mApplicationContext.getString(R.string.dbHintId),
				mApplicationContext.getString(R.string.dbHintPenalty),
				mApplicationContext.getString(R.string.dbHintUsed) };

		Cursor cursor2 = db.query(true,
				mApplicationContext.getString(R.string.dbHint), rowNames2,
				mApplicationContext.getString(R.string.dbHintTaskId) + "="
						+ taskId, null, null, null, null, null);

		int result2 = 0;

		if (cursor2.getCount() != 0) {
			cursor2.moveToFirst();
			for (int i = 0; i < cursor2.getCount(); i++) {
				if (cursor2.getInt(cursor2
						.getColumnIndexOrThrow(mApplicationContext
								.getString(R.string.dbHintUsed))) > 0) {
					result2 += cursor2.getInt(cursor2
							.getColumnIndexOrThrow(mApplicationContext
									.getString(R.string.dbHintPenalty)));
				}
				cursor2.moveToNext();
			}
		}

		// Insert penalties in database.
		ContentValues cv = new ContentValues();
		cv.put(mApplicationContext.getString(R.string.dbPenaltiesWrongAnswers),
				result);
		cv.put(mApplicationContext.getString(R.string.dbPenaltiesUsedHints),
				result2);
		cv.put(mApplicationContext.getString(R.string.dbPenaltiesTaskId),
				taskId);

		db.insert(mApplicationContext.getString(R.string.dbPenalties), null, cv);

		cursor.close();
		cursor2.close();
		close();
	}

}
