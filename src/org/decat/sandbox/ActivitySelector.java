package org.decat.sandbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class ActivitySelector extends ListActivity implements OnItemClickListener {
	private static final String KEY_TITLE = "TITLE";
	private static final String KEY_RESOLVE_INFO = "RESOLVE_INFO";
	private static final String FROM[] = new String[] {
			KEY_TITLE, KEY_RESOLVE_INFO
	};
	private static final int TO[] = new int[] {
			R.id.title, R.id.icon
	};
	private List<Map<String, ?>> list;
	private List<ResolveInfo> resolvInfos;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// List installed applications
		PackageManager pm = this.getPackageManager();

		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		resolvInfos = pm.queryIntentActivities(mainIntent, 0);
		Collections.sort(resolvInfos, new ResolveInfo.DisplayNameComparator(pm));
		list = new ArrayList<Map<String, ?>>();
		int resolvInfosSize = resolvInfos.size();
		for (int i = 0; i < resolvInfosSize; i++) {
			ResolveInfo resolvInfo = resolvInfos.get(i);
			/*
			 * Simple adapter craziness. For each item, we need to create a map
			 * from a key to its value (the value can be any object--the view
			 * binder will take care of filling the View with a representation
			 * of that object).
			 */
			Map<String, Object> map = new TreeMap<String, Object>();
			CharSequence label = resolvInfo.loadLabel(getPackageManager());
			if (label == null) {
				label = resolvInfo.activityInfo.name;
			}
			map.put(KEY_TITLE, label.toString());
			map.put(KEY_RESOLVE_INFO, resolvInfo);
			list.add(map);
		}

		// Register this as an item click listener
		getListView().setOnItemClickListener(this);

		// Set up our adapter
		setListAdapter(new SimpleAdapter(this, list, R.layout.activity_picker_layout, FROM, TO));
	}

	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		// Extract data
		if (position >= resolvInfos.size()) {
			return;
		}
		ResolveInfo resolvInfo = resolvInfos.get(position);
		String value = resolvInfo.activityInfo.applicationInfo.packageName + "/" + resolvInfo.activityInfo.name;

		// Show a toast
		StringBuilder sb = new StringBuilder("Selected id=");
		sb.append(id);
		sb.append(", position=");
		sb.append(position);
		sb.append(", value=");
		sb.append(value);
		Sandbox.showToast(this, sb.toString());

		// Prepare result for calling activity
		Intent result = new Intent();
		result.putExtra("id", id);
		result.putExtra("value", value);
		setResult(RESULT_OK, result);

		// Finish this activity
		finish();
	}
}