package Terms.TermActivities;

import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.jamarp.wgu_termscheduler.R;

import java.util.GregorianCalendar;
import java.util.Objects;

import Courses.CourseStatus;
import Database.DataHelper;
import Database.DataManager;
import Database.DataProvider;
import Utilities.AlarmHandler;


public class TermListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int TERM_EDITOR_ACTIVITY_CODE = 11111;
    public static final int TERM_DETAIL_ACTIVITY_CODE = 22222;

    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        String[] from = {DataHelper.TERM_NAME, DataHelper.TERM_START, DataHelper.TERM_END};
        int[] to = {R.id.termNameText, R.id.termStartDate, R.id.termEndDate};

        cursorAdapter = new SimpleCursorAdapter(this, R.layout.list_item_term, null, from, to, 0);
        DataProvider database = new DataProvider();

        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TermListActivity.this, TermDetailActivity.class);
                Uri uri = Uri.parse(DataProvider.TERMS_URI + "/" + id);
                intent.putExtra(DataProvider.TERM_CONTENT_TYPE, uri);
                startActivityForResult(intent, TERM_DETAIL_ACTIVITY_CODE);
            }
        });
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            restartLoader();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_create_sample:
                return generateExampleData();
            case R.id.action_delete_all_terms:
                return deleteAllTerms();
            case R.id.action_create_test_alarm:
                return createTestAlarm();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean generateExampleData() {
        Uri term1Uri = DataManager.insertTerm(this, "Example Term 1", "2020-01-01", "2020-06-30", 1);
        Uri term2Uri = DataManager.insertTerm(this, "Example Term 2", "2020-07-01", "2020-12-31", 0);
        Uri term3Uri = DataManager.insertTerm(this, "Example Term 3", "2021-01-01", "2021-06-30", 0);
        Uri term4Uri = DataManager.insertTerm(this, "Example Term 4", "2021-07-01", "2021-12-31", 0);
        Uri term5Uri = DataManager.insertTerm(this, "Example Term 5", "2022-01-01", "2022-06-30", 0);
        Uri term6Uri = DataManager.insertTerm(this, "Example Term 6", "2022-07-01", "2022-12-31", 0);
        Uri term7Uri = DataManager.insertTerm(this, "Example Term 7", "2023-01-01", "2023-06-30", 0);
        Uri term8Uri = DataManager.insertTerm(this, "Example Term 8", "2023-07-01", "2023-12-31", 0);

        Uri course1Uri = DataManager.insertCourse(this, Long.parseLong(Objects.requireNonNull(term1Uri.getLastPathSegment())),
                "CXX1: Example Course #1", "2020-01-01", "2020-02-01",
                "JimmyJoe Bob", "(945) 577-1243", "jimmyjoe.bob@wgu.edu",
                CourseStatus.IN_PROGRESS);

        DataManager.insertCourse(this, Long.parseLong(term1Uri.getLastPathSegment()),
                "CXX2: Example Course #2", "2020-02-01", "2020-03-01",
                "Sam Jones", "516-546-4458", "sam.jones@wgu.edu",
                CourseStatus.PLANNED);

        DataManager.insertCourse(this, Long.parseLong(term1Uri.getLastPathSegment()),
                "CXX3: Example Course #3", "2020-03-01", "2020-06-30",
                "Dale Allen", "", "dale.allen@wgu.edu",
                CourseStatus.PLANNED);

        DataManager.insertCourseNote(this, Long.parseLong(Objects.requireNonNull(course1Uri.getLastPathSegment())),
                "This is a short test note");

        DataManager.insertCourseNote(this, Long.parseLong(course1Uri.getLastPathSegment()),
                getString(R.string.long_test_note));

        Uri ass1Uri = DataManager.insertAssessment(this, Long.parseLong(course1Uri.getLastPathSegment()), "ABC1", "Example Assessment #1",
                "Example Assessment #1 Description", "2020-09-01 3:30 PM");

        Uri ass2Uri = DataManager.insertAssessment(this, Long.parseLong(course1Uri.getLastPathSegment()), "XYZ2", "Example Assessment #2",
                "Example Assessment #2 Description",  "2020-09-01 11:00 AM");

        DataManager.insertAssessmentNote(this, Long.parseLong(Objects.requireNonNull(ass1Uri.getLastPathSegment())),
                "Test assessment note 1");

        DataManager.insertAssessmentNote(this, Long.parseLong(ass1Uri.getLastPathSegment()),
                "Test assessment note 2");

        DataManager.insertAssessmentNote(this, Long.parseLong(Objects.requireNonNull(ass2Uri.getLastPathSegment())),
                "Test assessment note 3");

        DataManager.insertAssessmentNote(this, Long.parseLong(ass2Uri.getLastPathSegment()),
                "Test assessment note 4");

        DataManager.insertAssessmentNote(this, Long.parseLong(ass2Uri.getLastPathSegment()),
                "Test assessment note 5");

        restartLoader();
        return true;
    }

    private boolean deleteAllTerms() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {
                if (button == DialogInterface.BUTTON_POSITIVE) {
                    getContentResolver().delete(DataProvider.TERMS_URI, null, null);
                    getContentResolver().delete(DataProvider.COURSES_URI, null, null);
                    getContentResolver().delete(DataProvider.COURSE_NOTES_URI, null, null);
                    getContentResolver().delete(DataProvider.ASSESSMENTS_URI, null, null);
                    getContentResolver().delete(DataProvider.ASSESSMENT_NOTES_URI, null, null);
                    restartLoader();
                    Toast.makeText(TermListActivity.this, getString(R.string.all_terms_deleted), Toast.LENGTH_SHORT).show();
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirm_delete_all_terms))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
        return true;
    }

    private boolean createTestAlarm() {
        long time = new GregorianCalendar().getTimeInMillis() + 5000;

        Intent intent = new Intent(this, AlarmHandler.class);
        intent.putExtra("title", "Test Alarm");
        intent.putExtra("text", "This is a test alarm.");

        android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(android.app.AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_ONE_SHOT));
        Toast.makeText(this, getString(R.string.test_alarm), Toast.LENGTH_SHORT).show();
        return true;
    }

    public void openNewTermEditor(View view) {
        Intent intent = new Intent(this, TermEditorActivity.class);
        startActivityForResult(intent, TERM_EDITOR_ACTIVITY_CODE);
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DataProvider.TERMS_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}

