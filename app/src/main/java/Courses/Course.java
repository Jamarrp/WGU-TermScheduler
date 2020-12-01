package Courses;

import android.content.ContentValues;
import android.content.Context;

import Database.DataHelper;
import Database.DataProvider;

public class Course {
    public long courseId;
    public long termId;
    public String name;
    public String description;
    public String start;
    public String end;
    public CourseStatus status;
    public String mentor;
    public String mentorPhone;
    public String mentorEmail;
    public int notifications;

    public void saveChanges(Context context) {
        ContentValues values = new ContentValues();
        values.put(DataHelper.COURSE_TERM_ID, termId);
        values.put(DataHelper.COURSE_NAME, name);
        values.put(DataHelper.COURSE_DESCRIPTION, description);
        values.put(DataHelper.COURSE_START, start);
        values.put(DataHelper.COURSE_END, end);
        values.put(DataHelper.COURSE_STATUS, status.toString());
        values.put(DataHelper.COURSE_MENTOR, mentor);
        values.put(DataHelper.COURSE_MENTOR_PHONE, mentorPhone);
        values.put(DataHelper.COURSE_MENTOR_EMAIL, mentorEmail);
        values.put(DataHelper.COURSE_NOTIFICATIONS, notifications);
        context.getContentResolver().update(DataProvider.COURSES_URI, values, DataHelper.COURSES_TABLE_ID
                + " = " + courseId, null);
    }
}
