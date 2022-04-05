package com.lhs.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.lhs.project.adapter.MyPostingAdapter;
import com.lhs.project.adapter.PostingAdapter;
import com.lhs.project.api.NetworkClient;
import com.lhs.project.api.PostingApi;
import com.lhs.project.model.Posting;
import com.lhs.project.model.PostingList;
import com.lhs.project.model.PostingRes;
import com.lhs.project.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PhotoboardActivity extends AppCompatActivity {

    ProgressBar progressBar;
    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    PostingAdapter adapter;
    MyPostingAdapter myadapter;
    List<Posting> postingList = new ArrayList<>();

    RadioButton rg_btn1;
    RadioButton rg_btn2;

    int offset=0, limit=5, cnt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoboard);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView_photoboard);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(PhotoboardActivity.this));
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
//                    addNetworkData();
                    if(rg_btn1.isChecked() || rg_btn1.isEnabled() || rg_btn1.isSelected()) {
                        addNetworkData();
                    } else if(rg_btn2.isChecked() || rg_btn2.isEnabled() || rg_btn2.isSelected()) {
                        addNetworkData_myposting();
                    }
                }
            }
        });

        // 전체포스팅 라디오버튼
        rg_btn1 = findViewById(R.id.rg_btn1);
        // 내가올린포스팅 라디오 버튼
        rg_btn2 = findViewById(R.id.rg_btn2);
        if(rg_btn1.isChecked() || rg_btn1.isEnabled() || rg_btn1.isSelected()) {
            getPostingList();
        } else if(rg_btn2.isChecked() || rg_btn2.isEnabled() || rg_btn2.isSelected()) {
            getMyPostingList();
        }

        rg_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPostingList();
            }
        });

        rg_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMyPostingList();
            }
        });


    } // onCeate END



    private void getPostingList() {
        showProgress("전체 사진 가져오는 중...");
        offset=0;
        limit=5;
        Retrofit retrofit = NetworkClient.getRetrofitClient(PhotoboardActivity.this);
        PostingApi api = retrofit.create(PostingApi.class);
        Call<PostingList> call = api.getPostingMemoList(offset, limit);
        call.enqueue(new Callback<PostingList>() {
            @Override
            public void onResponse(Call<PostingList> call, Response<PostingList> response) {
                dismissProgress();
                if(response.isSuccessful()) {
                    postingList = response.body().getPosting_list();
                    adapter = new PostingAdapter(PhotoboardActivity.this, postingList);
                    cnt = response.body().getPosting_list().size();
                    offset+=cnt;
                    recyclerView.setAdapter(adapter);
                    // 카드뷰 하나 선택했을 때
                    adapter.setOnItemClickListener(new PostingAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(PostingAdapter.ViewHolder holder, View view, int position) {
                            Intent i = new Intent(PhotoboardActivity.this, ResultphotoActivity.class);
                            i.putExtra("type", "board");
                            i.putExtra("s3Uri", postingList.get(position).getImg_url().toString());
                            startActivity(i);
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<PostingList> call, Throwable t) {
            }
        });
    }

    private void getMyPostingList() {
        recyclerView.removeAllViewsInLayout();
        SharedPreferences sp = getSharedPreferences(Utils.PREFERENCE_NAME, MODE_PRIVATE);
        String access_Token = sp.getString("access_Token", null);

        if(access_Token==null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PhotoboardActivity.this, LoginActivity.class);
            startActivityForResult(intent, 2000);
        }


        showProgress("내가 올린 사진 가져오는 중...");
        offset=0;
        limit=5;
        Retrofit retrofit = NetworkClient.getRetrofitClient(PhotoboardActivity.this);
        PostingApi api = retrofit.create(PostingApi.class);
        Call<PostingList> call = api.myPostingList("Bearer " + access_Token, offset, limit);
        call.enqueue(new Callback<PostingList>() {
            @Override
            public void onResponse(Call<PostingList> call, Response<PostingList> response) {
                dismissProgress();
                if(response.isSuccessful()) {
                    postingList = response.body().getPosting_list();
                    myadapter = new MyPostingAdapter(PhotoboardActivity.this, postingList);
                    cnt = response.body().getPosting_list().size();
                    offset+=cnt;
                    recyclerView.setAdapter(myadapter);
                    // 카드뷰 하나 선택했을 때
                    myadapter.setOnItemClickListener(new MyPostingAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(MyPostingAdapter.ViewHolder holder, View view, int position) {
                            Intent i = new Intent(PhotoboardActivity.this, ResultphotoActivity.class);
                            i.putExtra("type", "board");
                            i.putExtra("s3Uri", postingList.get(position).getImg_url().toString());
                            startActivity(i);
                        }
                        @Override
                        public void onDeleteClick(int index) {
                            // deleteBtn 버튼 클릭했을 때
                            showAlertDialog(index);
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<PostingList> call, Throwable t) {
            }
        });
    }

    private void showAlertDialog(int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PhotoboardActivity.this);
        builder.setTitle("정말 삭제하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 네트워크를 통해서 삭제를 수행한다.
                        deletePosting(index);
                    }
                })
                .setNegativeButton("아니오", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deletePosting(int index) {
        Posting posting = postingList.get(index);
        SharedPreferences sp = getSharedPreferences(Utils.PREFERENCE_NAME, MODE_PRIVATE);
        String access_Token = sp.getString("access_Token", null);
        showProgress("포스팅 삭제중...");
        Retrofit retrofit = NetworkClient.getRetrofitClient(PhotoboardActivity.this);
        PostingApi api = retrofit.create(PostingApi.class);
        Call<PostingRes> call = api.myPostingDelete("Bearer " + access_Token, posting.getPosting_id());
        call.enqueue(new Callback<PostingRes>() {
            @Override
            public void onResponse(Call<PostingRes> call, Response<PostingRes> response) {
                dismissProgress();
                if(response.isSuccessful()) {
                    postingList.remove(index);
                    getMyPostingList();
                }
            }
            @Override
            public void onFailure(Call<PostingRes> call, Throwable t) {

            }
        });

    }

    private void addNetworkData() {
        showProgress("사진 더 가져오는 중...");
        Retrofit retrofit = NetworkClient.getRetrofitClient(PhotoboardActivity.this);
        PostingApi api = retrofit.create(PostingApi.class);
        Call<PostingList> call = api.getPostingMemoList(offset, limit);
        call.enqueue(new Callback<PostingList>() {
            @Override
            public void onResponse(Call<PostingList> call, Response<PostingList> response) {
                if(response.isSuccessful()) {
                    dismissProgress();
                    postingList.addAll(response.body().getPosting_list());
                    adapter.notifyDataSetChanged();
                    cnt = response.body().getPosting_list().size();
                    offset+=cnt;
                }
            }

            @Override
            public void onFailure(Call<PostingList> call, Throwable t) {

            }
        });
    }

    private void addNetworkData_myposting() {
        showProgress("사진 더 가져오는 중...");
        Retrofit retrofit = NetworkClient.getRetrofitClient(PhotoboardActivity.this);
        PostingApi api = retrofit.create(PostingApi.class);
        Call<PostingList> call = api.getPostingMemoList(offset, limit);
        call.enqueue(new Callback<PostingList>() {
            @Override
            public void onResponse(Call<PostingList> call, Response<PostingList> response) {
                if(response.isSuccessful()) {
                    dismissProgress();
                    postingList.addAll(response.body().getPosting_list());
                    myadapter.notifyDataSetChanged();
                    cnt = response.body().getPosting_list().size();
                    offset+=cnt;
                }
            }

            @Override
            public void onFailure(Call<PostingList> call, Throwable t) {

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        // 로그인 하고 돌아왔을 때
        if(requestCode==2000 && resultCode==RESULT_OK) {
            getMyPostingList();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}