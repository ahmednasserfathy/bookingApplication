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

public class ListViewAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Booking> mDataSource;

    public ListViewAdapter(Context context, ArrayList<Booking> items) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = mInflater.inflate(R.layout.list_item_resource, parent, false);
        // Get title element
        TextView titleTextView =
                (TextView) rowView.findViewById(R.id.booking_list_title);
        // Get subtitle element
        TextView subtitleTextView =
                (TextView) rowView.findViewById(R.id.booking_list_subtitle);
        TextView statusTextView =
                (TextView) rowView.findViewById(R.id.booking_list_status);
        // Get thumbnail element
        ImageView thumbnailImageView =
                (ImageView) rowView.findViewById(R.id.booking_list_thumbnail);

        Booking resource = (Booking) getItem(position);
        titleTextView.setText(resource.name);
        subtitleTextView.setText(resource.dateBooked);
        statusTextView.setText("Status: " + resource.status);
        thumbnailImageView.setImageResource(R.drawable.chrome);

        titleTextView.setTextColor(Color.BLACK);
        subtitleTextView.setTextColor(Color.BLACK);
        statusTextView.setTextColor(Color.BLACK);

        return rowView;
    }
}
