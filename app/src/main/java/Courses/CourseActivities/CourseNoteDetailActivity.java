package Courses.CourseActivities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import com.jamarp.wgu_termscheduler.R;

import java.util.Objects;

import Courses.Course;
import Courses.CourseNote;
import Database.DataManager;
import Database.DataProvider;

public class CourseNoteDetailActivity extends AppCompatActivity {

    private static final int COURSE_NOTE_EDITOR_ACTIVITY_CODE = 11111;
    private static final int CAMERA_ACTIVITY_CODE = 22222;

    private long courseNoteId;
    private Uri courseNoteUri;
    private TextView courseNoteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_note_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        courseNoteText = findViewById(R.id.courseNoteText);
        courseNoteUri = getIntent().getParcelableExtra(DataProvider.COURSE_NOTE_CONTENT_TYPE);

        if (courseNoteUri != null) {
            courseNoteId = Long.parseLong(Objects.requireNonNull(courseNoteUri.getLastPathSegment()));
            setTitle(getString(R.string.view_course_note));
            loadNote();
        }
    }

    private void loadNote() {
        CourseNote courseNote = DataManager.getCourseNote(this, courseNoteId);
        courseNoteText.setText(courseNote.text);
        courseNoteText.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadNote();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course_note_detail, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        CourseNote courseNote = DataManager.getCourseNote(this, courseNoteId);
        Course course = DataManager.getCourse(this, courseNote.courseId);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareSubject = course.name + ": Course Note";
        String shareBody = courseNote.text;
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        shareActionProvider.setShareIntent(shareIntent);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete_course_note) {
            return deleteCourseNote();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean deleteCourseNote() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {
                if (button == DialogInterface.BUTTON_POSITIVE) {
                    DataManager.deleteCourseNote(CourseNoteDetailActivity.this, courseNoteId);
                    setResult(RESULT_OK);
                    finish();
                    Toast.makeText(CourseNoteDetailActivity.this, getString(R.string.note_deleted), Toast.LENGTH_SHORT).show();
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_delete_note)
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
        return true;
    }

    public void editNote(View view) {
        Intent intent = new Intent(this, CourseNoteEditorActivity.class);
        intent.putExtra(DataProvider.COURSE_NOTE_CONTENT_TYPE, courseNoteUri);
        startActivityForResult(intent, COURSE_NOTE_EDITOR_ACTIVITY_CODE);
    }
}
