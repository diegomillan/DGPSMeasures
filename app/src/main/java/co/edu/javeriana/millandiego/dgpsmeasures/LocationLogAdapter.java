package co.edu.javeriana.millandiego.dgpsmeasures;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class LocationLogAdapter extends ArrayAdapter<LocationLog> {

    private Context mcontext;
    int mresource;
    public LocationLogAdapter(Context context, int resource, List<LocationLog> objects) {
        super(context, resource, objects);
        mcontext = context;
        mresource = resource;
    }

    @NonNull
    @Override
    public View getView(int position,View convertView, ViewGroup parent) {
        String location = getItem(position).getLocation();
        LocationLog current_location = new LocationLog(location);

        LayoutInflater inflater = LayoutInflater.from(mcontext);
        convertView = inflater.inflate(mresource,parent,false);

        TextView details = (TextView) convertView.findViewById(R.id.location_message);
        details.setText(current_location.getLocation());
        return convertView;
    }
}
