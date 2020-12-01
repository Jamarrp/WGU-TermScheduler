package com.jamarp.wgu_termscheduler;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import Database.DataHelper;
import Database.DataProvider;

import Terms.TermActivities.TermListActivity;
import Terms.TermActivities.TermDetailActivity;

public class MainActivity extends AppCompatActivity {

    private static final int TERM_DETAIL_ACTIVITY_CODE = 11111;
    private static final int TERM_LIST_ACTIVITY_CODE = 22222;

    public static final String CHANNEL_1_ID = "channel1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createNotificationChannels();

        setContentView(R.layout.activity_main);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel 1");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }

    public void openTermList(View view) {
        Intent intent = new Intent(this, TermListActivity.class);
        startActivityForResult(intent, TERM_LIST_ACTIVITY_CODE);
    }

    public void openCurrentTerm(View view) {
        @SuppressLint("Recycle") Cursor c = getContentResolver().query(DataProvider.TERMS_URI, null, DataHelper.TERM_ACTIVE
                + " =1", null, null);
        assert c != null;
        if (c.moveToNext()) {
            Intent intent = new Intent(this, TermDetailActivity.class);
            long id = c.getLong(c.getColumnIndex(DataHelper.TERMS_TABLE_ID));
            Uri uri = Uri.parse(DataProvider.TERMS_URI + "/" + id);
            intent.putExtra(DataProvider.TERM_CONTENT_TYPE, uri);
            startActivityForResult(intent, TERM_DETAIL_ACTIVITY_CODE);
            return;
        }
        Toast.makeText(this, getString(R.string.no_active_term_set),
                Toast.LENGTH_SHORT).show();
    }
}
