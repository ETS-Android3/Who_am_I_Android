package com.lhs.project;

import static com.lhs.project.MainActivity.REQ_REGISTER_CHECK;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lhs.project.api.NetworkClient;
import com.lhs.project.api.UserApi;
import com.lhs.project.model.UserReq;
import com.lhs.project.model.UserRes;
import com.lhs.project.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    public static final int REQ_LOGIN_CHECK = 102;
    public static final int REQ_REGISTER_CHECK = 101;
    EditText editEmail, editPw;
    Button loginBtn;
    TextView txtSignup;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editEmail);
        editPw = findViewById(R.id.editPasswd);

        // 아직 회원이 아니신가요? 클릭하면 회원가입 화면으로 넘어가기
        txtSignup = findViewById(R.id.txtSignUp);
        txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivityForResult(intent,REQ_REGISTER_CHECK);
            }
        });

        // login / logout 버튼
        loginBtn = findViewById(R.id.btnDone);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = editEmail.getText().toString().trim();
                String password = editPw.getText().toString().trim();

                // 이메일 확인
                if(email.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
                }

                // 비밀번호 확인
                if(password.isEmpty()) {
                    Toast.makeText(LoginActivity.this,"비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                }

                // 네트워크를 통해 API 호출
                showProgress("로그인 중...");
                Retrofit retrofit = NetworkClient.getRetrofitClient(LoginActivity.this);
                UserApi api = retrofit.create(UserApi.class);
                UserReq userReq = new UserReq(email, password);
                Call<UserRes> call = api.userLogin(userReq);
                call.enqueue(new Callback<UserRes>() {
                    @Override
                    public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                        if(response.isSuccessful()) {
                            // 로그인
                            SharedPreferences sp = getSharedPreferences(Utils.PREFERENCE_NAME, MODE_PRIVATE);
                            String access_Token = response.body().getAccess_token();
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("access_Token", access_Token);
                            editor.apply();
                            dismissProgress();
                            Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            // 이 전 액티비티로 돌아가기
                            setResult(RESULT_OK);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserRes> call, Throwable t) {

                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ_REGISTER_CHECK && resultCode==RESULT_OK) {
            // 회원가입하고 돌아왔을 때 이 전 액티비티로 보내기
            setResult(RESULT_OK);
            finish();
        }
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