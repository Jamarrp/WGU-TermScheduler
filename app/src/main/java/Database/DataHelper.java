package Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHelper extends SQLiteOpenHelper {

// CONSTANTS FOR DATABASE NAME AND VERSION NUMBER ==================================================
    private static final String DATABASE_NAME = "wgu_terms.db";
    private static final int DATABASE_VERSION = 1;

// CONSTANTS FOR DATABASE TABLES AND COLUMNS
// TERMS TABLE =====================================================================================
    public static final String TERMS_TABLE = "terms";
    public static final String TERMS_TABLE_ID = "_id";
    public static final String TERM_NAME = "termName";
    public static final String TERM_START = "termStart";
    public static final String TERM_END = "termEnd";
    public static final String TERM_ACTIVE = "termActive";
    public static final String TERM_CREATED = "termCreated";
    public static final String[] TERMS_COLUMNS = {
            TERMS_TABLE_ID,
            TERM_NAME,
            TERM_START,
            TERM_END,
            TERM_ACTIVE,
            TERM_CREATED
    };

// COURSES TABLE ===================================================================================
    public static final String COURSES_TABLE = "courses";
    public static final String COURSES_TABLE_ID = "_id";
    public static final String COURSE_TERM_ID = "courseTermId";
    public static final String COURSE_NAME = "courseName";
    public static final String COURSE_DESCRIPTION = "courseDescription";
    public static final String COURSE_START = "courseStart";
    public static final String COURSE_END = "courseEnd";
    public static final String COURSE_STATUS = "courseStatus";
    public static final String COURSE_MENTOR = "courseMentor";
    public static final String COURSE_MENTOR_PHONE = "courseMentorPhone";
    public static final String COURSE_MENTOR_EMAIL = "courseMentorEmail";
    public static final String COURSE_NOTIFICATIONS = "courseNotifications";
    public static final String COURSE_CREATED = "courseCreated";
    public static final String[] COURSES_COLUMNS = {
            COURSES_TABLE_ID,
            COURSE_TERM_ID,
            COURSE_NAME,
            COURSE_DESCRIPTION,
            COURSE_START,
            COURSE_END,
            COURSE_STATUS,
            COURSE_MENTOR,
            COURSE_MENTOR_PHONE,
            COURSE_MENTOR_EMAIL,
            COURSE_NOTIFICATIONS,
            COURSE_CREATED
    };

// COURSE NOTES TABLE ==============================================================================
    public static final String COURSE_NOTES_TABLE = "courseNotes";
    public static final String COURSE_NOTES_TABLE_ID = "_id";
    public static final String COURSE_NOTE_COURSE_ID = "courseNoteCourseId";
    public static final String COURSE_NOTE_TEXT = "courseNoteText";
    public static final String COURSE_NOTE_IMAGE_URI = "courseNoteUri";
    public static final String COURSE_NOTE_CREATED = "courseNoteCreated";
    public static final String[] COURSE_NOTES_COLUMNS = {
            COURSE_NOTES_TABLE_ID,
            COURSE_NOTE_COURSE_ID,
            COURSE_NOTE_TEXT,
            COURSE_NOTE_IMAGE_URI,
            COURSE_NOTE_CREATED
    };

// ASSESSMENTS TABLE ===============================================================================
    public static final String ASSESSMENTS_TABLE = "assessments";
    public static final String ASSESSMENTS_TABLE_ID = "_id";
    public static final String ASSESSMENT_COURSE_ID = "assessmentCourseId";
    public static final String ASSESSMENT_CODE = "assessmentCode";
    public static final String ASSESSMENT_NAME = "assessmentName";
    public static final String ASSESSMENT_DESCRIPTION = "assessmentDescription";
    public static final String ASSESSMENT_DATETIME = "assessmentDateTime";
    public static final String ASSESSMENT_NOTIFICATIONS = "assessmentNotifications";
    public static final String ASSESSMENT_CREATED = "assessmentCreated";
    public static final String[] ASSESSMENTS_COLUMNS = {
            ASSESSMENTS_TABLE_ID,
            ASSESSMENT_COURSE_ID,
            ASSESSMENT_CODE,
            ASSESSMENT_NAME,
            ASSESSMENT_DESCRIPTION,
            ASSESSMENT_DATETIME,
            ASSESSMENT_NOTIFICATIONS,
            ASSESSMENT_CREATED
    };

// ASSESSMENT NOTES TABLE ==========================================================================
    public static final String ASSESSMENT_NOTES_TABLE = "assessmentNotes";
    public static final String ASSESSMENT_NOTES_TABLE_ID = "_id";
    public static final String ASSESSMENT_NOTE_ASSESSMENT_ID = "assessmentNoteAssessmentId";
    public static final String ASSESSMENT_NOTE_TEXT = "assessmentNoteText";
    public static final String ASSESSMENT_NOTE_IMAGE_URI = "assessmentNoteUri";
    public static final String ASSESSMENT_NOTE_CREATED = "assessmentNoteCreated";
    public static final String[] ASSESSMENT_NOTES_COLUMNS = {
            ASSESSMENT_NOTES_TABLE_ID,
            ASSESSMENT_NOTE_ASSESSMENT_ID,
            ASSESSMENT_NOTE_TEXT,
            ASSESSMENT_NOTE_IMAGE_URI,
            ASSESSMENT_NOTE_CREATED
    };

// IMAGES TABLE ====================================================================================
    public static final String IMAGES_TABLE = "images";
    public static final String IMAGES_TABLE_ID = "_id";
    public static final String IMAGE_PARENT_URI = "imageUri";
    public static final String IMAGE_TIMESTAMP = "imageTimestamp";
    public static final String IMAGE_CREATED = "imageCreated";
    public static final String[] IMAGES_COLUMNS = {
            IMAGES_TABLE_ID,
            IMAGE_PARENT_URI,
            IMAGE_TIMESTAMP,
            IMAGE_CREATED
    };

// TREATE TABLE SQLITE COMMAND STRINGS
// TERMS TABLE =====================================================================================
    private static final String CREATE_TERMS_TABLE =
        "CREATE TABLE IF NOT EXISTS " + TERMS_TABLE + " (" +
                TERMS_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TERM_NAME + " TEXT, " +
                TERM_START + " DATE, " +
                TERM_END + " DATE, " +
                TERM_ACTIVE + " INTEGER, " +
                TERM_CREATED + " TEXT default CURRENT_TIMESTAMP" + ")";

// COURSES TABLE ===================================================================================
    private static final String CREATE_COURSES_TABLE =
        "CREATE TABLE IF NOT EXISTS " + COURSES_TABLE + " (" +
                COURSES_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COURSE_TERM_ID + " INTEGER, " +
                COURSE_NAME + " TEXT, " +
                COURSE_DESCRIPTION + " TEXT, " +
                COURSE_START + " DATE, " +
                COURSE_END + " DATE, " +
                COURSE_STATUS + " DATE, " +
                COURSE_MENTOR + " TEXT, " +
                COURSE_MENTOR_PHONE + " TEXT, " +
                COURSE_MENTOR_EMAIL + " TEXT, " +
                COURSE_NOTIFICATIONS + " INTEGER, " +
                COURSE_CREATED + " TEXT default CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(" + COURSE_TERM_ID + ") REFERENCES " + TERMS_TABLE + "(" + TERMS_TABLE_ID + ")" + ")";

// COURSE NOTES TABLE ==============================================================================
    private static final String CREATE_COURSE_NOTES_TABLE =
        "CREATE TABLE IF NOT EXISTS " + COURSE_NOTES_TABLE + " (" +
                COURSE_NOTES_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COURSE_NOTE_COURSE_ID + " INTEGER, " +
                COURSE_NOTE_TEXT + " TEXT, " +
                COURSE_NOTE_IMAGE_URI + " TEXT, " +
                COURSE_NOTE_CREATED + " TEXT default CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(" + COURSE_NOTE_COURSE_ID + ") REFERENCES " + COURSES_TABLE + "(" + COURSES_TABLE_ID + ")" + ")";

// ASSESSMENTS TABLE ===============================================================================
    private static final String CREATE_ASSESSMENTS_TABLE =
        "CREATE TABLE IF NOT EXISTS " + ASSESSMENTS_TABLE + " (" +
                ASSESSMENTS_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ASSESSMENT_COURSE_ID + " INTEGER, " +
                ASSESSMENT_NAME + " TEXT, " +
                ASSESSMENT_DESCRIPTION + " TEXT, " +
                ASSESSMENT_CODE + " TEXT, " +
                ASSESSMENT_DATETIME + " TEXT, " +
                ASSESSMENT_NOTIFICATIONS + " INTEGER, " +
                ASSESSMENT_CREATED + " TEXT default CURRENT_TIMESTAMP, " + "FOREIGN KEY(" +
                ASSESSMENT_COURSE_ID + ") REFERENCES " +
                COURSES_TABLE + "(" +
                COURSES_TABLE_ID + ")" + ")";

// ASSESSMENT NOTES TABLE ==========================================================================
    private static final String CREATE_ASSESSMENT_NOTES_TABLE =
        "CREATE TABLE IF NOT EXISTS " + ASSESSMENT_NOTES_TABLE + " (" +
                ASSESSMENT_NOTES_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ASSESSMENT_NOTE_ASSESSMENT_ID + " INTEGER, " +
                ASSESSMENT_NOTE_TEXT + " TEXT, " +
                ASSESSMENT_NOTE_IMAGE_URI + " TEXT, " +
                ASSESSMENT_NOTE_CREATED + " TEXT default CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(" + ASSESSMENT_NOTE_ASSESSMENT_ID + ") REFERENCES " + ASSESSMENTS_TABLE + "(" + ASSESSMENTS_TABLE_ID + ")" + ")";

// IMAGES TABLE ====================================================================================
    private static final String CREATE_IMAGES_TABLE =
        "CREATE TABLE IF NOT EXISTS " + IMAGES_TABLE + " (" +
                IMAGES_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                IMAGE_PARENT_URI + " TEXT, " +
                IMAGE_TIMESTAMP + " INTEGER, " +
                IMAGE_CREATED + " TEXT default CURRENT_TIMESTAMP" + ")";

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL(CREATE_TERMS_TABLE);
        DB.execSQL(CREATE_COURSES_TABLE);
        DB.execSQL(CREATE_COURSE_NOTES_TABLE);
        DB.execSQL(CREATE_ASSESSMENTS_TABLE);
        DB.execSQL(CREATE_ASSESSMENT_NOTES_TABLE);
        DB.execSQL(CREATE_IMAGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int oldVersion, int newVersion) {
        DB.execSQL("DROP TABLE IF EXISTS " + TERMS_TABLE);
        DB.execSQL("DROP TABLE IF EXISTS " + COURSES_TABLE);
        DB.execSQL("DROP TABLE IF EXISTS " + COURSE_NOTES_TABLE);
        DB.execSQL("DROP TABLE IF EXISTS " + ASSESSMENTS_TABLE);
        DB.execSQL("DROP TABLE IF EXISTS " + ASSESSMENT_NOTES_TABLE);
        DB.execSQL("DROP TABLE IF EXISTS " + IMAGES_TABLE);
        onCreate(DB);
    }
}