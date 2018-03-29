package info.androidhive.bookingApplication.helper;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import info.androidhive.bookingApplication.R;

public class ListViewResources extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Resource> mDataSource;

    public ListViewResources(Context context, ArrayList<Resource> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clearData() {
        // clear the data
        mDataSource.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = mInflater.inflate(R.layout.resourceview, parent, false);
        // Get title element
        TextView type =
                (TextView) rowView.findViewById(R.id.resourceType);
        // Get subtitle element
        TextView name =
                (TextView) rowView.findViewById(R.id.resourceName);
        TextView state =
                (TextView) rowView.findViewById(R.id.resourceState);
        TextView location =
                (TextView) rowView.findViewById(R.id.resourceLocation);

        Resource resource = (Resource) getItem(position);
        type.setText(resource.getType());
        name.setText(resource.getName());
        state.setText("State: " + resource.getState());
        location.setText("Location: " + resource.getLocation());

        type.setTextColor(Color.WHITE);
        name.setTextColor(Color.WHITE);
        state.setTextColor(Color.WHITE);
        location.setTextColor(Color.WHITE);

        return rowView;
    }
}
