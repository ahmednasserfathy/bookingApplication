package info.androidhive.bookingApplication.helper;

import org.json.JSONException;
import org.json.JSONObject;

public class Booking {

    private int id;
    private String name;
    private String dateBooked;
    private String siteLocation;
    private String location;
    private String status;

    public Booking(JSONObject obj) {
        try {
            this.id = obj.getInt("id");
            this.name = obj.getString("name").toString();
            this.dateBooked = obj.getString("dateBooked").toString();
            this.siteLocation = obj.getString("siteLocation").toString();
            this.location = obj.getString("location").toString();
            this.status = obj.getString("status").toString();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDateBooked() {
        return dateBooked;
    }

    public String getSiteLocation() {
        return siteLocation;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String newStatus) {
        status = newStatus;
    }

}
