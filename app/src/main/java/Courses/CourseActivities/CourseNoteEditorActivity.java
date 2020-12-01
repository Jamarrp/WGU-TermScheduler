package Courses.CourseActivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.jamarp.wgu_termscheduler.R;

import java.util.Objects;

import Courses.CourseNote;
import Database.DataManager;
import Database.DataProvider;

public class CourseNoteEditorActivity extends AppCompatActivity {

    private long courseId;
    private CourseNote courseNote;
    private EditText noteTextField;
    private String action;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_note_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        noteTextField = (EditText) findViewById(R.id.courseNoteText);
        Uri courseNoteUri = getIntent().getParcelableExtra(DataProvider.COURSE_NOTE_CONTENT_TYPE);

        if (courseNoteUri == null) {
            setTitle(getString(R.string.enter_new_note));
            Uri courseUri = getIntent().getParcelableExtra(DataProvider.COURSE_CONTENT_TYPE);
            courseId = Long.parseLong(Objects.requireNonNull(courseUri.getLastPathSegment()));
            action = Intent.ACTION_INSERT;
        }
        else {
            setTitle(getString(R.string.edit_note));
            long courseNoteId = Long.parseLong(Objects.requireNonNull(courseNoteUri.getLastPathSegment()));
            courseNote = DataManager.getCourseNote(this, courseNoteId);
            courseId = courseNote.courseId;
            noteTextField.setText(courseNote.text);
            action = Intent.ACTION_EDIT;
        }
    }

    public void saveCourseNote(View view) {
        if (action.equals(Intent.ACTION_INSERT)) {
            DataManager.insertCourseNote(this, courseId, noteTextField.getText().toString().trim());
            setResult(RESULT_OK);
            finish();
        }
        if (action.equals(Intent.ACTION_EDIT)) {
            courseNote.text = noteTextField.getText().toString().trim();
            courseNote.saveChanges(this);
            setResult(RESULT_OK);
            finish();
        }
    }
}