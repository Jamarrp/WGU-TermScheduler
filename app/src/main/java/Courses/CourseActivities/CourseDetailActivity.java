package Courses.CourseActivities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import Assessments.AssessmentActivities.AssessmentListActivity;

import Courses.Course;
import Courses.CourseStatus;

import Database.DataManager;
import Database.DataProvider;

import Utilities.AlarmHandler;
import Utilities.DateManager;

import com.jamarp.wgu_termscheduler.R;

import java.util.Objects;

public class CourseDetailActivity extends AppCompatActivity {
    private static final int COURSE_NOTE_LIST_ACTIVITY_CODE = 11111;
    private static final int ASSESSMENT_LIST_ACTIVITY_CODE = 22222;
    private static final int COURSE_EDITOR_ACTIVITY_CODE = 33333;

    private Menu menu;
    private long courseId;
    private Course course;

    private TextView courseName;
    private TextView startDate;
    private TextView endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Uri courseUri = intent.getParcelableExtra(DataProvider.COURSE_CONTENT_TYPE);
        courseId = Long.parseLong(Objects.requireNonNull(courseUri.getLastPathSegment()));
        course = DataManager.getCourse(this, courseId);

        setStatusLabel();
        findElements();
    }

    //@SuppressLint("SetTextI18n")
    private void setStatusLabel() {
        TextView courseStatus = findViewById(R.id.courseStatus);
        String status = "";
        switch (course.status.toString()) {
            case "PLANNED":
                status = "Planned to Take";
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
        courseStatus.setText(status);
    }

    private void findElements() {
        courseName = findViewById(R.id.courseName);
        courseName.setText(course.name);

        startDate = findViewById(R.id.courseStartDate);
        startDate.setText(course.start);

        endDate = findViewById(R.id.courseEndDate);
        endDate.setText(course.end);
    }

    private void updateElements() {
        course = DataManager.getCourse(this, courseId);
        courseName.setText(course.name);
        startDate.setText(course.start);
        endDate.setText(course.end);
    }

    public void openCourseNotes(View view) {
        Intent intent = new Intent(CourseDetailActivity.this, CourseNoteListActivity.class);
        Uri uri = Uri.parse(DataProvider.COURSES_URI + "/" + courseId);
        intent.putExtra(DataProvider.COURSE_CONTENT_TYPE, uri);
        startActivityForResult(intent, COURSE_NOTE_LIST_ACTIVITY_CODE);
    }

    public void openCourseAssessments(View view) {
        Intent intent = new Intent(CourseDetailActivity.this, AssessmentListActivity.class);
        Uri uri = Uri.parse(DataProvider.COURSES_URI + "/" + courseId);
        intent.putExtra(DataProvider.COURSE_CONTENT_TYPE, uri);
        startActivityForResult(intent, ASSESSMENT_LIST_ACTIVITY_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course_detail, menu);
        this.menu = menu;
        showMenuOptions();
        return true;
    }

    private void showMenuOptions() {
        SharedPreferences sp = getSharedPreferences(AlarmHandler.courseAlarmFile, Context.MODE_PRIVATE);
        menu.findItem(R.id.action_enable_notifications).setVisible(true);
        menu.findItem(R.id.action_disable_notifications).setVisible(true);

        if (course.notifications == 1) {
            menu.findItem(R.id.action_enable_notifications).setVisible(false);
        }
        else {
            menu.findItem(R.id.action_disable_notifications).setVisible(false);
        }

        if (course.status == null) {
            course.status = CourseStatus.PLANNED;
            course.saveChanges(this);
        }

        switch (course.status.toString()) {
            case "PLANNED":
                menu.findItem(R.id.action_drop_course).setVisible(false);
                menu.findItem(R.id.action_start_course).setVisible(true);
                menu.findItem(R.id.action_mark_course_completed).setVisible(false);
                break;
            case "IN_PROGRESS":
                menu.findItem(R.id.action_drop_course).setVisible(true);
                menu.findItem(R.id.action_start_course).setVisible(false);
                menu.findItem(R.id.action_mark_course_completed).setVisible(true);
                break;
            case "COMPLETED":
            case "DROPPED":
                menu.findItem(R.id.action_drop_course).setVisible(false);
                menu.findItem(R.id.action_start_course).setVisible(false);
                menu.findItem(R.id.action_mark_course_completed).setVisible(false);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit_course:
                return editCourse();
            case R.id.action_delete_course:
                return deleteCourse();
            case R.id.action_enable_notifications:
                return enableNotifications();
            case R.id.action_disable_notifications:
                return disableNotifications();
            case R.id.action_drop_course:
                return dropCourse();
            case R.id.action_start_course:
                return startCourse();
            case R.id.action_mark_course_completed:
                return markCourseCompleted();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean editCourse() {
        Intent intent = new Intent(this, CourseEditorActivity.class);
        Uri uri = Uri.parse(DataProvider.COURSES_URI + "/" + course.courseId);
        intent.putExtra(DataProvider.COURSE_CONTENT_TYPE, uri);
        startActivityForResult(intent, COURSE_EDITOR_ACTIVITY_CODE);
        return true;
    }

    private boolean deleteCourse() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {
                if (button == DialogInterface.BUTTON_POSITIVE) {
                    DataManager.deleteCourse(CourseDetailActivity.this, courseId);
                    setResult(RESULT_OK);
                    finish();
                    Toast.makeText(CourseDetailActivity.this, getString(R.string.course_deleted), Toast.LENGTH_SHORT).show();
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_delete_course)
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
        return true;
    }

    public boolean activateCourseStartAlert(View view) {
        System.out.println("1. Enable course notifications button pressed.");

        long now = DateManager.todayLong();
        System.out.println("2. NOW: " + now);

        if (now <= DateManager.getDateTimestamp(course.start)) {
            System.out.println("3. if NOW: " + now + "<= Course Start: " + DateManager.getDateTimestamp(course.start));
            AlarmHandler.scheduleCourseAlarm(getApplicationContext(), courseId, DateManager.getDateTimestamp(course.start),
                    "Course start date", course.name + " begins on " + course.start);
        }
        course.notifications = 1;
        course.saveChanges(this);
        showMenuOptions();
        return true;
    }

    public boolean activateCourseEndAlert(View view) {
        long now = DateManager.todayLong();

        if (now <= DateManager.getDateTimestamp(course.end)) {
            AlarmHandler.scheduleCourseAlarm(getApplicationContext(), courseId, DateManager.getDateTimestamp(course.end),
                    "Course end date", course.name + " ends on " + course.end);
        }
        course.notifications = 1;
        course.saveChanges(this);
        showMenuOptions();
        return true;
    }

    private boolean enableNotifications() {
        long now = DateManager.todayLong();

        if (now <= DateManager.getDateTimestamp(course.start)) {
            AlarmHandler.scheduleCourseAlarm(getApplicationContext(), courseId, DateManager.getDateTimestamp(course.start),
                    "Course start date", course.name + " begins on " + course.start);
        } else if (now <= DateManager.getDateTimestamp(course.end)) {
            AlarmHandler.scheduleCourseAlarm(getApplicationContext(), courseId, DateManager.getDateTimestamp(course.end),
                    "Course end date", course.name + " ends on " + course.end);
        }
        course.notifications = 1;
        course.saveChanges(this);
        showMenuOptions();
        return true;
    }

    private boolean disableNotifications() {
        course.notifications = 0;
        course.saveChanges(this);
        showMenuOptions();
        return true;
    }

    private boolean dropCourse() {
        course.status = CourseStatus.DROPPED;
        course.saveChanges(this);
        setStatusLabel();
        showMenuOptions();
        return true;
    }

    private boolean startCourse() {
        course.status = CourseStatus.IN_PROGRESS;
        course.saveChanges(this);
        setStatusLabel();
        showMenuOptions();
        return true;
    }

    private boolean markCourseCompleted() {
        course.status = CourseStatus.COMPLETED;
        course.saveChanges(this);
        setStatusLabel();
        showMenuOptions();
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            updateElements();
        }
    }
}
