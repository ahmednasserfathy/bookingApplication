package info.androidhive.bookingApplication.helper;

import org.json.JSONException;
import org.json.JSONObject;

public class Resource {
    private String type;
    private String name;
    private String state;
    private String siteLocation;
    private String location;


    public Resource(JSONObject obj) {
        try {
            this.type = obj.getString("type").toString();
            this.name = obj.getString("name").toString();
            this.state = obj.getString("state").toString();
            this.siteLocation = obj.getString("siteLocation").toString();
            this.location = obj.getString("location").toString();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getState() {
        return state;
    }

    public void setState(String newState) {
        state = newState;
    }

    public String getSiteLocation() {
        return siteLocation;
    }

    public String getLocation() {
        return location;
    }
}
