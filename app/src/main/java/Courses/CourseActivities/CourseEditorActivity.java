package Courses.CourseActivities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.jamarp.wgu_termscheduler.R;

import java.util.Calendar;
import java.util.Objects;

import Courses.Course;
import Courses.CourseStatus;
import Database.DataManager;
import Database.DataProvider;
import Utilities.DateManager;

public class CourseEditorActivity extends AppCompatActivity implements View.OnClickListener {

    private String action;
    private Uri termUri;
    private Course course;

    private EditText courseNameField;
    private EditText courseStartField;
    private EditText courseEndField;
    private EditText courseMentorField;
    private EditText courseMentorPhoneField;
    private EditText courseMentorEmailField;
    private DatePickerDialog courseStartDateDialog;
    private DatePickerDialog courseEndDateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        findViews();
        Intent intent = getIntent();
        Uri courseUri = intent.getParcelableExtra(DataProvider.COURSE_CONTENT_TYPE);
        termUri = intent.getParcelableExtra(DataProvider.TERM_CONTENT_TYPE);

        if (courseUri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.add_new_course));
        } else {
            action = Intent.ACTION_EDIT;
            setTitle(getString(R.string.edit_course_title));
            long classId = Long.parseLong(Objects.requireNonNull(courseUri.getLastPathSegment()));
            course = DataManager.getCourse(this, classId);
            fillCourseForm(course);
        }
        setupDatePickers();
    }

    private void findViews() {
        courseNameField = (EditText) findViewById(R.id.courseNameField);
        courseStartField = (EditText) findViewById(R.id.courseStartField);
        courseEndField = (EditText) findViewById(R.id.courseEndField);
        courseMentorField = (EditText) findViewById(R.id.courseMentorField);
        courseMentorPhoneField = (EditText) findViewById(R.id.courseMentorPhoneField);
        courseMentorEmailField = (EditText) findViewById(R.id.courseMentorEmailField);
    }

    private void fillCourseForm(Course course) {
        courseNameField.setText(course.name);
        courseStartField.setText(course.start);
        courseEndField.setText(course.end);
        courseMentorField.setText(course.mentor);
        courseMentorPhoneField.setText(course.mentorPhone);
        courseMentorEmailField.setText(course.mentorEmail);
    }

    private void setupDatePickers() {
        courseStartField.setOnClickListener(this);
        courseEndField.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        courseStartDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                courseStartField.setText(DateManager.dateFormat.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        courseEndDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                courseEndField.setText(DateManager.dateFormat.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        courseStartField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    courseStartDateDialog.show();
                }
            }
        });

        courseEndField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    courseEndDateDialog.show();
                }
            }
        });
    }

    public void saveCourse(View view) {
        if (action == Intent.ACTION_INSERT) {
            long termId = Long.parseLong(Objects.requireNonNull(termUri.getLastPathSegment()));
            DataManager.insertCourse(this, termId,
                    courseNameField.getText().toString().trim(),
                    courseStartField.getText().toString().trim(),
                    courseEndField.getText().toString().trim(),
                    courseMentorField.getText().toString().trim(),
                    courseMentorPhoneField.getText().toString().trim(),
                    courseMentorEmailField.getText().toString().trim(),
                    CourseStatus.PLANNED);
        } else if (action == Intent.ACTION_EDIT) {
            course.name = courseNameField.getText().toString().trim();
            course.start = courseStartField.getText().toString().trim();
            course.end = courseEndField.getText().toString().trim();
            course.mentor = courseMentorField.getText().toString().trim();
            course.mentorPhone = courseMentorPhoneField.getText().toString().trim();
            course.mentorEmail = courseMentorEmailField.getText().toString().trim();
            course.saveChanges(this);
            setResult(RESULT_OK);
        }
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view == courseStartField) {
            courseStartDateDialog.show();
        }
        if (view == courseEndField) {
            courseEndDateDialog.show();
        }
    }
}
