package com.lhs.project;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.lhs.project.adapter.ItemAdapter;
import com.lhs.project.api.ItemApi;
import com.lhs.project.api.NetworkClient;
import com.lhs.project.api.NetworkClient_youtube;
import com.lhs.project.model.Item;
import com.lhs.project.model.ItemList;
import com.lhs.project.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class YoutubeActivity extends YouTubeBaseActivity {

    YouTubePlayerView playerView;
    YouTubePlayer player;
    RecyclerView recyclerView;
    EditText editSearch;
    List<Item> itemList = new ArrayList<>();
    ItemAdapter adapter;
    String edittxt; // 검색어
    String nextPageToken;
    String prevPageToken;


    private static String videoId = "vy87oPztINk";
    //logcat 사용 설정
    private static final String TAG = "MainActivity";
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);
        editSearch = findViewById(R.id.editSearch);
        Intent intent = getIntent();
        editSearch.setText(intent.getExtras().getString("result"));

        initPlayer();
        YoutubeList();

        Button btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoutubeList();
            }
        });
    }

    private void YoutubeList() {
        showProgress("Youtube List 가져오는 중...");
        recyclerView = findViewById(R.id.recyclerView);
        edittxt = editSearch.getText().toString();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(YoutubeActivity.this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount();
                if((lastPosition+1)==totalCount) {
                    addNetworkData();
                }
            }
        });
//                Retrofit retrofit = NetworkClient.getRetrofitClient(YoutubeActivity.this, Utils.youtube_URL);
        Retrofit retrofit = NetworkClient_youtube.getRetrofitClient(YoutubeActivity.this);
        ItemApi api = retrofit.create(ItemApi.class);
        Call<ItemList> call = api.getItemList("snippet", edittxt, Utils.ACCESS_KEY_youtube, "strict", "");
        call.enqueue(new Callback<ItemList>() {
            @Override
            public void onResponse(Call<ItemList> call, Response<ItemList> response) {
                if(response.isSuccessful()) {
                    dismissProgress();
                    itemList = response.body().getItems();
                    prevPageToken = response.body().getPrevPageToken();
                    nextPageToken = response.body().getNextPageToken();
                    adapter = new ItemAdapter(YoutubeActivity.this, itemList);
                    adapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int index) {
                            videoId = itemList.get(index).getId().videoId;
                            playVideo(videoId);
                        }
                    });
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ItemList> call, Throwable t) {

            }
        });
    }

    private void addNetworkData() {
        showProgress("Youtube List 가져오는 중...");
        Retrofit retrofit = NetworkClient_youtube.getRetrofitClient(YoutubeActivity.this);
        ItemApi api = retrofit.create(ItemApi.class);
        Call<ItemList> call = api.getItemList("snippet", edittxt, Utils.ACCESS_KEY_youtube, "strict", nextPageToken);
        call.enqueue(new Callback<ItemList>() {
            @Override
            public void onResponse(Call<ItemList> call, Response<ItemList> response) {
                if(response.isSuccessful()) {
                    dismissProgress();
                    itemList.addAll(response.body().getItems());
                    prevPageToken = response.body().getPrevPageToken();
                    nextPageToken = response.body().getNextPageToken();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ItemList> call, Throwable t) {

            }
        });
    }

    private void playVideo(String videoId) {
        if(player!=null) {
            if(player.isPlaying()) {
                player.pause();
            }
            player.cueVideo(videoId);
        }
    }

    //유튜브 플레이어 메서드
    private void initPlayer() {
        playerView = findViewById(R.id.youTubePlayerView);
        playerView.initialize(Utils.ACCESS_KEY_youtube, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                player = youTubePlayer;
                player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                    @Override
                    public void onLoading() {

                    }
                    @Override
                    public void onLoaded(String id) {
                        Log.d(TAG, "onLoaded: " + id);
                        player.play();
                    }
                    @Override
                    public void onAdStarted() {
                    }
                    @Override
                    public void onVideoStarted() {
                    }
                    @Override
                    public void onVideoEnded() {
                    }
                    @Override
                    public void onError(YouTubePlayer.ErrorReason errorReason) {
                        Log.d(TAG, "onError: " + errorReason);
                    }
                });
            }
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            }
        });
    }

    private void showProgress(String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void dismissProgress() {
        progressDialog.dismiss();
    }
}