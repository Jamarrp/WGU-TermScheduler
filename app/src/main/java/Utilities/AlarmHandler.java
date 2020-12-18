package Utilities;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.jamarp.wgu_termscheduler.MainActivity;

import Assessments.Assessment;
import Assessments.AssessmentActivities.AssessmentDetailActivity;
import Courses.Course;
import Courses.CourseActivities.CourseDetailActivity;
import Database.DataManager;
import Database.DataProvider;

import com.jamarp.wgu_termscheduler.R;

public class AlarmHandler extends BroadcastReceiver {


    public static final String courseAlarmFile = "courseAlarms";
    public static final String assessmentAlarmFile = "assessmentAlarms";
    public static final String alarmFile = "alarmFile";
    public static final String nextAlarmField = "nextAlarmId";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("alarmLogTag", "onReceive");

        int notifyID = 1;
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "Notification";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

        String destination = intent.getStringExtra("destination");

        if (destination == null || destination.isEmpty()) {
            destination = "";
        }

        long id = intent.getLongExtra("id", 0);
        String alarmTitle = intent.getStringExtra("title");
        String alarmText = intent.getStringExtra("text");
        int nextAlarmId = intent.getIntExtra("nextAlarmId", getAndIncrementNextAlarmId(context));

        Log.i("alarmLogTag", "title " + alarmTitle);
        Log.i("alarmLogTag", "text " + alarmText);

        // Need to create another screen for course tracking.

        // Screenshot of progress tracking and copy of document.

        // Create a notification and set the notification channel.
        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(alarmTitle)
                .setContentText(alarmText)
                .setSmallIcon(R.drawable.twotone_schedule_24)
                .setChannelId(CHANNEL_ID);


        Intent resultIntent;
        Uri uri;
        SharedPreferences sharedPreferences;

        switch (destination) {
            case "course":
                Log.i("alarmLogTag", "course");

                Course course = DataManager.getCourse(context, id);
                if (course != null && course.notifications == 1) {
                    resultIntent = new Intent(context, CourseDetailActivity.class);
                    uri = Uri.parse(DataProvider.COURSES_URI + "/" + id);
                    resultIntent.putExtra(DataProvider.COURSE_CONTENT_TYPE, uri);
                }
                else {
                    return;
                }
                break;
            case "assessment":
                Log.i("alarmLogTag", "assignment");

                Assessment assessment = DataManager.getAssessment(context, id);
                if (assessment != null && assessment.notifications == 1) {
                    resultIntent = new Intent(context, AssessmentDetailActivity.class);
                    uri = Uri.parse(DataProvider.ASSESSMENTS_URI + "/" + id);
                    resultIntent.putExtra(DataProvider.ASSESSMENT_CONTENT_TYPE, uri);
                }
                else {
                    return;
                }
                break;
            default:
                Log.i("alarmLogTag", "default");

                resultIntent = new Intent(context, MainActivity.class);
                break;
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent).setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(mChannel);
        }
        // Issue the notification.
        mNotificationManager.notify(nextAlarmId , builder.build());

        Log.i("alarmLogTag", "end");

//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(nextAlarmId, builder.build());
    }

    public static boolean scheduleCourseAlarm(Context context, long id, long time, String title, String text) {

        Log.i("alarmLogTag", "set "+id);

        int nextAlarmId = getNextAlarmId(context);
        Intent intentAlarm = new Intent(context, AlarmHandler.class);
        intentAlarm.putExtra("id", id);
        intentAlarm.putExtra("title", title);
        intentAlarm.putExtra("text", text);
        intentAlarm.putExtra("destination", "course");
        intentAlarm.putExtra("nextAlarmId", nextAlarmId);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intentAlarm, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,  time, alarmIntent);

        SharedPreferences sp = context.getSharedPreferences(courseAlarmFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Long.toString(id), nextAlarmId);
        editor.apply();

        incrementNextAlarmId(context);
        return true;
    }

    public static boolean scheduleAssessmentAlarm(Context context, int id, long time, String tile, String text) {

        Log.i("alarmLogTag", "set "+ id);

        int nextAlarmId = getNextAlarmId(context);
        Intent intentAlarm = new Intent(context, AlarmHandler.class);
        intentAlarm.putExtra("id", id);
        intentAlarm.putExtra("title", tile);
        intentAlarm.putExtra("text", text);
        intentAlarm.putExtra("destination", "assessment");
        intentAlarm.putExtra("nextAlarmId", nextAlarmId);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intentAlarm, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,  time, alarmIntent);

        SharedPreferences sp = context.getSharedPreferences(assessmentAlarmFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Long.toString(id), nextAlarmId);
        editor.apply();

        incrementNextAlarmId(context);
        return true;
    }

    private static int getNextAlarmId(Context context) {
        SharedPreferences alarmPrefs;
        alarmPrefs = context.getSharedPreferences(alarmFile, Context.MODE_PRIVATE);
        int nextAlarmId = alarmPrefs.getInt(nextAlarmField, 1);
        return nextAlarmId;
    }

    private static void incrementNextAlarmId(Context context) {
        SharedPreferences alarmPrefs;
        alarmPrefs = context.getSharedPreferences(alarmFile, Context.MODE_PRIVATE);
        int nextAlarmId = alarmPrefs.getInt(nextAlarmField, 1);
        SharedPreferences.Editor alarmEditor = alarmPrefs.edit();
        alarmEditor.putInt(nextAlarmField, nextAlarmId + 1);
        alarmEditor.commit();
    }

    private static int getAndIncrementNextAlarmId(Context context) {
        int nextAlarmId = getNextAlarmId(context);
        incrementNextAlarmId(context);
        return nextAlarmId;
    }
}
