package info.androidhive.bookingApplication.helper;

public class Booking {

    private String name;
    private String dateBooked;
    private String status;

    public Booking(String name, String date, String status) {
        this.name = name;
        this.dateBooked = date;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getDateBooked() {
        return dateBooked;
    }

    public String getStatus() {
        return status;
    }
}
