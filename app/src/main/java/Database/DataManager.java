package Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import Assessments.Assessment;
import Assessments.AssessmentNote;
import Courses.Course;
import Courses.CourseNote;
import Courses.CourseStatus;
import Terms.Term;

public class DataManager {
    // Terms
    public static Uri insertTerm(Context context, String termName, String termStart, String termEnd, int termActive) {
        ContentValues values = new ContentValues();
        values.put(DataHelper.TERM_NAME, termName);
        values.put(DataHelper.TERM_START, termStart);
        values.put(DataHelper.TERM_END, termEnd);
        values.put(DataHelper.TERM_ACTIVE, termActive);
        return context.getContentResolver().insert(DataProvider.TERMS_URI, values);
    }

    public static Term getTerm(Context context, long termId) {
        @SuppressLint("Recycle") Cursor cursor = context.getContentResolver().query(DataProvider.TERMS_URI, DataHelper.TERMS_COLUMNS,
                DataHelper.TERMS_TABLE_ID + " = " + termId, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        String termName = cursor.getString(cursor.getColumnIndex(DataHelper.TERM_NAME));
        String termStartDate = cursor.getString(cursor.getColumnIndex(DataHelper.TERM_START));
        String termEndDate = cursor.getString(cursor.getColumnIndex(DataHelper.TERM_END));
        int termActive = cursor.getInt(cursor.getColumnIndex(DataHelper.TERM_ACTIVE));

        Term t = new Term();
        t.termId = termId;
        t.name = termName;
        t.start = termStartDate;
        t.end = termEndDate;
        t.active = termActive;
        return t;
    }

    // Courses
    public static Uri insertCourse(Context context, long termId, String courseName, String courseStart, String courseEnd,
                                   String courseMentor, String courseMentorPhone, String courseMentorEmail, CourseStatus status) {
        ContentValues values = new ContentValues();
        values.put(DataHelper.COURSE_TERM_ID, termId);
        values.put(DataHelper.COURSE_NAME, courseName);
        values.put(DataHelper.COURSE_START, courseStart);
        values.put(DataHelper.COURSE_END, courseEnd);
        values.put(DataHelper.COURSE_MENTOR, courseMentor);
        values.put(DataHelper.COURSE_MENTOR_PHONE, courseMentorPhone);
        values.put(DataHelper.COURSE_MENTOR_EMAIL, courseMentorEmail);
        values.put(DataHelper.COURSE_STATUS, status.toString());
        return context.getContentResolver().insert(DataProvider.COURSES_URI, values);
    }

    public static Course getCourse(Context context, long courseId) {
        Cursor cursor = context.getContentResolver().query(DataProvider.COURSES_URI, DataHelper.COURSES_COLUMNS,
                DataHelper.COURSES_TABLE_ID + " = " + courseId, null, null);

        Course c = new Course();

        if (cursor != null && cursor.moveToFirst()) {
            long termId = cursor.getLong(cursor.getColumnIndex(DataHelper.COURSE_TERM_ID));
            String courseName = cursor.getString(cursor.getColumnIndex(DataHelper.COURSE_NAME));
            String courseDescription = cursor.getString(cursor.getColumnIndex(DataHelper.COURSE_DESCRIPTION));
            String courseStart = cursor.getString(cursor.getColumnIndex(DataHelper.COURSE_START));
            String courseEnd = cursor.getString(cursor.getColumnIndex(DataHelper.COURSE_END));
            CourseStatus courseStatus = CourseStatus.valueOf(cursor.getString(cursor.getColumnIndex(DataHelper.COURSE_STATUS)));
            String courseMentor = cursor.getString(cursor.getColumnIndex(DataHelper.COURSE_MENTOR));
            String courseMentorPhone = cursor.getString(cursor.getColumnIndex(DataHelper.COURSE_MENTOR_PHONE));
            String courseMentorEmail = cursor.getString(cursor.getColumnIndex(DataHelper.COURSE_MENTOR_EMAIL));
            int courseNotifications = (cursor.getInt(cursor.getColumnIndex(DataHelper.COURSE_NOTIFICATIONS)));

            c.courseId = courseId;
            c.termId = termId;
            c.name = courseName;
            c.description = courseDescription;
            c.start = courseStart;
            c.end = courseEnd;
            c.status = courseStatus;
            c.mentor = courseMentor;
            c.mentorPhone = courseMentorPhone;
            c.mentorEmail = courseMentorEmail;
            c.notifications = courseNotifications;
            return c;


        }

/*        assert cursor != null;
        cursor.moveToFirst();*/
        return c;

    }

    public static boolean deleteCourse(Context context, long courseId) {
        @SuppressLint("Recycle") Cursor notesCursor = context.getContentResolver().query(DataProvider.COURSE_NOTES_URI,
                DataHelper.COURSE_NOTES_COLUMNS, DataHelper.COURSE_NOTE_COURSE_ID + " = " + courseId,
                null, null);
        assert notesCursor != null;
        while (notesCursor.moveToNext()) {
            deleteCourseNote(context, notesCursor.getLong(notesCursor.getColumnIndex(DataHelper.COURSE_NOTES_TABLE_ID)));
        }
        context.getContentResolver().delete(DataProvider.COURSES_URI, DataHelper.COURSES_TABLE_ID + " = "
                + courseId, null);
        return true;
    }

    // Course Notes
    public static Uri insertCourseNote(Context context, long courseId, String text) {
        ContentValues values = new ContentValues();
        values.put(DataHelper.COURSE_NOTE_COURSE_ID, courseId);
        values.put(DataHelper.COURSE_NOTE_TEXT, text);
        return context.getContentResolver().insert(DataProvider.COURSE_NOTES_URI, values);
    }

    public static CourseNote getCourseNote(Context context, long courseNoteId) {
        @SuppressLint("Recycle") Cursor cursor = context.getContentResolver().query(DataProvider.COURSE_NOTES_URI, DataHelper.COURSE_NOTES_COLUMNS,
                DataHelper.COURSE_NOTES_TABLE_ID + " = " + courseNoteId, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        long courseId = cursor.getLong(cursor.getColumnIndex(DataHelper.COURSE_NOTE_COURSE_ID));
        String text = cursor.getString(cursor.getColumnIndex(DataHelper.COURSE_NOTE_TEXT));

        CourseNote c = new CourseNote();
        c.courseNoteId = courseNoteId;
        c.courseId = courseId;
        c.text = text;
        return c;
    }

    public static boolean deleteCourseNote(Context context, long courseNoteId) {
        context.getContentResolver().delete(DataProvider.COURSE_NOTES_URI, DataHelper.COURSE_NOTES_TABLE_ID + " = " + courseNoteId, null);
        return true;
    }

    // Assessments
    public static Uri insertAssessment(Context context, long courseId, String code, String name, String description, String datetime) {
        ContentValues values = new ContentValues();
        values.put(DataHelper.ASSESSMENT_COURSE_ID, courseId);
        values.put(DataHelper.ASSESSMENT_CODE, code);
        values.put(DataHelper.ASSESSMENT_NAME, name);
        values.put(DataHelper.ASSESSMENT_DESCRIPTION, description);
        values.put(DataHelper.ASSESSMENT_DATETIME, datetime);
        return context.getContentResolver().insert(DataProvider.ASSESSMENTS_URI, values);
    }

    public static Assessment getAssessment(Context context, long assessmentId) {
        @SuppressLint("Recycle") Cursor cursor = context.getContentResolver().query(DataProvider.ASSESSMENTS_URI, DataHelper.ASSESSMENTS_COLUMNS,
                DataHelper.ASSESSMENTS_TABLE_ID + " = " + assessmentId, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        long courseId = cursor.getLong(cursor.getColumnIndex(DataHelper.ASSESSMENT_COURSE_ID));
        String name = cursor.getString(cursor.getColumnIndex(DataHelper.ASSESSMENT_NAME));
        String description = cursor.getString(cursor.getColumnIndex(DataHelper.ASSESSMENT_DESCRIPTION));
        String code = cursor.getString(cursor.getColumnIndex(DataHelper.ASSESSMENT_CODE));
        String datetime = cursor.getString(cursor.getColumnIndex(DataHelper.ASSESSMENT_DATETIME));
        int notifications = cursor.getInt(cursor.getColumnIndex(DataHelper.ASSESSMENT_NOTIFICATIONS));

        Assessment a = new Assessment();
        a.assessmentId = assessmentId;
        a.courseId = courseId;
        a.name = name;
        a.description = description;
        a.code = code;
        a.datetime = datetime;
        a.notifications = notifications;
        return a;
    }

    public static boolean deleteAssessment(Context context, long assessmentId) {
        @SuppressLint("Recycle") Cursor notesCursor = context.getContentResolver().query(DataProvider.ASSESSMENT_NOTES_URI,
                DataHelper.ASSESSMENT_NOTES_COLUMNS, DataHelper.ASSESSMENT_NOTE_ASSESSMENT_ID + " = " +
                        assessmentId, null, null);
        assert notesCursor != null;
        while (notesCursor.moveToNext()) {
            deleteAssessmentNote(context, notesCursor.getLong(notesCursor.getColumnIndex(DataHelper.ASSESSMENT_NOTES_TABLE_ID)));
        }
        context.getContentResolver().delete(DataProvider.ASSESSMENTS_URI, DataHelper.ASSESSMENTS_TABLE_ID
                + " = " + assessmentId, null);
        return true;
    }

    // Assessment Notes
    public static Uri insertAssessmentNote(Context context, long assessmentId, String text) {
        ContentValues values = new ContentValues();
        values.put(DataHelper.ASSESSMENT_NOTE_ASSESSMENT_ID, assessmentId);
        values.put(DataHelper.ASSESSMENT_NOTE_TEXT, text);
        return context.getContentResolver().insert(DataProvider.ASSESSMENT_NOTES_URI, values);
    }

    public static AssessmentNote getAssessmentNote(Context context, long assessmentNoteId) {
        @SuppressLint("Recycle") Cursor cursor = context.getContentResolver().query(DataProvider.ASSESSMENT_NOTES_URI,
                DataHelper.ASSESSMENT_NOTES_COLUMNS, DataHelper.ASSESSMENT_NOTES_TABLE_ID + " = "
                        + assessmentNoteId, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        long assessmentId = cursor.getLong(cursor.getColumnIndex(DataHelper.ASSESSMENT_NOTE_ASSESSMENT_ID));
        String text = cursor.getString(cursor.getColumnIndex(DataHelper.ASSESSMENT_NOTE_TEXT));

        AssessmentNote a = new AssessmentNote();
        a.assessmentNoteID = assessmentNoteId;
        a.assessmentID = assessmentId;
        a.text = text;
        return a;
    }

    public static boolean deleteAssessmentNote(Context context, long assessmentNoteId) {
        context.getContentResolver().delete(DataProvider.ASSESSMENT_NOTES_URI, DataHelper.ASSESSMENT_NOTES_TABLE_ID
                + " = " + assessmentNoteId, null);
        return true;
    }
}
