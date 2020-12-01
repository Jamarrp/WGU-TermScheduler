package Terms;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import Database.DataHelper;
import Database.DataProvider;

public class Term {
    public long termId;
    public String name;
    public String start;
    public String end;
    public int active;

    public void saveChanges(Context context) {
        ContentValues values = new ContentValues();
        values.put(DataHelper.TERM_NAME, name);
        values.put(DataHelper.TERM_START, start);
        values.put(DataHelper.TERM_END, end);
        values.put(DataHelper.TERM_ACTIVE, active);
        context.getContentResolver().update(DataProvider.TERMS_URI, values, DataHelper.TERMS_TABLE_ID
                + " = " + termId, null);
    }

    public long getClassCount(Context context) {
        @SuppressLint("Recycle") Cursor cursor = context.getContentResolver().query(DataProvider.COURSES_URI, DataHelper.COURSES_COLUMNS,
                DataHelper.COURSE_TERM_ID + " = " + this.termId, null, null);
        return cursor.getCount();
    }

    public void activate(Context context) {
        this.active = 1;
        saveChanges(context);
    }

    public void deactivate(Context context) {
        this.active = 0;
        saveChanges(context);
    }
}
