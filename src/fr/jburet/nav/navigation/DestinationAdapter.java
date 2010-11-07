package fr.jburet.nav.navigation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.jburet.nav.R;
import fr.jburet.nav.database.point.Waypoint;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DestinationAdapter extends BaseAdapter {

	private List<Waypoint> waypoints;

	private Context context;

	public DestinationAdapter(Context context, Collection<Waypoint> waypoints) {
		this.waypoints = new ArrayList<Waypoint>();
		this.waypoints.addAll(waypoints);
		this.context = context;
	}

	public int getCount() {
		return waypoints.size();
	}

	public Object getItem(int position) {
		return waypoints.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Waypoint waypoint = (Waypoint) getItem(position);
		View returnView;
		if(convertView!=null){
			returnView = convertView;
		}else{
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            returnView = vi.inflate(R.layout.destination_item, parent, false);
		}
		((TextView)returnView.findViewById(R.id.destination_listitem_name)).setText(waypoint.getName());
		return returnView;
	}
}
