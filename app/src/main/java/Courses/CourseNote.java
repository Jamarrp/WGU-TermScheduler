package Courses;

import android.content.ContentValues;
import android.content.Context;

import Database.DataHelper;
import Database.DataProvider;

public class CourseNote {
    public long courseNoteId;
    public long courseId;
    public String text;

    public void saveChanges(Context context) {
        ContentValues values = new ContentValues();
        values.put(DataHelper.COURSE_NOTE_COURSE_ID, courseId);
        values.put(DataHelper.COURSE_NOTE_TEXT, text);
        context.getContentResolver().update(DataProvider.COURSE_NOTES_URI, values, DataHelper.COURSE_NOTES_TABLE_ID
                + " = " + courseNoteId, null);
    }
}
