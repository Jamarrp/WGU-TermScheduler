package Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateManager {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd h:mm a z", Locale.getDefault());

    public static long getDateTimestamp(String dateInput) {
        try {
            Date date = DateManager.dateFormat.parse(dateInput + TimeZone.getDefault().getDisplayName());
            return date.getTime();
        }
        catch (ParseException e) {
            return 0;
        }
    }

    public static long todayLong() {
        String currentDate = DateManager.dateFormat.format(new Date());
        return getDateTimestamp(currentDate);
    }

    public static long todayLongWithTime() {
        return System.currentTimeMillis();
    }
}
