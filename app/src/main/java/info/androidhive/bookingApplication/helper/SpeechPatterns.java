package info.androidhive.bookingApplication.helper;

import java.util.StringTokenizer;

public class SpeechPatterns {

    public static String[] reserveRoom(String speechText) {

        //test cases:
        //"reserve room 1 on the 1st of April at 12:30 a.m. in Library level 1"
        //"reserve room 3 on the 24th of March at 9 a.m. in Library level 2"
        //"reserve room 4.3 on the 15th of june at 1 p.m. in Library level 4"

        StringTokenizer stringTokenizer = new StringTokenizer(speechText);
        String name, day, month, hourAndMins, amOrPM,
                rSiteLocation, location, locNum, nameNum;
        String rName, rDateBooked, rLocation;

        // Separate each word
        stringTokenizer.nextElement().toString();
        name = stringTokenizer.nextElement().toString();
        nameNum = stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        day = stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        month = stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        hourAndMins = stringTokenizer.nextElement().toString();
        amOrPM = stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        rSiteLocation = stringTokenizer.nextElement().toString();
        location = stringTokenizer.nextElement().toString();
        locNum = stringTokenizer.nextElement().toString();

        // Combine words before sending to database
        rName = name + "-" + nameNum;
        rDateBooked = day + " " + month;
        rLocation = location + " " + locNum;

        rName = capitalizeFirstLetter(rName);
        rSiteLocation = capitalizeFirstLetter(rSiteLocation);
        rLocation = capitalizeFirstLetter(rLocation);

        return new String[]{rName, rDateBooked, rSiteLocation, rLocation};
    }

    public static String[] reserveRoomAtCharles(String speechText) {

        // soundex

        StringTokenizer stringTokenizer = new StringTokenizer(speechText);
        String name, day, month, hourAndMins, amOrPM,
                rSiteLocation, location, locNum, nameNum;
        String rName, rDateBooked, rLocation;

        //"reserve room 1.1 on the 3rd of December at 3:31 p.m. in Charles building level 1"

        // Separate each word
        stringTokenizer.nextElement().toString();
        name = stringTokenizer.nextElement().toString();
        nameNum = stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        day = stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        month = stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        hourAndMins = stringTokenizer.nextElement().toString();
        amOrPM = stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        rSiteLocation = stringTokenizer.nextElement().toString() + " "
                + stringTokenizer.nextElement().toString();
        location = stringTokenizer.nextElement().toString();
        locNum = stringTokenizer.nextElement().toString();

        // Combine words before sending to database
        rName = name + "-" + nameNum;
        rDateBooked = day + " " + month;
        rLocation = location + " " + locNum;

        rName = capitalizeFirstLetter(rName);
        rSiteLocation = capitalizeFirstLetter(rSiteLocation);
        rLocation = capitalizeFirstLetter(rLocation);

        return new String[]{rName, rDateBooked, rSiteLocation, rLocation};
    }

    public static String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }
}
