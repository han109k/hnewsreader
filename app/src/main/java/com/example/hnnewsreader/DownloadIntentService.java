package com.example.hnnewsreader;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadIntentService extends IntentService {

    private static final String TAG = "DownloadIntentService";

    public static final String PENDING_RESULT_EXTRA = "pending_result";
    public static final String URL_EXTRA = "url";

    public static final int RESULT_CODE = 0;
    public static final int ERROR_CODE = 2;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    public DownloadIntentService(String name) {
        super(TAG);
    }

    public DownloadIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        PendingIntent reply = intent.getParcelableExtra(PENDING_RESULT_EXTRA);

        try {
            try {
                String returnData = "";
                URL url = new URL(intent.getStringExtra(URL_EXTRA));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream stream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(stream);

                int data = inputStreamReader.read();
                while (data != -1) {

                    char currenData = (char) data;
                    returnData += currenData;
                    data = inputStreamReader.read();

                }

                Intent result = new Intent();
                result.putExtra(URL_EXTRA, returnData);

                reply.send(this, RESULT_CODE, result);
            } catch (Exception e) {
                reply.send(ERROR_CODE);
            }
        } catch (PendingIntent.CanceledException e) {
            Log.d(TAG, "reply cancelled", e);
        }
    }
}
