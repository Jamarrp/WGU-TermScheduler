package Assessments;

import android.content.ContentValues;
import android.content.Context;

import Database.DataHelper;
import Database.DataProvider;

public class Assessment {
    public long assessmentId;
    public long courseId;
    public String code;
    public String name;
    public String description;
    public String datetime;
    public int notifications;

    public void saveChanges(Context context) {
        ContentValues values = new ContentValues();
        values.put(DataHelper.ASSESSMENT_COURSE_ID, courseId);
        values.put(DataHelper.ASSESSMENT_CODE, code);
        values.put(DataHelper.ASSESSMENT_NAME, name);
        values.put(DataHelper.ASSESSMENT_DESCRIPTION, description);
        values.put(DataHelper.ASSESSMENT_DATETIME, datetime);
        values.put(DataHelper.ASSESSMENT_NOTIFICATIONS, notifications);
        context.getContentResolver().update(DataProvider.ASSESSMENTS_URI, values, DataHelper.ASSESSMENTS_TABLE_ID
                + " = " + assessmentId, null);
    }
}
