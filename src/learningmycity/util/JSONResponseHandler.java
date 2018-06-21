package learningmycity.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import learningmycity.content.Image;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.JsonReader;
import android.util.Log;

/**
 * A helper class for fetching, reading and parsing JSON data.
 */
public class JSONResponseHandler {
	private static final String TAG = "JSONResponseHandler";

	public volatile boolean parsingComplete = true;
	public static String urlServer = "http://1-dot-learningmycity.appspot.com/";
	// public static String urlServer = "http://192.168.1.1:8888/";
	public static String urlGetPath = urlServer + "path?pathId=";
	public static String urlGetPaths = urlServer + "path/getAll";
	public static String urlGetScore = urlServer
			+ "score/search/findByPathId?pathId=";
	public static String urlPostScore = urlServer + "score";
	public static String urlGetImages = urlServer
			+ "image/search/findByPathId?pathId=";

	JSONObject path;
	JSONArray quests;
	JSONArray tasks;
	JSONArray questions;
	JSONArray alternatives;
	JSONArray hints;
	ArrayList<Image> images;
	JSONArray list;
	JSONArray scores;

	/**
	 * Reads the images of the path as stream.
	 */
	public void readJsonImage(InputStream in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		try {
			readImageArray(reader);

			parsingComplete = false;
		} finally {
			reader.close();
		}
	}

	/**
	 * Fills the ArrayList with images.
	 */
	public void readImageArray(JsonReader reader) throws IOException {
		images = new ArrayList<Image>();

		reader.beginArray();
		while (reader.hasNext()) {
			images.add(readImage(reader));
		}
		reader.endArray();
	}

	/**
	 * Parses one image at a time.
	 */
	public Image readImage(JsonReader reader) throws IOException {
		String imageId = null;
		String imageName = null;
		String imageString = null;

		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("imageName")) {
				imageName = reader.nextString();
			} else if (name.equals("imageId")) {
				imageId = reader.nextString();
			} else if (name.equals("imageString")) {
				imageString = reader.nextString();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return new Image(Integer.parseInt(imageId), imageName, imageString);
	}

	/**
	 * Reads the list of available paths.
	 */
	public void readJsonPathList(String in) {
		try {
			// Read the paths
			list = new JSONArray(in);

			parsingComplete = false;

		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	/**
	 * Reads the list of available paths.
	 */
	public void readJsonScore(String in) {
		try {
			// Read the score
			scores = new JSONArray(in);

			parsingComplete = false;

		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	/**
	 * Reads the selected path.
	 */
	public void readJsonPath(String in) {
		try {
			// Read the path
			path = new JSONObject(in);

			// Read the quests
			quests = path.getJSONArray("quests");

			// Read the tasks
			tasks = path.getJSONArray("tasks");

			// Read the questions
			questions = path.getJSONArray("questions");

			// Read the alternatives
			alternatives = path.getJSONArray("alternatives");

			// Read the hints
			hints = path.getJSONArray("hints");

			parsingComplete = false;

		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}

	}

	public void fetchJSONImages(int pathId) throws Exception {
		Log.i(TAG, "fetchJSONImages");

		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
																				// Limit
		HttpGet post = new HttpGet(urlGetImages + pathId);

		// try {
		HttpResponse response = client.execute(post);
		// StatusLine statusLine = response.getStatusLine();
		// int statusCode = statusLine.getStatusCode();
		// if (statusCode == 200) {
		HttpEntity entity = response.getEntity();
		InputStream content = entity.getContent();
		readJsonImage(content);
		// } else {
		// Log.e("TAG", "Failed to download file");
		// }
		// } catch (ClientProtocolException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		readJsonPath(builder.toString());

	}

	public void fetchJSONPath(int pathId) throws Exception {
		Log.i(TAG, "fetchJSONPath");

		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
																				// Limit
		HttpGet post = new HttpGet(urlGetPath + pathId);

		// try {
		HttpResponse response = client.execute(post);
		// StatusLine statusLine = response.getStatusLine();
		// int statusCode = statusLine.getStatusCode();
		// if (statusCode == 200) {
		HttpEntity entity = response.getEntity();
		InputStream content = entity.getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				content));
		String line;
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}
		// } else {
		// Log.e("TAG", "Failed to download file");
		// }
		// } catch (ClientProtocolException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		readJsonPath(builder.toString());

	}

	public void fetchJSONList() throws Exception {
		Log.i(TAG, "fetchJSONList");

		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
																				// Limit
		HttpGet post = new HttpGet(urlGetPaths);

		// try {
		HttpResponse response = client.execute(post);
		// StatusLine statusLine = response.getStatusLine();
		// int statusCode = statusLine.getStatusCode();
		// if (statusCode == 200) {
		HttpEntity entity = response.getEntity();
		InputStream content = entity.getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				content));
		String line;
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}
		// } else {
		// Log.e("TAG", "Failed to download file");
		// }
		// } catch (ClientProtocolException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		readJsonPathList(builder.toString());
	}

	public void fetchJSONScore(int pathId) throws Exception {
		Log.i(TAG, "fetchJSONScore");

		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
																				// Limit
		HttpGet post = new HttpGet(urlGetScore + pathId);

		// try {
		HttpResponse response = client.execute(post);
		// StatusLine statusLine = response.getStatusLine();
		// int statusCode = statusLine.getStatusCode();
		// if (statusCode == 200) {
		HttpEntity entity = response.getEntity();
		InputStream content = entity.getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				content));
		String line;
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}
		// } else {
		// Log.e("TAG", "Failed to download file");
		// }
		// } catch (ClientProtocolException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		readJsonScore(builder.toString());
	}

	public void postJSONScore(int pathId, String userName, int score,
			String date) throws Exception {
		Log.i(TAG, "postJSONScore");

		// Looper.prepare(); //For Preparing Message Pool for the child Thread
		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
																				// Limit
		HttpResponse response;
		JSONObject obj = new JSONObject();

		HttpPost post = new HttpPost(urlPostScore);

		obj.put("pathId", pathId);
		obj.put("userName", userName);
		obj.put("score", score);
		obj.put("date", date);

		StringEntity se = new StringEntity(obj.toString(), "UTF8");
		se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		post.setEntity(se);
		response = client.execute(post);

		/* Checking response */
		if (response != null) {
			InputStream in = response.getEntity().getContent(); // Get the data
																// in the entity
			Log.i(TAG, "" + in);
		}

		// Looper.loop(); //Loop in the message queue
	}

	public ArrayList<Image> getImages() {
		return images;
	}

	public JSONObject getPath() {
		return path;
	}

	public JSONArray getList() {
		return list;
	}

	public JSONArray getScores() {
		return scores;
	}

	public JSONArray getQuests() {
		return quests;
	}

	public JSONArray getTasks() {
		return tasks;
	}

	public JSONArray getQuestions() {
		return questions;
	}

	public JSONArray getAlternatives() {
		return alternatives;
	}

	public JSONArray getHints() {
		return hints;
	}

	static String convertStreamToString(java.io.InputStream is) {
		@SuppressWarnings("resource")
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

}
