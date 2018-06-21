package learningmycity.db;

import learningmycity.content.Task;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tsivas061.learningmycity.R;

/**
 * The class handling connections to the database.
 */
public class DbHelper extends SQLiteOpenHelper {

	private static final String TAG = "DbHelper";

	// The activity invoking this database helper.
	private Context mApplicationContext;

	/**
	 * Creates a new database helper.
	 */
	public DbHelper(Context context) {
		super(context, "database", null, 1);
		this.mApplicationContext = context;
	}

	/**
	 * Creates the database.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "onCreate");

		// Creates the database tables.
		createTables(db);

		// Creates a new game state.
		createGameState(db);
	}

	/**
	 * Upgrades the database by deleting all tables and creating it from
	 * scratch.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "onUpgrade");

		db.execSQL("DROP TABLE IF EXISTS "
				+ mApplicationContext.getString(R.string.dbPath));
		db.execSQL("DROP TABLE IF EXISTS "
				+ mApplicationContext.getString(R.string.dbQuest));
		db.execSQL("DROP TABLE IF EXISTS "
				+ mApplicationContext.getString(R.string.dbTask));
		db.execSQL("DROP TABLE IF EXISTS "
				+ mApplicationContext.getString(R.string.dbGameState));
		db.execSQL("DROP TABLE IF EXISTS "
				+ mApplicationContext.getString(R.string.dbQuestion));
		db.execSQL("DROP TABLE IF EXISTS "
				+ mApplicationContext.getString(R.string.dbAlternative));
		db.execSQL("DROP TABLE IF EXISTS "
				+ mApplicationContext.getString(R.string.dbHint));
		db.execSQL("DROP TABLE IF EXISTS "
				+ mApplicationContext.getString(R.string.dbGPS));
		db.execSQL("DROP TABLE IF EXISTS "
				+ mApplicationContext.getString(R.string.dbPenalties));
		db.execSQL("DROP TABLE IF EXISTS "
				+ mApplicationContext.getString(R.string.dbImage));

		onCreate(db);
	}

	public void resetDatabase(SQLiteDatabase db) {
		Log.i(TAG, "resetDatabase");

		db.execSQL("DROP TABLE IF EXISTS "
				+ mApplicationContext.getString(R.string.dbPath));
		db.execSQL("DROP TABLE IF EXISTS "
				+ mApplicationContext.getString(R.string.dbQuest));
		db.execSQL("DROP TABLE IF EXISTS "
				+ mApplicationContext.getString(R.string.dbTask));
		db.execSQL("DROP TABLE IF EXISTS "
				+ mApplicationContext.getString(R.string.dbGameState));
		db.execSQL("DROP TABLE IF EXISTS "
				+ mApplicationContext.getString(R.string.dbQuestion));
		db.execSQL("DROP TABLE IF EXISTS "
				+ mApplicationContext.getString(R.string.dbAlternative));
		db.execSQL("DROP TABLE IF EXISTS "
				+ mApplicationContext.getString(R.string.dbHint));
		db.execSQL("DROP TABLE IF EXISTS "
				+ mApplicationContext.getString(R.string.dbGPS));
		db.execSQL("DROP TABLE IF EXISTS "
				+ mApplicationContext.getString(R.string.dbPenalties));
		db.execSQL("DROP TABLE IF EXISTS "
				+ mApplicationContext.getString(R.string.dbImage));

		onCreate(db);
	}

	/**
	 * Creates the database tables.
	 */
	public void createTables(SQLiteDatabase db) {
		Log.i(TAG, "createTables");

		// Path table.
		String createPathTable = "CREATE TABLE "
				+ mApplicationContext.getString(R.string.dbPath) + " ("
				+ mApplicationContext.getString(R.string.dbPathId)
				+ " integer primary key autoincrement, "
				+ mApplicationContext.getString(R.string.dbPathDescription)
				+ " text, "
				+ mApplicationContext.getString(R.string.dbPathName)
				+ " text);";
		db.execSQL(createPathTable);

		// Quest table.
		String createQuestTable = "CREATE TABLE "
				+ mApplicationContext.getString(R.string.dbQuest) + " ("
				+ mApplicationContext.getString(R.string.dbQuestId)
				+ " integer primary key autoincrement, "
				+ mApplicationContext.getString(R.string.dbQuestName)
				+ " text, "
				+ mApplicationContext.getString(R.string.dbQuestImage)
				+ " integer, "
				+ mApplicationContext.getString(R.string.dbQuestLatitude)
				+ " text, "
				+ mApplicationContext.getString(R.string.dbQuestLongitude)
				+ " text, "
				+ mApplicationContext.getString(R.string.dbQuestPathId)
				+ " integer not null, "
				+ mApplicationContext.getString(R.string.dbQuestDescription)
				+ " integer not null, "
				+ mApplicationContext.getString(R.string.dbQuestCompleted)
				+ " integer);";
		db.execSQL(createQuestTable);

		// Task table.
		String createTaskTable = "CREATE TABLE "
				+ mApplicationContext.getString(R.string.dbTask) + " ("
				+ mApplicationContext.getString(R.string.dbTaskId)
				+ " integer primary key autoincrement, "
				+ mApplicationContext.getString(R.string.dbTaskQuestId)
				+ " integer not null, "
				+ mApplicationContext.getString(R.string.dbTaskDescription)
				+ " text, "
				+ mApplicationContext.getString(R.string.dbTaskCompleted)
				+ " integer, "
				+ mApplicationContext.getString(R.string.dbTaskImage)
				+ " integer, "
				+ mApplicationContext.getString(R.string.dbTaskType)
				+ " integer not null, "
				+ mApplicationContext.getString(R.string.dbTaskPenalty)
				+ " integer not null, "
				+ mApplicationContext.getString(R.string.dbTaskWrongAnswers)
				+ " integer);";
		db.execSQL(createTaskTable);

		// GameState table.
		String createGameStateTable = "CREATE TABLE "
				+ mApplicationContext.getString(R.string.dbGameState) + " ("
				+ mApplicationContext.getString(R.string.dbGameStateId)
				+ " integer primary key autoincrement, "
				+ mApplicationContext.getString(R.string.dbGameStatePathId)
				+ " integer not null, "
				+ mApplicationContext.getString(R.string.dbGameStateQuestId)
				+ " integer not null, "
				+ mApplicationContext.getString(R.string.dbGameStateTaskId)
				+ " integer);";
		db.execSQL(createGameStateTable);

		// Question table.
		String createQuestionTable = "CREATE TABLE "
				+ mApplicationContext.getString(R.string.dbQuestion) + " ("
				+ mApplicationContext.getString(R.string.dbQuestionId)
				+ " integer primary key autoincrement, "
				+ mApplicationContext.getString(R.string.dbQuestionQuestion)
				+ " text, "
				+ mApplicationContext.getString(R.string.dbQuestionSolution)
				+ " text, "
				+ mApplicationContext.getString(R.string.dbQuestionTaskId)
				+ " integer not null);";
		db.execSQL(createQuestionTable);

		// Alternative table.
		String createAlternativeTable = "CREATE TABLE "
				+ mApplicationContext.getString(R.string.dbAlternative)
				+ " ("
				+ mApplicationContext.getString(R.string.dbAlternativeId)
				+ " integer primary key autoincrement, "
				+ mApplicationContext
						.getString(R.string.dbAlternativeAlternative)
				+ " text, "
				+ mApplicationContext.getString(R.string.dbAlternativeTaskId)
				+ " integer not null);";
		db.execSQL(createAlternativeTable);

		// Hint table.
		String createHintTable = "CREATE TABLE "
				+ mApplicationContext.getString(R.string.dbHint) + " ("
				+ mApplicationContext.getString(R.string.dbHintId)
				+ " integer primary key autoincrement, "
				+ mApplicationContext.getString(R.string.dbHintDescription)
				+ " text, "
				+ mApplicationContext.getString(R.string.dbHintTaskId)
				+ " integer not null, "
				+ mApplicationContext.getString(R.string.dbHintPenalty)
				+ " integer, "
				+ mApplicationContext.getString(R.string.dbHintUsed)
				+ " integer, "
				+ mApplicationContext.getString(R.string.dbHintImage)
				+ " integer);";
		db.execSQL(createHintTable);

		// GPS table.
		String createGPSTable = "CREATE TABLE "
				+ mApplicationContext.getString(R.string.dbGPS) + " ("
				+ mApplicationContext.getString(R.string.dbGPSId)
				+ " integer primary key autoincrement, "
				+ mApplicationContext.getString(R.string.dbGPSLongitude)
				+ " text, "
				+ mApplicationContext.getString(R.string.dbGPSLatitude)
				+ " text, "
				+ mApplicationContext.getString(R.string.dbGPSRadius)
				+ " integer, "
				+ mApplicationContext.getString(R.string.dbGPSQuestId)
				+ " integer not null);";
		db.execSQL(createGPSTable);

		// Penalties table.
		String createPenaltiesTable = "CREATE TABLE "
				+ mApplicationContext.getString(R.string.dbPenalties)
				+ " ("
				+ mApplicationContext.getString(R.string.dbPenaltiesId)
				+ " integer primary key autoincrement, "
				+ mApplicationContext
						.getString(R.string.dbPenaltiesWrongAnswers)
				+ " integer, "
				+ mApplicationContext.getString(R.string.dbPenaltiesUsedHints)
				+ " integer, "
				+ mApplicationContext.getString(R.string.dbPenaltiesTaskId)
				+ " integer not null);";
		db.execSQL(createPenaltiesTable);

		// Images table.
		String createImagesTable = "CREATE TABLE "
				+ mApplicationContext.getString(R.string.dbImage) + " ("
				+ mApplicationContext.getString(R.string.dbImageId)
				+ " integer primary key autoincrement, "
				+ mApplicationContext.getString(R.string.dbImageName)
				+ " text, "
				+ mApplicationContext.getString(R.string.dbImageString)
				+ " text);";
		db.execSQL(createImagesTable);
	}

	/**
	 * Creates a new game state.
	 */
	public void createGameState(SQLiteDatabase db) {
		Log.i(TAG, "createGameState");

		ContentValues cv = new ContentValues();
		cv.put(mApplicationContext.getString(R.string.dbGameStatePathId), 0);
		cv.put(mApplicationContext.getString(R.string.dbGameStateQuestId), 0);
		cv.put(mApplicationContext.getString(R.string.dbGameStateTaskId), 0);

		db.insert(mApplicationContext.getString(R.string.dbGameState), null, cv);
	}

	/**
	 * Adds a question to a task.
	 */
	public void addQuestion(SQLiteDatabase db, int taskId, String question,
			String answer) {
		if (taskId < 0) {
			Log.e(TAG, "Could not add question. Invalid task ID: " + taskId);
			return;
		}

		ContentValues cv = new ContentValues();
		cv.put(mApplicationContext.getString(R.string.dbQuestionQuestion),
				question);
		cv.put(mApplicationContext.getString(R.string.dbQuestionTaskId),
				(int) taskId);
		cv.put(mApplicationContext.getString(R.string.dbQuestionSolution),
				answer);

		try {
			db.insert(mApplicationContext.getString(R.string.dbQuestion), null,
					cv);
		} catch (SQLException e) {
			Log.e(TAG, "Could not insert question (Task ID: " + taskId);
			Log.e(TAG, e.getMessage());
		}
	}

	/**
	 * Adds a multiple choice alternative to a task
	 */
	public void addAlternative(SQLiteDatabase db, int taskId, String alternative) {
		if (taskId < 0) {
			Log.e(TAG, "Could not add alternative. Invalid task ID: " + taskId);
			return;
		}

		ContentValues cv = new ContentValues();
		cv.put(mApplicationContext.getString(R.string.dbAlternativeAlternative),
				alternative);
		cv.put(mApplicationContext.getString(R.string.dbAlternativeTaskId),
				(int) taskId);

		try {
			db.insert(mApplicationContext.getString(R.string.dbAlternative),
					null, cv);
		} catch (SQLException e) {
			Log.e(TAG, "Could not insert alternative (Task ID: " + taskId);
			Log.e(TAG, e.getMessage());
		}
	}

	/**
	 * Adds a hint to a task.
	 */
	public void addHint(SQLiteDatabase db, int taskId, String description,
			int imageId, int penalty) {
		if (taskId < 0) {
			Log.e(TAG, "Could not add hint. Invalid task ID: " + taskId);
			return;
		}

		ContentValues cv = new ContentValues();
		cv.put(mApplicationContext.getString(R.string.dbHintTaskId),
				(int) taskId);
		cv.put(mApplicationContext.getString(R.string.dbHintDescription),
				description);
		cv.put(mApplicationContext.getString(R.string.dbHintImage), imageId);
		cv.put(mApplicationContext.getString(R.string.dbHintPenalty), penalty);
		cv.put(mApplicationContext.getString(R.string.dbHintUsed), 0);

		try {
			db.insert(mApplicationContext.getString(R.string.dbHint), null, cv);
		} catch (SQLException e) {
			Log.e(TAG, "Could not insert hint (Task ID: " + taskId);
			Log.e(TAG, e.getMessage());
		}
	}

	/**
	 * Creates a path in the database.
	 */
	public void createPath(SQLiteDatabase db, int pathId, String pathName,
			String pathDescription) {

		ContentValues cv = new ContentValues();
		cv.put(mApplicationContext.getString(R.string.dbPathId), pathId);
		cv.put(mApplicationContext.getString(R.string.dbPathName), pathName);
		cv.put(mApplicationContext.getString(R.string.dbPathDescription),
				pathDescription);

		db.insert(mApplicationContext.getString(R.string.dbPath), null, cv);
	}

	/**
	 * Creates a quest in the database.
	 */
	public void createQuest(SQLiteDatabase db, int questId, String questName,
			int questImageID, String questDescription, double latitude,
			double longitude, int pathId, int completed) {

		ContentValues cv = new ContentValues();
		cv.put(mApplicationContext.getString(R.string.dbQuestId), questId);
		cv.put(mApplicationContext.getString(R.string.dbQuestName), questName);
		cv.put(mApplicationContext.getString(R.string.dbQuestImage),
				questImageID);
		cv.put(mApplicationContext.getString(R.string.dbQuestDescription),
				questDescription);
		cv.put(mApplicationContext.getString(R.string.dbQuestLatitude),
				latitude);
		cv.put(mApplicationContext.getString(R.string.dbQuestLongitude),
				longitude);
		cv.put(mApplicationContext.getString(R.string.dbQuestPathId), pathId);
		cv.put(mApplicationContext.getString(R.string.dbQuestCompleted),
				completed);

		db.insert(mApplicationContext.getString(R.string.dbQuest), null, cv);
	}

	/**
	 * Creates a task in the database.
	 */
	public int createTask(SQLiteDatabase db, String taskDescription,
			int taskType, int taskCompleted, int taskImageID, int taskPenalty,
			int taskWrongAnswers, int questId, double latitude,
			double longitude, double radius) {
		int id = -1;

		ContentValues cv = new ContentValues();
		switch (taskType) {
		case Task.OPEN_TASK:
			cv.put(mApplicationContext.getString(R.string.dbTaskDescription),
					taskDescription);
			cv.put(mApplicationContext.getString(R.string.dbTaskCompleted),
					taskCompleted);
			cv.put(mApplicationContext.getString(R.string.dbTaskImage),
					taskImageID);
			cv.put(mApplicationContext.getString(R.string.dbTaskType), taskType);
			cv.put(mApplicationContext.getString(R.string.dbTaskPenalty),
					taskPenalty);
			cv.put(mApplicationContext.getString(R.string.dbTaskWrongAnswers),
					taskWrongAnswers);
			cv.put(mApplicationContext.getString(R.string.dbTaskQuestId),
					questId);

			try {
				id = (int) db.insertOrThrow(
						mApplicationContext.getString(R.string.dbTask), null,
						cv);
			} catch (SQLException e) {
				Log.e(TAG, "Could not insert Open task");
				Log.e(TAG, e.getMessage());
			}
			return id;
		case Task.MULTIPLE_CHOICE_TASK:
			cv.put(mApplicationContext.getString(R.string.dbTaskDescription),
					taskDescription);
			cv.put(mApplicationContext.getString(R.string.dbTaskCompleted),
					taskCompleted);
			cv.put(mApplicationContext.getString(R.string.dbTaskImage),
					taskImageID);
			cv.put(mApplicationContext.getString(R.string.dbTaskType), taskType);
			cv.put(mApplicationContext.getString(R.string.dbTaskPenalty),
					taskPenalty);
			cv.put(mApplicationContext.getString(R.string.dbTaskWrongAnswers),
					taskWrongAnswers);
			cv.put(mApplicationContext.getString(R.string.dbTaskQuestId),
					questId);

			try {
				id = (int) db.insertOrThrow(
						mApplicationContext.getString(R.string.dbTask), null,
						cv);
			} catch (SQLException e) {
				Log.e(TAG, "Could not insert MultipleChoice task");
				Log.e(TAG, e.getMessage());
			}
			return id;
		case Task.CHECKBOX_TASK:
			cv.put(mApplicationContext.getString(R.string.dbTaskDescription),
					taskDescription);
			cv.put(mApplicationContext.getString(R.string.dbTaskCompleted),
					taskCompleted);
			cv.put(mApplicationContext.getString(R.string.dbTaskImage),
					taskImageID);
			cv.put(mApplicationContext.getString(R.string.dbTaskType), taskType);
			cv.put(mApplicationContext.getString(R.string.dbTaskPenalty),
					taskPenalty);
			cv.put(mApplicationContext.getString(R.string.dbTaskWrongAnswers),
					taskWrongAnswers);
			cv.put(mApplicationContext.getString(R.string.dbTaskQuestId),
					questId);

			try {
				id = (int) db.insertOrThrow(
						mApplicationContext.getString(R.string.dbTask), null,
						cv);
			} catch (SQLException e) {
				Log.e(TAG, "Could not insert CkeckBox task");
				Log.e(TAG, e.getMessage());
			}
			return id;
		case Task.GPS_TASK:
			cv.put(mApplicationContext.getString(R.string.dbTaskDescription),
					taskDescription);
			cv.put(mApplicationContext.getString(R.string.dbTaskCompleted),
					taskCompleted);
			cv.put(mApplicationContext.getString(R.string.dbTaskImage),
					taskImageID);
			cv.put(mApplicationContext.getString(R.string.dbTaskType), taskType);
			cv.put(mApplicationContext.getString(R.string.dbTaskPenalty),
					taskPenalty);
			cv.put(mApplicationContext.getString(R.string.dbTaskWrongAnswers),
					taskWrongAnswers);
			cv.put(mApplicationContext.getString(R.string.dbTaskQuestId),
					questId);

			try {
				id = (int) db.insertOrThrow(
						mApplicationContext.getString(R.string.dbTask), null,
						cv);

				cv.clear();
				cv.put(mApplicationContext.getString(R.string.dbGPSLatitude),
						latitude);
				cv.put(mApplicationContext.getString(R.string.dbGPSLongitude),
						longitude);
				cv.put(mApplicationContext.getString(R.string.dbGPSRadius),
						radius);
				cv.put(mApplicationContext.getString(R.string.dbGPSQuestId), id);
				db.insertOrThrow(mApplicationContext.getString(R.string.dbGPS),
						null, cv);
			} catch (SQLException e) {
				Log.e(TAG, "Could not insert GPS task");
				Log.e(TAG, e.getMessage());
			}
			return id;
		}
		return -1;
	}

	/**
	 * Creates an image in the database.
	 */
	public void createImage(SQLiteDatabase db, int imageId, String imageName,
			String imageString) {

		ContentValues cv = new ContentValues();
		cv.put(mApplicationContext.getString(R.string.dbImageId), imageId);
		cv.put(mApplicationContext.getString(R.string.dbImageName), imageName);
		cv.put(mApplicationContext.getString(R.string.dbImageString),
				imageString);

		db.insert(mApplicationContext.getString(R.string.dbImage), null, cv);
	}
}
