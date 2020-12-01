package Terms.TermActivities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Objects;

import Courses.CourseActivities.CourseListActivity;
import Database.DataHelper;
import Database.DataManager;
import Database.DataProvider;
import Terms.Term;

import com.jamarp.wgu_termscheduler.R;


public class TermDetailActivity extends AppCompatActivity {
    private static final int TERM_EDITOR_ACTIVITY_CODE = 11111;
    private static final int COURSE_LIST_ACTIVITY_CODE = 22222;

    private Uri termUri;
    private Term term;

    private CursorAdapter cursorAdapter;

    private TextView termTitle;
    private TextView termStartDate;
    private TextView termEndDate;
    private Menu menu;

    private long termId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        termUri = intent.getParcelableExtra(DataProvider.TERM_CONTENT_TYPE);
        findElements();
        loadTermData();
    }

    private void findElements() {
        termTitle = (TextView) findViewById(R.id.termName);
        termStartDate = (TextView) findViewById(R.id.startDate);
        termEndDate = (TextView) findViewById(R.id.endDate);
    }

    private void loadTermData() {
        if (termUri == null) {
            setResult(RESULT_CANCELED);
            finish();
        }
        else {
            termId = Long.parseLong(Objects.requireNonNull(termUri.getLastPathSegment()));
            term = DataManager.getTerm(this, termId);

            setTitle(getString(R.string.view_term));
            termTitle.setText(term.name);
            termStartDate.setText(term.start);
            termEndDate.setText(term.end);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_term_detail, menu);
        this.menu = menu;
        showAppropriateMenuOptions();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_mark_term_active:
                return markTermActive();
            case R.id.action_edit_term:
                Intent intent = new Intent(this, TermEditorActivity.class);
                Uri uri = Uri.parse(DataProvider.TERMS_URI + "/" + term.termId);
                intent.putExtra(DataProvider.TERM_CONTENT_TYPE, uri);
                startActivityForResult(intent, TERM_EDITOR_ACTIVITY_CODE);
                break;
            case R.id.action_delete_term:
                return deleteTerm();
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private boolean markTermActive() {
        @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(DataProvider.TERMS_URI, null, null, null, null);
        ArrayList<Term> termList = new ArrayList<>();
        assert cursor != null;
        while (cursor.moveToNext()) {
            termList.add(DataManager.getTerm(this, cursor.getLong(cursor.getColumnIndex(DataHelper.TERMS_TABLE_ID))));
        }

        for (Term term : termList) {
            term.deactivate(this);
        }

        this.term.activate(this);
        showAppropriateMenuOptions();

        Toast.makeText(TermDetailActivity.this, getString(R.string.term_marked_active), Toast.LENGTH_SHORT).show();
        return true;
    }

    private boolean deleteTerm() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {
                if (button == DialogInterface.BUTTON_POSITIVE) {
                    long classCount = term.getClassCount(TermDetailActivity.this);
                    if (classCount == 0) {
                        getContentResolver().delete(DataProvider.TERMS_URI, DataHelper.TERMS_TABLE_ID + " = " + termId, null);

                        Toast.makeText(TermDetailActivity.this, getString(R.string.term_deleted), Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                    else {
                        Toast.makeText(TermDetailActivity.this, getString(R.string.need_to_remove_courses), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_delete_term)
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
        return true;
    }

    public void showAppropriateMenuOptions() {
        if (term.active == 1) {
            menu.findItem(R.id.action_mark_term_active).setVisible(false);
        }
    }

    public void openCourseList(View view) {
        Intent intent = new Intent(this, CourseListActivity.class);
        intent.putExtra(DataProvider.TERM_CONTENT_TYPE, termUri);
        startActivityForResult(intent, COURSE_LIST_ACTIVITY_CODE);
    }
}
