package Database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class DataProvider extends ContentProvider {

// AUTHORITY AND PATH STRINGS ======================================================================
    private static final String AUTHORITY = "com.jamarp.wgu_termscheduler.dataprovider";
    private static final String TERMS_PATH = "terms";
    private static final String COURSES_PATH = "courses";
    private static final String COURSE_NOTES_PATH = "courseNotes";
    private static final String ASSESSMENTS_PATH = "assessments";
    private static final String ASSESSMENT_NOTES_PATH = "assessmentNotes";
    private static final String IMAGES_PATH = "images";

// PATH URI'S ======================================================================================
    public static final Uri TERMS_URI = Uri.parse("content://" + AUTHORITY + "/" + TERMS_PATH);
    public static final Uri COURSES_URI = Uri.parse("content://" + AUTHORITY + "/" + COURSES_PATH);
    public static final Uri COURSE_NOTES_URI = Uri.parse("content://" + AUTHORITY + "/" + COURSE_NOTES_PATH);
    public static final Uri ASSESSMENTS_URI = Uri.parse("content://" + AUTHORITY + "/" + ASSESSMENTS_PATH);
    public static final Uri ASSESSMENT_NOTES_URI = Uri.parse("content://" + AUTHORITY + "/" + ASSESSMENT_NOTES_PATH);
    public static final Uri IMAGES_URI = Uri.parse("content://" + AUTHORITY + "/" + IMAGES_PATH);

// CONSTANT TO IDENTIFY THE REQUESTED OPERATION ====================================================
    private static final int TERMS = 1;
    private static final int TERMS_ID = 2;
    private static final int COURSES = 3;
    private static final int COURSES_ID = 4;
    private static final int COURSE_NOTES = 5;
    private static final int COURSE_NOTES_ID = 6;
    private static final int ASSESSMENTS = 7;
    private static final int ASSESSMENTS_ID = 8;
    private static final int ASSESSMENT_NOTES = 9;
    private static final int ASSESSMENT_NOTES_ID = 10;
    private static final int IMAGES = 11;
    private static final int IMAGES_ID = 12;

// URI MATCHER INITIALIZATION ======================================================================
    private static final UriMatcher URIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        URIMatcher.addURI(AUTHORITY, TERMS_PATH, TERMS);
        URIMatcher.addURI(AUTHORITY, TERMS_PATH + "/#", TERMS_ID);
        URIMatcher.addURI(AUTHORITY, COURSES_PATH, COURSES);
        URIMatcher.addURI(AUTHORITY, COURSES_PATH + "/#", COURSES_ID);
        URIMatcher.addURI(AUTHORITY, COURSE_NOTES_PATH, COURSE_NOTES);
        URIMatcher.addURI(AUTHORITY, COURSE_NOTES_PATH + "/#", COURSE_NOTES_ID);
        URIMatcher.addURI(AUTHORITY, ASSESSMENTS_PATH, ASSESSMENTS);
        URIMatcher.addURI(AUTHORITY, ASSESSMENTS_PATH + "/#", ASSESSMENTS_ID);
        URIMatcher.addURI(AUTHORITY, ASSESSMENT_NOTES_PATH, ASSESSMENT_NOTES);
        URIMatcher.addURI(AUTHORITY, ASSESSMENT_NOTES_PATH + "/#", ASSESSMENT_NOTES_ID);
        URIMatcher.addURI(AUTHORITY, IMAGES_PATH, IMAGES);
        URIMatcher.addURI(AUTHORITY, IMAGES_PATH + "/#", IMAGES_ID);
    }

    public static final String TERM_CONTENT_TYPE = "term";
    public static final String COURSE_CONTENT_TYPE = "course";
    public static final String COURSE_NOTE_CONTENT_TYPE = "courseNote";
    public static final String ASSESSMENT_CONTENT_TYPE = "assessment";
    public static final String ASSESSMENT_NOTE_CONTENT_TYPE = "assessmentNote";
    public static final String IMAGE_CONTENT_TYPE = "image";

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        DataHelper helper = new DataHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri URI, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (URIMatcher.match(URI)) {
            case TERMS:
                return database.query(DataHelper.TERMS_TABLE, DataHelper.TERMS_COLUMNS, selection, null,
                        null, null, DataHelper.TERMS_TABLE_ID + " ASC");
            case COURSES:
                return database.query(DataHelper.COURSES_TABLE, DataHelper.COURSES_COLUMNS, selection, null,
                        null, null, DataHelper.COURSES_TABLE_ID + " ASC");
            case COURSES_ID:
                return database.query(DataHelper.COURSES_TABLE, DataHelper.COURSES_COLUMNS,
                        DataHelper.COURSES_TABLE_ID + "=" + URI.getLastPathSegment(), null,
                        null, null, DataHelper.COURSES_TABLE_ID + " ASC");
            case COURSE_NOTES:
                return database.query(DataHelper.COURSE_NOTES_TABLE, DataHelper.COURSE_NOTES_COLUMNS, selection, null,
                        null, null, DataHelper.COURSE_NOTES_TABLE_ID + " ASC");
            case ASSESSMENTS:
                return database.query(DataHelper.ASSESSMENTS_TABLE, DataHelper.ASSESSMENTS_COLUMNS, selection, null,
                        null, null, DataHelper.ASSESSMENTS_TABLE_ID + " ASC");
            case ASSESSMENT_NOTES:
                return database.query(DataHelper.ASSESSMENT_NOTES_TABLE, DataHelper.ASSESSMENT_NOTES_COLUMNS, selection, null,
                        null, null, DataHelper.ASSESSMENT_NOTES_TABLE_ID + " ASC");
            case IMAGES:
                return database.query(DataHelper.IMAGES_TABLE, DataHelper.IMAGES_COLUMNS, selection, null,
                        null, null, DataHelper.IMAGES_TABLE_ID + " ASC");
            default:
                throw new IllegalArgumentException("UNSUPPORTED URI: " + URI);
        }
    }

    @Override
    public String getType(Uri URI) {
        return null;
    }

    @Override
    public Uri insert(Uri URI, ContentValues values) {
        long ID;
        switch (URIMatcher.match(URI)) {
            case TERMS:
                ID = database.insert(DataHelper.TERMS_TABLE, null, values);
                return URI.parse(TERMS_PATH + "/" + ID);
            case COURSES:
                ID = database.insert(DataHelper.COURSES_TABLE, null, values);
                return URI.parse(COURSES_PATH + "/" + ID);
            case COURSE_NOTES:
                ID = database.insert(DataHelper.COURSE_NOTES_TABLE, null, values);
                return URI.parse(COURSE_NOTES_PATH + "/" + ID);
            case ASSESSMENTS:
                ID = database.insert(DataHelper.ASSESSMENTS_TABLE, null, values);
                return URI.parse(ASSESSMENTS_PATH + "/" + ID);
            case ASSESSMENT_NOTES:
                ID = database.insert(DataHelper.ASSESSMENT_NOTES_TABLE, null, values);
                return URI.parse(ASSESSMENT_NOTES_PATH + "/" + ID);
            case IMAGES:
                ID = database.insert(DataHelper.IMAGES_TABLE, null, values);
                return URI.parse(IMAGES_PATH + "/" + ID);
            default:
                throw new IllegalArgumentException("UNSUPPORTED URI: " + URI);
        }
    }

    @Override
    public int delete(Uri URI, String selection, String[] selectionArgs) {
        switch (URIMatcher.match(URI)) {
            case TERMS:
                return database.delete(DataHelper.TERMS_TABLE, selection, selectionArgs);
            case COURSES:
                return database.delete(DataHelper.COURSES_TABLE, selection, selectionArgs);
            case COURSE_NOTES:
                return database.delete(DataHelper.COURSE_NOTES_TABLE, selection, selectionArgs);
            case ASSESSMENTS:
                return database.delete(DataHelper.ASSESSMENTS_TABLE, selection, selectionArgs);
            case ASSESSMENT_NOTES:
                return database.delete(DataHelper.ASSESSMENT_NOTES_TABLE, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("UNSUPPORTED URI: " + URI);
        }
    }

    @Override
    public int update(Uri URI, ContentValues values, String selection, String[] selectionArgs) {
        switch (URIMatcher.match(URI)) {
            case TERMS:
                return database.update(DataHelper.TERMS_TABLE, values, selection, selectionArgs);
            case COURSES:
                return database.update(DataHelper.COURSES_TABLE, values, selection, selectionArgs);
            case COURSE_NOTES:
                return database.update(DataHelper.COURSE_NOTES_TABLE, values, selection, selectionArgs);
            case ASSESSMENTS:
                return database.update(DataHelper.ASSESSMENTS_TABLE, values, selection, selectionArgs);
            case ASSESSMENT_NOTES:
                return database.update(DataHelper.ASSESSMENT_NOTES_TABLE, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("UNSUPPORTED URI: " + URI);
        }
    }
}