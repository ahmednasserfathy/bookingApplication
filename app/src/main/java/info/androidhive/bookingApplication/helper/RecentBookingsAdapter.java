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

public class RecentBookingsAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<Booking> mDataSource;

    public RecentBookingsAdapter(Context context, ArrayList<Booking> items) {
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

    public void clearData() {
        // clear the data
        mDataSource.clear();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = mInflater.inflate(R.layout.list_item_bookings, parent, false);
        // Get title element
        TextView titleTextView =
                rowView.findViewById(R.id.booking_list_title);
        // Get subtitle element
        TextView subtitleTextView =
                rowView.findViewById(R.id.booking_list_subtitle);
        TextView statusTextView =
                rowView.findViewById(R.id.booking_list_status);
        TextView siteLocationTextView =
                rowView.findViewById(R.id.booking_list_siteLocation);
        TextView locationTextView =
                rowView.findViewById(R.id.booking_list_location);
        // Get thumbnail element
        ImageView thumbnailImageView =
                rowView.findViewById(R.id.booking_list_thumbnail);

        Booking aBooking = (Booking) getItem(position);
        titleTextView.setText(aBooking.getName());
        subtitleTextView.setText(aBooking.getDateBooked());
        statusTextView.setText("Status: " + aBooking.getStatus());
        siteLocationTextView.setText(aBooking.getSiteLocation());
        locationTextView.setText(aBooking.getLocation());

        String imgType = aBooking.getName().toLowerCase();
        if (imgType.startsWith("room")) {
            thumbnailImageView.setImageResource(R.drawable.room);
        } else if (imgType.startsWith("s-pc")) {
            thumbnailImageView.setImageResource(R.drawable.standard);
        } else if (imgType.startsWith("sp-pc")) {
            thumbnailImageView.setImageResource(R.drawable.special);
        } else if (imgType.startsWith("booth")) {
            thumbnailImageView.setImageResource(R.drawable.group);
        } else if (imgType.startsWith("scan")) {
            thumbnailImageView.setImageResource(R.drawable.scanner);
        }

        titleTextView.setTextColor(Color.BLACK);
        subtitleTextView.setTextColor(Color.BLACK);
        statusTextView.setTextColor(Color.BLACK);
        locationTextView.setTextColor(Color.BLACK);
        siteLocationTextView.setTextColor(Color.BLACK);

        return rowView;
    }
}
