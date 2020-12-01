package Assessments.AssessmentActivities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.jamarp.wgu_termscheduler.R;

import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;

import Assessments.Assessment;
import Database.DataManager;
import Database.DataProvider;
import Utilities.DateManager;

public class AssessmentEditorActivity extends AppCompatActivity implements View.OnClickListener {

    private Assessment assessment;
    private long courseId;
    private EditText assessmentCode;
    private EditText assessmentName;
    private EditText assessmentDescription;
    private EditText assessmentDateTime;
    private DatePickerDialog assessmentDateDialog;
    private TimePickerDialog assessmentTimeDialog;
    private String action;

// Wrong view cast
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        assessmentCode = (EditText) findViewById(R.id.assessmentCode);
        assessmentName = (EditText) findViewById(R.id.assessmentName);
        assessmentDescription = (EditText) findViewById(R.id.assessmentDescription);
        assessmentDateTime = (EditText) findViewById(R.id.assessmentDateTime);

        Uri assessmentUri = getIntent().getParcelableExtra(DataProvider.ASSESSMENT_CONTENT_TYPE);
        if (assessmentUri == null) {
            setTitle(getString(R.string.new_assessment));
            action = Intent.ACTION_INSERT;
            Uri courseUri = getIntent().getParcelableExtra(DataProvider.COURSE_CONTENT_TYPE);
            courseId = Long.parseLong(Objects.requireNonNull(courseUri.getLastPathSegment()));
            assessment = new Assessment();
        }
        else {
            setTitle(getString(R.string.edit_assessment));
            action = Intent.ACTION_EDIT;
            long assessmentId = Long.parseLong(Objects.requireNonNull(assessmentUri.getLastPathSegment()));
            assessment = DataManager.getAssessment(this, assessmentId);
            courseId = assessment.courseId;
            populateAssessmentForm();
        }
        setupDateAndTimePickers();
    }

    private void populateAssessmentForm() {
        if (assessment != null) {
            assessmentCode.setText(assessment.code);
            assessmentName.setText(assessment.name);
            assessmentDescription.setText(assessment.description);
            assessmentDateTime.setText(assessment.datetime);
        }
    }

    private void setupDateAndTimePickers() {
        assessmentDateTime.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        assessmentDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar2 = Calendar.getInstance();
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                assessmentDateTime.setText(DateManager.dateFormat.format(newDate.getTime()));
                assessmentTimeDialog = new TimePickerDialog(AssessmentEditorActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String AM_PM;
                        if (hourOfDay < 12) {
                            AM_PM = "AM";
                        }
                        else {
                            AM_PM = "PM";
                        }
                        if (hourOfDay > 12) {
                            hourOfDay = hourOfDay - 12;
                        }
                        if (hourOfDay == 0) {
                            hourOfDay = 12;
                        }
                        String minuteString = Integer.toString(minute);
                        if (minute < 10) {
                            minuteString = "0" + minuteString;
                        }
                        String datetime = assessmentDateTime.getText().toString() + " " + hourOfDay + ":" + minuteString
                                + " " + AM_PM + " " + TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT);
                        assessmentDateTime.setText(datetime);
                    }
                }, calendar2.get(Calendar.HOUR_OF_DAY), calendar2.get(Calendar.MINUTE), false);
                assessmentTimeDialog.show();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        assessmentDateTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    assessmentDateDialog.show();
                }
            }
        });
    }

    public void saveAssessment(View view) {
        getValues();
        switch (action) {
            case Intent.ACTION_INSERT:
                DataManager.insertAssessment(this, courseId, assessment.code, assessment.name, assessment.description,
                        assessment.datetime);
                setResult(RESULT_OK);
                finish();
                break;
            case Intent.ACTION_EDIT:
                assessment.saveChanges(this);
                setResult(RESULT_OK);
                finish();
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private void getValues() {
        assessment.code = assessmentCode.getText().toString().trim();
        assessment.name = assessmentName.getText().toString().trim();
        assessment.description = assessmentDescription.getText().toString().trim();
        assessment.datetime = assessmentDateTime.getText().toString().trim();
    }

    @Override
    public void onClick(View view) {
        if (view == assessmentDateTime) {
            assessmentDateDialog.show();
        }
    }
}

