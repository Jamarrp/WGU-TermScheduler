package Assessments.AssessmentActivities;

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

import Assessments.AssessmentNote;
import Database.DataManager;
import Database.DataProvider;

public class AssessmentNoteEditorActivity extends AppCompatActivity {

    private AssessmentNote assessmentNote;
    private long assessmentId;
    private EditText assessmentNoteTextField;
    private String action;

// Suppressed wrong view cast
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_note_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        assessmentNoteTextField = (EditText) findViewById(R.id.assessmentNoteText);
        Uri assessmentNoteUri = getIntent().getParcelableExtra(DataProvider.ASSESSMENT_NOTE_CONTENT_TYPE);
        if (assessmentNoteUri == null) {
            setTitle(R.string.enter_new_note);
            Uri assessmentUri = getIntent().getParcelableExtra(DataProvider.ASSESSMENT_CONTENT_TYPE);
            assessmentId = Long.parseLong(Objects.requireNonNull(assessmentUri.getLastPathSegment()));
            action = Intent.ACTION_INSERT;
        }
        else {
            setTitle(R.string.edit_note);
            long assessmentNoteId = Long.parseLong(Objects.requireNonNull(assessmentNoteUri.getLastPathSegment()));
            assessmentNote = DataManager.getAssessmentNote(this, assessmentNoteId);
            assessmentId = assessmentNote.assessmentID;
            assessmentNoteTextField.setText(assessmentNote.text);
            action = Intent.ACTION_EDIT;
        }
    }

    public void saveAssessmentNote(View view) {
        if (action.equals(Intent.ACTION_INSERT)) {
            DataManager.insertAssessmentNote(this, assessmentId, assessmentNoteTextField.getText().toString().trim());
            setResult(RESULT_OK);
            finish();
        }
        if (action.equals(Intent.ACTION_EDIT)) {
            assessmentNote.text = assessmentNoteTextField.getText().toString().trim();
            assessmentNote.saveChanges(this);
            setResult(RESULT_OK);
            finish();
        }
    }
}
