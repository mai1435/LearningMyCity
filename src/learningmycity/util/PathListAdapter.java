package learningmycity.util;

import java.util.ArrayList;

import learningmycity.content.Path;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tsivas061.learningmycity.R;

/**
 * The List Adapter.
 */
public class PathListAdapter extends BaseAdapter {

	private ArrayList<Path> list = new ArrayList<Path>();
	private static LayoutInflater inflater = null;
	private Context mContext;

	// The typeFace for importing custom fonts.
	private Typeface typeFace;

	public PathListAdapter(Context context) {
		mContext = context;
		inflater = LayoutInflater.from(mContext);
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {

		View newView = convertView;
		ViewHolder holder;

		Path curr = list.get(position);

		if (null == convertView) {
			holder = new ViewHolder();
			newView = inflater.inflate(R.layout.path_list, null);

			// Import external font style.
			typeFace = Typeface.createFromAsset(mContext
					.getAssets(), "fonts/Caudex-Italic.ttf");

			holder.pathName = (TextView) newView.findViewById(R.id.path_name);
			holder.pathName.setTypeface(typeFace);
			holder.pathDescription = (TextView) newView
					.findViewById(R.id.path_description);
			holder.pathDescription.setTypeface(typeFace);
			newView.setTag(holder);

		} else {
			holder = (ViewHolder) newView.getTag();
		}

		holder.pathName.setText(curr.getPathName());
		holder.pathDescription.setText(curr.getPathDescription());

		return newView;
	}

	static class ViewHolder {
		TextView pathName;
		TextView pathDescription;
	}

	public boolean intersects(String pathName) {
		for (Path item : list) {
			if (item.intersects(pathName)) {
				return true;
			}
		}
		return false;
	}

	public void add(Path listItem) {
		list.add(listItem);
		notifyDataSetChanged();
	}

	public ArrayList<Path> getList() {
		return list;
	}

	public void removeAllViews() {
		list.clear();
		this.notifyDataSetChanged();
	}

}
