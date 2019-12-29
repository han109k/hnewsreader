package com.example.hnnewsreader;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private final int JSON_DOWNLOAD_REQUEST_CODE = 0;
    private final int ITEM_DOWNLOAD_REQUEST_CODE = 1;

    protected boolean refresh = false;
    protected int top30 = 9;     // total Hacker News stories that will be shown ( Only  urls || Stories not included such as: show HN, ask HN )
    protected int flak = 0;
    protected String hnAPI = "https://hacker-news.firebaseio.com/v0/";
    protected ArrayList<String> itemIds = new ArrayList<>();
    protected ArrayList<String> title = new ArrayList<>();
    protected ArrayList<String> urls = new ArrayList<>();

    RecyclerViewAdapter adapter = new RecyclerViewAdapter(title, urls, this);
    SwipeRefreshLayout mSwipeRefreshLayout;

    public void parseTitleAndContext() {

        try {

            for(int i = 0; i < top30; i++){

                PendingIntent pendingResult = createPendingResult(ITEM_DOWNLOAD_REQUEST_CODE, new Intent(), 0);
                Intent intent = new Intent(getApplicationContext(), DownloadIntentService.class);
                intent.putExtra(DownloadIntentService.URL_EXTRA, hnAPI + "item/" + itemIds.get(i) + ".json");
                intent.putExtra(DownloadIntentService.PENDING_RESULT_EXTRA, pendingResult);
                startService(intent);
            }

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    public void handleJSONArray(String data){

        // transforming string into JSON array
        try {
            JSONArray jsonArray = new JSONArray(data);  // item's IDs i.e hackerNews top stories up to top 500

            if(jsonArray.length() < top30) {
                top30 = jsonArray.length();
            }

            for(int i = 0; i < top30; i++) {
                itemIds.add(jsonArray.getString(i));     // first 30 items from top stories
            }

            // test
            //System.out.println("item ids from top stories" + itemIds.toString());

            parseTitleAndContext();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void handleItemData(String jsonData) {

        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            // title of item(i) & html context of item(i) |||||| only urls & titles included
            if(!jsonObject.isNull("url") && !jsonObject.isNull("title")){
                title.add(jsonObject.getString("title"));
                urls.add(jsonObject.getString("url"));
                flak++;

                if ( flak == top30 && !refresh){
                    flak=0;
                    initRecyclerView();

                } else if(flak == top30){
                    flak=0;
                    updateRecyclerView();
                }

                // test
                //System.out.println(jsonObject.getString("title"));
                //System.out.println(jsonObject.getString("url"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: started");

        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh: refreshing");
                itemIds.clear();
                title.clear();
                urls.clear();
                refresh = true;

                initList();
            }
        });

        initList();
    }

    public void initList() {

        try {

            PendingIntent pendingResult = createPendingResult(JSON_DOWNLOAD_REQUEST_CODE, new Intent(), 0);
            Intent intent = new Intent(getApplicationContext(), DownloadIntentService.class);
            intent.putExtra(DownloadIntentService.URL_EXTRA, hnAPI + "topstories.json");
            intent.putExtra(DownloadIntentService.PENDING_RESULT_EXTRA, pendingResult);
            startService(intent);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    public void updateRecyclerView(){
        mSwipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
        refresh = false;
    }

    public void initRecyclerView() {

        Log.d(TAG, "initRecyclerView: initRecyclerView");

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), R.drawable.divider));
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == JSON_DOWNLOAD_REQUEST_CODE) {
            if (resultCode == DownloadIntentService.RESULT_CODE) {
                Log.i("activity result", "json_download");
                handleJSONArray(data.getStringExtra(DownloadIntentService.URL_EXTRA));
            }
        } else if (requestCode == ITEM_DOWNLOAD_REQUEST_CODE) {
            if (resultCode == DownloadIntentService.RESULT_CODE) {
                Log.i("activity result", "item_download");
                handleItemData(data.getStringExtra(DownloadIntentService.URL_EXTRA));
            }
        } else {
            Log.d(TAG, "onActivityResult: request failure");
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
