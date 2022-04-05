package com.lhs.project;

import static android.view.View.VISIBLE;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lhs.project.api.NetworkClient;
import com.lhs.project.api.PostingApi;
import com.lhs.project.model.nuPostingRes;
import com.lhs.project.utils.Utils;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ResultphotoActivity extends AppCompatActivity {

    public static final int RECORD_AUDIO_PERMISSION_CHECK = 1001;
    public static final int REQ_LOGIN_CHECK = 102;

    ImageView imgPhoto;
    File photoFile;
    TextView txtContent;
    String result;

    public TextToSpeech tts;
    public ImageButton speakbtn;
    private ProgressDialog progressDialog;
    LinearLayout currentLayout;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultphoto);
        imgPhoto = findViewById(R.id.imageView);
        txtContent = findViewById(R.id.txtContentresult);
        currentLayout = findViewById(R.id.resultLayout);
        currentLayout.setVisibility(View.VISIBLE);

        // 사진선택, 촬영 후 URI 가져와 imageView에 보이기
        Intent intent = getIntent();
        String type = intent.getExtras().getString("type");
        // 앨범선택으로 넘어온 경우 or
        if(type.equals("album")){
            String uri = intent.getExtras().getString("imageUri");
            Uri albumUri = Uri.parse(uri);
            String fileName = getFileName( albumUri );
            try {
                ParcelFileDescriptor parcelFileDescriptor = getContentResolver( ).openFileDescriptor( albumUri, "r" );
                if ( parcelFileDescriptor == null ) return;
                FileInputStream inputStream = new FileInputStream( parcelFileDescriptor.getFileDescriptor( ) );
                photoFile = new File( this.getCacheDir( ), fileName );
                FileOutputStream outputStream = new FileOutputStream( photoFile );
                IOUtils.copy( inputStream, outputStream );
                // 압축시킨다. 해상도 낮춰서
                Bitmap photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                OutputStream os;
                try {
                    os = new FileOutputStream(photoFile);
                    photo.compress(Bitmap.CompressFormat.JPEG,60, os);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
                }
                imgPhoto.setImageBitmap(photo);
            } catch ( Exception e ) {
                e.printStackTrace( );
            }
            postPhotoFile();
        } else if (type.equals("camera")){
            // 카메라를 통해 사진직접찍어서 넘어온경우
            String filePath = intent.getExtras().getString("filePath");
            Bitmap photo = BitmapFactory.decodeFile(filePath);
            imgPhoto.setImageBitmap(photo);
            photoFile = new File(filePath);
            postPhotoFile();
        } else if (type.equals("board")) {
            // Photoboard에서 넘어온 경우
            String s3uri = intent.getExtras().getString("s3Uri");
            if(s3uri!=null) {
                ImageView imageView = findViewById(R.id.imageView);
                Glide.with(getApplicationContext()).asBitmap().load(s3uri)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                photoFile = saveBitmapToJpeg(resource, "ex");
                                Bitmap cacheImg_bitmap = getBitmapFromCacheDir("ex");
                                imageView.setImageBitmap(cacheImg_bitmap);
                                postPhotoFile();
                            }
                        });
            }
        }

        Button youtubeBtn = findViewById(R.id.youtubeBtn);
        youtubeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResultphotoActivity.this, YoutubeActivity.class);
                intent.putExtra("result", result);
                startActivity(intent);
            }
        });

        // 업로드레이아웃의 '올리기'버튼을 클릭했을 때
        Button btnuploadDone = findViewById(R.id.btnUploadDone);
        EditText uploadcontent = findViewById(R.id.uploadcontent);
        btnuploadDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = uploadcontent.getText().toString();
                SharedPreferences sp = getSharedPreferences(Utils.PREFERENCE_NAME, MODE_PRIVATE);
                String access_Token = sp.getString("access_Token", null);
                if(access_Token!=null) {
                    // 로그인 돼 있을 때
                    showProgress("업로드중....");
                    Retrofit retrofit = NetworkClient.getRetrofitClient(ResultphotoActivity.this);
                    PostingApi api = retrofit.create(PostingApi.class);
                    // 이미지
                    RequestBody fileBody = RequestBody.create(photoFile,MediaType.parse("image/*"));
                    MultipartBody.Part part = MultipartBody.Part.createFormData("image", photoFile.getName(), fileBody);
                    // content
                    RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"), content);
                    HashMap<String, RequestBody> requestBodyHashMap = new HashMap<>();
                    requestBodyHashMap.put("content", contentBody);

                    Call<nuPostingRes> call = api.PostingUpload("Bearer " + access_Token, part, requestBodyHashMap);
                    call.enqueue(new Callback<nuPostingRes>() {
                        @Override
                        public void onResponse(Call<nuPostingRes> call, Response<nuPostingRes> response) {
                            dismissProgress();
                            Toast.makeText(ResultphotoActivity.this, "업로드 완료", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ResultphotoActivity.this, PhotoboardActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        @Override
                        public void onFailure(Call<nuPostingRes> call, Throwable t) {
                            Toast.makeText(ResultphotoActivity.this, "연결안됨", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // 로그인이 안돼있을 때
                    Toast.makeText(ResultphotoActivity.this, "로그인 오류", Toast.LENGTH_SHORT).show();
                }
            }
        });


        /////////////////////////////////////////  TTS  /////////////////////////////////////////////////
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=android.speech.tts.TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
        speakbtn = findViewById(R.id.speakbtn);
        speakbtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(
                        ResultphotoActivity.this, Manifest.permission.RECORD_AUDIO);

                if(permissionCheck != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ResultphotoActivity.this,
                            new String[]{Manifest.permission.RECORD_AUDIO} ,
                            RECORD_AUDIO_PERMISSION_CHECK);
                    Toast.makeText(ResultphotoActivity.this, "오디오 권한이 필요합니다.",
                            Toast.LENGTH_SHORT).show();
                    return; }
                TextView content = findViewById(R.id.txtContentresult);
                String text2 = content.getText().toString();
                tts.setPitch(0.7f);
                tts.setSpeechRate(0.7f);
                tts.speak((String) text2, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        ////////////////////////////////  TTS END  ///////////////////////////////////////

        // 다른친구가 볼 수 있게 올리기 버튼
        Button btnUpload = findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = getSharedPreferences(Utils.PREFERENCE_NAME, MODE_PRIVATE);
                String access_Token = sp.getString("access_Token", null);
                if(access_Token!=null) {
                    // 로그인됐을 때
                    currentLayout.setVisibility(View.INVISIBLE);
                    currentLayout = findViewById(R.id.uploadLayout);
                    currentLayout.setVisibility(View.VISIBLE);
                } else {
                    // 로그인 안됐을 때
                    Toast.makeText(ResultphotoActivity.this, "로그인이 필요합니다. ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ResultphotoActivity.this, LoginActivity.class);
                    startActivityForResult(intent, REQ_LOGIN_CHECK);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ_LOGIN_CHECK && resultCode==RESULT_OK) {
            // 로그인 하고 돌아왔을 때
            SharedPreferences sp = getSharedPreferences(Utils.PREFERENCE_NAME, MODE_PRIVATE);
            String access_Token = sp.getString("access_Token", null);
            currentLayout.setVisibility(View.INVISIBLE);
            currentLayout = findViewById(R.id.uploadLayout);
            currentLayout.setVisibility(View.VISIBLE);
        }
    }

    // 네트워크로 사진 보내기
    private void postPhotoFile(){
        showProgress("사진분석중...");
        Retrofit retrofit = NetworkClient.getRetrofitClient(ResultphotoActivity.this);
        PostingApi api = retrofit.create(PostingApi.class);
        RequestBody fileBody = RequestBody.create(photoFile,
                MediaType.parse("image/*"));
        MultipartBody.Part part = MultipartBody.Part.createFormData(
                "image", photoFile.getName(), fileBody
        );
        Call<nuPostingRes> call = api.nuPostingUpload(part);
        call.enqueue(new Callback<nuPostingRes>() {
            @Override
            public void onResponse(Call<nuPostingRes> call, Response<nuPostingRes> response) {
                dismissProgress();
                result = response.body().getResult();
                txtContent.setText(result);
            }

            @Override
            public void onFailure(Call<nuPostingRes> call, Throwable t) {
                Toast.makeText(ResultphotoActivity.this, "연결안됨", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //앨범에서 선택한 사진이름 가져오기
    public String getFileName( Uri uri ) {
        Cursor cursor = getContentResolver( ).query( uri, null, null, null, null );
        try {
            if ( cursor == null ) return null;
            cursor.moveToFirst( );
            @SuppressLint("Range") String fileName = cursor.getString( cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME) );
            cursor.close( );
            return fileName;

        } catch ( Exception e ) {
            e.printStackTrace( );
            cursor.close( );
            return null;
        }
    }

    // 비트맵 이미지를 캐시에 저장
    private File saveBitmapToJpeg(Bitmap bitmap, String name) {
        File storage = getCacheDir();
        String fileName = name + ".jpg";
        File tempFile = new File(storage, fileName);
        try {
            // 자동으로 빈 파일을 생성합니다.
            tempFile.createNewFile();
            // 파일을 쓸 수 있는 스트림을 준비합니다.
            FileOutputStream out = new FileOutputStream(tempFile);
            // compress 함수를 사용해 스트림에 비트맵을 저장합니다.
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            // 스트림 사용후 닫아줍니다.
            out.close();
        } catch (FileNotFoundException e) {
            Log.e("MyTag","FileNotFoundException : " + e.getMessage());
        } catch (IOException e) {
            Log.e("MyTag","IOException : " + e.getMessage());
        }
        return tempFile;
    }

    // 캐시에서 불러오기
    private Bitmap getBitmapFromCacheDir(String name) {
        File storage = getCacheDir();
        String fileName = name + ".jpg";
        File tempFile = new File(storage, fileName);
        Bitmap bitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
        return bitmap;
    }

    //tts 부분
    @Override public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        super.onDestroy();
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