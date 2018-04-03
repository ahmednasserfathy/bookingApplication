package info.androidhive.bookingApplication.helper;

import org.apache.commons.codec.language.Soundex;

import java.util.StringTokenizer;

public class SpeechPatterns {

    public static String[] reserveRoom(String speechText) {
        //"reserve room 1 on the 1st of April at 12:30 a.m. in Library level 1"

        StringTokenizer stringTokenizer = new StringTokenizer(speechText);
        String name, day, month, hourAndMins, time,
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
        time = stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        rSiteLocation = stringTokenizer.nextElement().toString();
        location = stringTokenizer.nextElement().toString();
        locNum = stringTokenizer.nextElement().toString();

        // Using soundex algorithm to correct words
        Soundex soundex = new Soundex();
        String phoneticValue = soundex.encode("for");
        String phoneticValue2 = soundex.encode("to");
        String phoneticValue3 = soundex.encode("Owen");
        if (soundex.encode(nameNum).equals(phoneticValue)) {
            nameNum = "4";
        } else if (soundex.encode(nameNum).equals(phoneticValue2)) {
            nameNum = "2";
        }

        if(soundex.encode(rSiteLocation).equals(phoneticValue3)){
            rSiteLocation = "owen";
        }

        if (rSiteLocation.equals("erwin")) {
            rSiteLocation = "Owen";
        }

        if (name.equals("can")) {
            name = "scan";
        }

        // Combine words before sending to database
        rName = name + "-" + nameNum;
        rLocation = location + " " + locNum;

        time = time.replace(".", "");
        time = time.toUpperCase();
        month = capitalizeFirstLetter(month);

        String trim = hourAndMins.trim();
        int numOfWords = trim.split(":").length;

        if(numOfWords == 1){
            hourAndMins = hourAndMins + ":00";
        }

        // 10:30 AM 14th, March
        rDateBooked = hourAndMins + " " + time + " " + day + ", " + month;

        rName = capitalizeFirstLetter(rName);
        rSiteLocation = capitalizeFirstLetter(rSiteLocation);
        rLocation = capitalizeFirstLetter(rLocation);

        return new String[]{rName, rDateBooked, rSiteLocation, rLocation};
    }

    public static String[] reserveRoomAtCharles(String speechText) {
        //"reserve room 1.1 on the 3rd of December at 3:31 p.m. in Charles building level 1"

        StringTokenizer stringTokenizer = new StringTokenizer(speechText);
        String name, day, month, hourAndMins, time,
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
        time = stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        rSiteLocation = stringTokenizer.nextElement().toString() + " "
                + stringTokenizer.nextElement().toString();
        location = stringTokenizer.nextElement().toString();
        locNum = stringTokenizer.nextElement().toString();

        // Using soundex algorithm to correct words
        Soundex soundex = new Soundex();
        String phoneticValue = soundex.encode("for");
        String phoneticValue2 = soundex.encode("to");
        if (soundex.encode(nameNum).equals(phoneticValue)) {
            nameNum = "4";
        } else if (soundex.encode(nameNum).equals(phoneticValue2)) {
            nameNum = "2";
        }

        if (name.equals("can")) {
            name = "scan";
        }

        // Combine words before sending to database
        rName = name + "-" + nameNum;
        rLocation = location + " " + locNum;

        time = time.replace(".", "");
        time = time.toUpperCase();
        month = capitalizeFirstLetter(month);

        String trim = hourAndMins.trim();
        int numOfWords = trim.split(":").length;

        if(numOfWords == 1){
            hourAndMins = hourAndMins + ":00";
        }

        // 10:30 AM 14th, March
        rDateBooked = hourAndMins + " " + time + " " + day + ", " + month;

        rName = capitalizeFirstLetter(rName);
        rSiteLocation = capitalizeFirstLetter(rSiteLocation);
        rLocation = capitalizeFirstLetter(rLocation);

        return new String[]{rName, rDateBooked, rSiteLocation, rLocation};
    }

    public static String[] reservePC(String speechText) {
        //"reserve standard PC 424 on the 1st of April at 12:30 a.m. in Library level 1"

        StringTokenizer stringTokenizer = new StringTokenizer(speechText);
        String name, day, month, hourAndMins, time,
                rSiteLocation, location, locNum, nameNum;
        String rName, rDateBooked, rLocation;

        // Separate each word
        stringTokenizer.nextElement().toString();
        name = stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        nameNum = stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        day = stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        month = stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        hourAndMins = stringTokenizer.nextElement().toString();
        time = stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        rSiteLocation = stringTokenizer.nextElement().toString();
        location = stringTokenizer.nextElement().toString();
        locNum = stringTokenizer.nextElement().toString();

        // Using soundex algorithm to correct words
        Soundex soundex = new Soundex();
        String phoneticValue = soundex.encode("for");
        String phoneticValue2 = soundex.encode("to");
        if (soundex.encode(nameNum).equals(phoneticValue)) {
            nameNum = "4";
        } else if (soundex.encode(nameNum).equals(phoneticValue2)) {
            nameNum = "2";
        }

        String phoneticValue3 = soundex.encode("Owen");
        if(soundex.encode(rSiteLocation).equals(phoneticValue3)){
            rSiteLocation = "owen";
        }

        if (rSiteLocation.equals("erwin")) {
            rSiteLocation = "Owen";
        }

        // Combine words before sending to database
        if (name.equals("standard")) {
            rName = "S-PC" + nameNum;
        } else {
            rName = "SP-PC" + nameNum;
        }
        // Combine words before sending to database
        rLocation = location + " " + locNum;

        time = time.replace(".", "");
        time = time.toUpperCase();
        month = capitalizeFirstLetter(month);

        String trim = hourAndMins.trim();
        int numOfWords = trim.split(":").length;

        if(numOfWords == 1){
            hourAndMins = hourAndMins + ":00";
        }

        // 10:30 AM 14th, March
        rDateBooked = hourAndMins + " " + time + " " + day + ", " + month;

        rSiteLocation = capitalizeFirstLetter(rSiteLocation);
        rLocation = capitalizeFirstLetter(rLocation);

        return new String[]{rName, rDateBooked, rSiteLocation, rLocation};
    }

    public static String[] reservePCAtCharles(String speechText) {
        //"reserve standard PC 424 on the 1st of April at 12:30 a.m. in Library level 1"

        StringTokenizer stringTokenizer = new StringTokenizer(speechText);
        String name, day, month, hourAndMins, time,
                rSiteLocation, location, locNum, nameNum;
        String rName, rDateBooked, rLocation;

        // Separate each word
        stringTokenizer.nextElement().toString();
        name = stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        nameNum = stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        day = stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        month = stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        hourAndMins = stringTokenizer.nextElement().toString();
        time = stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        rSiteLocation = stringTokenizer.nextElement().toString() + " "
                + stringTokenizer.nextElement().toString();
        location = stringTokenizer.nextElement().toString();
        locNum = stringTokenizer.nextElement().toString();

        // Using soundex algorithm to correct words
        Soundex soundex = new Soundex();
        String phoneticValue = soundex.encode("for");
        String phoneticValue2 = soundex.encode("to");
        if (soundex.encode(nameNum).equals(phoneticValue)) {
            nameNum = "4";
        } else if (soundex.encode(nameNum).equals(phoneticValue2)) {
            nameNum = "2";
        }

        // Combine words before sending to database
        if (name.equals("standard")) {
            rName = "S-PC" + nameNum;
        } else {
            rName = "SP-PC" + nameNum;
        }
        // Combine words before sending to database
        rLocation = location + " " + locNum;

        time = time.replace(".", "");
        time = time.toUpperCase();
        month = capitalizeFirstLetter(month);

        String trim = hourAndMins.trim();
        int numOfWords = trim.split(":").length;

        if(numOfWords == 1){
            hourAndMins = hourAndMins + ":00";
        }

        // 10:30 AM 14th, March
        rDateBooked = hourAndMins + " " + time + " " + day + ", " + month;

        rSiteLocation = capitalizeFirstLetter(rSiteLocation);
        rLocation = capitalizeFirstLetter(rLocation);

        return new String[]{rName, rDateBooked, rSiteLocation, rLocation};
    }

    public static String cancelBooking(String speechText) {
        StringTokenizer stringTokenizer = new StringTokenizer(speechText);
        // Separate each word
        stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        return stringTokenizer.nextElement().toString();
    }
    public static String finishBooking(String speechText) {
        StringTokenizer stringTokenizer = new StringTokenizer(speechText);
        // Separate each word
        stringTokenizer.nextElement().toString();
        stringTokenizer.nextElement().toString();
        return stringTokenizer.nextElement().toString();
    }

    private static String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }
}
