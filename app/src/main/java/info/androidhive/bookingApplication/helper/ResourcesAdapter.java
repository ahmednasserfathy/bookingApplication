package info.androidhive.bookingApplication.helper;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import info.androidhive.bookingApplication.R;

public class ResourcesAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<Resource> mDataSource;

    public ResourcesAdapter(Context context, ArrayList<Resource> items) {
        mDataSource = items;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        View rowView = mInflater.inflate(R.layout.list_item_resources, parent, false);
        // Get title element
        TextView type =
                rowView.findViewById(R.id.resourceType);
        // Get subtitle element
        TextView name =
                rowView.findViewById(R.id.resourceName);
        TextView state =
                rowView.findViewById(R.id.resourceState);
        // Get thumbnail element
        ImageView thumbnailImageView =
                rowView.findViewById(R.id.resourceIcon);

        Resource resource = (Resource) getItem(position);
        type.setText(resource.getType());
        name.setText(resource.getName());
        state.setText("State: " + resource.getState());

        String imgType = resource.getName();
        if (imgType.startsWith("Room")) {
            thumbnailImageView.setImageResource(R.drawable.room);
        } else if (imgType.startsWith("S-PC")) {
            thumbnailImageView.setImageResource(R.drawable.standard);
        } else if (imgType.startsWith("SP-PC")) {
            thumbnailImageView.setImageResource(R.drawable.special);
        } else if (imgType.startsWith("Booth")) {
            thumbnailImageView.setImageResource(R.drawable.group);
        } else if (imgType.startsWith("Scan")) {
            thumbnailImageView.setImageResource(R.drawable.scanner);
        }

        type.setTextColor(Color.BLACK);
        name.setTextColor(Color.BLACK);
        state.setTextColor(Color.BLACK);

        return rowView;
    }
}
