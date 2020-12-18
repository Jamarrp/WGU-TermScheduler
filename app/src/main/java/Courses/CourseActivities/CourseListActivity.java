package Courses.CourseActivities;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jamarp.wgu_termscheduler.R;

import java.util.Objects;

import Database.DataHelper;
import Database.DataManager;
import Database.DataProvider;
import Terms.Term;

public class CourseListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int COURSE_DETAIL_ACTIVITY_CODE = 11111;
    private static final int COURSE_EDITOR_ACTIVITY_CODE = 22222;

    private long termId;
    private Uri termUri;
    private MySimpleCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseListActivity.this, CourseEditorActivity.class);
                intent.putExtra(DataProvider.TERM_CONTENT_TYPE, termUri);
                startActivityForResult(intent, COURSE_EDITOR_ACTIVITY_CODE);
            }
        });

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        termUri = intent.getParcelableExtra(DataProvider.TERM_CONTENT_TYPE);
        loadTermData();
        bindClassList();
        getLoaderManager().initLoader(0, null, this);
    }

    private void loadTermData() {
        if (termUri == null) {
            setResult(RESULT_CANCELED);
            finish();
        }
        else {
            termId = Long.parseLong(Objects.requireNonNull(termUri.getLastPathSegment()));
            Term term = DataManager.getTerm(this, termId);
            setTitle(getString(R.string.courses));
        }
    }

    private void bindClassList() {
        String[] from = {DataHelper.COURSE_NAME, DataHelper.COURSE_START, DataHelper.COURSE_END, DataHelper.COURSE_STATUS};
        int[] to = {R.id.courseName, R.id.courseStartDate, R.id.courseEndDate, R.id.courseStatus};

        cursorAdapter = new MySimpleCursorAdapter(this, R.layout.list_item_course, null, from, to);
        DataProvider database = new DataProvider();

        ListView list = findViewById(android.R.id.list);

        list.setAdapter(cursorAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CourseListActivity.this, CourseDetailActivity.class);
                Uri uri = Uri.parse(DataProvider.COURSES_URI + "/" + id);
                intent.putExtra(DataProvider.COURSE_CONTENT_TYPE, uri);
                startActivityForResult(intent, COURSE_DETAIL_ACTIVITY_CODE);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DataProvider.COURSES_URI, DataHelper.COURSES_COLUMNS, DataHelper.COURSE_TERM_ID + " = " + this.termId, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadTermData();
        restartLoader();
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    public static class MySimpleCursorAdapter extends SimpleCursorAdapter {

        public MySimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void setViewText(TextView view, String text) {
            if (view.getId() == R.id.courseStatus) {
                String status = "";
                switch (text) {
                    case "PLANNED":
                        status = "Plan to Take";
                        break;
                    case "IN_PROGRESS":
                        status = "In Progress";
                        break;
                    case "COMPLETED":
                        status = "Completed";
                        break;
                    case "DROPPED":
                        status = "Dropped";
                        break;
                }
                view.setText("Status: " + status);
            }
            else {
                view.setText(text);
            }
        }
    }
}
