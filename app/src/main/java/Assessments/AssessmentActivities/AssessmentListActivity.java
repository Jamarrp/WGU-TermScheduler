package Assessments.AssessmentActivities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jamarp.wgu_termscheduler.R;

import java.util.Objects;

import Database.DataHelper;
import Database.DataProvider;

public class AssessmentListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ASSESSMENT_DETAIL_ACTIVITY_CODE = 11111;
    private static final int ASSESSMENT_EDITOR_ACTIVITY_CODE = 22222;

    private CursorAdapter cursorAdapter;
    private long courseId;
    private Uri courseUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        courseUri = getIntent().getParcelableExtra(DataProvider.COURSE_CONTENT_TYPE);
        courseId = Long.parseLong(Objects.requireNonNull(courseUri.getLastPathSegment()));
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AssessmentListActivity.this, AssessmentEditorActivity.class);
                intent.putExtra(DataProvider.COURSE_CONTENT_TYPE, courseUri);
                startActivityForResult(intent, ASSESSMENT_EDITOR_ACTIVITY_CODE);
            }
        });
        bindAssessmentList();
        getLoaderManager().initLoader(0, null, this);
    }

    protected void bindAssessmentList() {
        String[] from = {DataHelper.ASSESSMENT_CODE, DataHelper.ASSESSMENT_NAME, DataHelper.ASSESSMENT_DATETIME};
        int[] to = {R.id.assessmentCode, R.id.assessmentName, R.id.assessmentDateTime};
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.list_item_assessment, null, from, to, 0);
        DataProvider database = new DataProvider();
        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AssessmentListActivity.this, AssessmentDetailActivity.class);
                Uri uri = Uri.parse(DataProvider.ASSESSMENTS_URI + "/" + id);
                intent.putExtra(DataProvider.ASSESSMENT_CONTENT_TYPE, uri);
                startActivityForResult(intent, ASSESSMENT_DETAIL_ACTIVITY_CODE);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DataProvider.ASSESSMENTS_URI, DataHelper.ASSESSMENTS_COLUMNS,
                DataHelper.ASSESSMENT_COURSE_ID + " = " + this.courseId, null, null);
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
        restartLoader();
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }
}

