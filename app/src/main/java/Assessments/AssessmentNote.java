package Assessments;

import android.content.ContentValues;
import android.content.Context;

import Database.DataHelper;
import Database.DataProvider;

public class AssessmentNote {
    public long assessmentNoteID;
    public long assessmentID;
    public String text;

    public void saveChanges(Context context) {
        ContentValues values = new ContentValues();
        values.put(DataHelper.ASSESSMENT_NOTE_ASSESSMENT_ID, assessmentID);
        values.put(DataHelper.ASSESSMENT_NOTE_TEXT, text);
        context.getContentResolver().update(DataProvider.ASSESSMENT_NOTES_URI, values, DataHelper.ASSESSMENT_NOTES_TABLE_ID
                + " = " + assessmentNoteID, null);
    }
}
