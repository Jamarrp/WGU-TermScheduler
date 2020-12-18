package Assessments.AssessmentActivities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jamarp.wgu_termscheduler.R;

import java.util.Objects;

import Assessments.Assessment;
import Database.DataManager;
import Database.DataProvider;
import Utilities.AlarmHandler;
import Utilities.DateManager;

public class AssessmentDetailActivity extends AppCompatActivity {

    private static final int ASSESSMENT_EDITOR_ACTIVITY_CODE = 11111;
    private static final int ASSESSMENT_NOTE_LIST_ACTIVITY_CODE = 22222;

    private long assessmentId;
    private Assessment assessment;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AssessmentDetailActivity.this, AssessmentEditorActivity.class);
                Uri uri = Uri.parse(DataProvider.ASSESSMENTS_URI + "/" + assessmentId);
                intent.putExtra(DataProvider.ASSESSMENT_CONTENT_TYPE, uri);
                startActivityForResult(intent, ASSESSMENT_EDITOR_ACTIVITY_CODE);
            }
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        loadAssessment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_assessment_detail, menu);
        this.menu = menu;
        showMenuOptions();
        return true;
    }

    public void openAssessmentNotesList(View view) {
        Intent intent = new Intent(AssessmentDetailActivity.this, AssessmentNoteListActivity.class);
        Uri uri = Uri.parse(DataProvider.ASSESSMENTS_URI + "/" + assessmentId);
        intent.putExtra(DataProvider.ASSESSMENT_CONTENT_TYPE, uri);
        startActivityForResult(intent, ASSESSMENT_NOTE_LIST_ACTIVITY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadAssessment();
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadAssessment() {
        Uri assessmentUri = getIntent().getParcelableExtra(DataProvider.ASSESSMENT_CONTENT_TYPE);
        assessmentId = Long.parseLong(Objects.requireNonNull(assessmentUri.getLastPathSegment()));
        assessment = DataManager.getAssessment(this, assessmentId);
        TextView assessmentTitle = (TextView) findViewById(R.id.assessmentTitle);
        TextView assessmentDatetime = (TextView) findViewById(R.id.assessmentDateTime);
        TextView assessmentDescription = (TextView) findViewById(R.id.assessmentDescription);
        assessmentTitle.setText(assessment.code + ": " + assessment.name);
        assessmentDescription.setText(assessment.description);
        assessmentDatetime.setText(assessment.datetime);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_delete_assessment:
                return deleteAssessment();
            case R.id.action_enable_notifications:
                return enableNotifications();
            case R.id.action_disable_notifications:
                return disableNotifications();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean deleteAssessment() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {
                if (button == DialogInterface.BUTTON_POSITIVE) {
                    DataManager.deleteAssessment(AssessmentDetailActivity.this, assessmentId);
                    setResult(RESULT_OK);
                    finish();
                    Toast.makeText(AssessmentDetailActivity.this, getString(R.string.assessment_deleted), Toast.LENGTH_SHORT).show();
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_delete_assessment)
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
        return true;
    }

    private boolean enableNotifications() {
        long now = DateManager.todayLong();

        if (now <= DateManager.getDateTimestamp(assessment.datetime)) {
            AlarmHandler.scheduleCourseAlarm(getApplicationContext(), assessmentId, DateManager.getDateTimestamp(assessment.datetime),
                    "Assessment start date", assessment.name + " begins on " + assessment.datetime);
        }

        assessment.notifications = 1;
        assessment.saveChanges(this);
        showMenuOptions();
        return true;
    }

    private boolean disableNotifications() {
        assessment.notifications = 0;
        assessment.saveChanges(this);
        showMenuOptions();
        return true;
    }

    private void showMenuOptions() {
        menu.findItem(R.id.action_enable_notifications).setVisible(true);
        menu.findItem(R.id.action_disable_notifications).setVisible(true);

        if (assessment.notifications == 1) {
            menu.findItem(R.id.action_enable_notifications).setVisible(false);
        }
        else {
            menu.findItem(R.id.action_disable_notifications).setVisible(false);
        }
    }
}
