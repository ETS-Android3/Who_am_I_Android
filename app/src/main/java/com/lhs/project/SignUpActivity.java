package com.lhs.project;

import androidx.appcompat.app.AppCompatActivity;

import static com.lhs.project.MainActivity.REQ_REGISTER_CHECK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class SignUpActivity extends AppCompatActivity {

    EditText editEmail, editPw, editNick;
    Button btnDone;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        editEmail = findViewById(R.id.editEmail);
        editPw = findViewById(R.id.editPasswd);
        editNick = findViewById(R.id.editNickname);
        btnDone = findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editEmail.getText().toString().trim();
                String password = editPw.getText().toString().trim();
                String nickname = editNick.getText().toString().trim();

                if(email.isEmpty() || password.isEmpty() || nickname.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "이메일, 비밀번호, 닉네임을 확인하세요...", Toast.LENGTH_LONG).show();
                    return;
                }

                if(password.length()<8 || password.length() > 17) {
                    Toast.makeText(SignUpActivity.this, "비밀번호는 8자리에서 16자리까지 입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(nickname.length()<2 || nickname.length() > 10) {
                    Toast.makeText(SignUpActivity.this, "이름은 2자리에서 10자리까지 입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                showProgress("회원가입중입니다. 잠시만 기다리세요");

                // 토큰값 지우기
                SharedPreferences sp = getSharedPreferences(Utils.PREFERENCE_NAME,MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.commit();

                Retrofit retrofit = NetworkClient.getRetrofitClient(SignUpActivity.this);
                UserApi api = retrofit.create(UserApi.class);

                UserReq userReq = new UserReq(email, password, nickname);
                Call<UserRes> call = api.userSignUp(userReq);
                call.enqueue(new Callback<UserRes>() {
                    @Override
                    public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                        dismissProgress();
                        if(response.isSuccessful()) {
                            // 토큰값 생성하기
                            SharedPreferences sp = getSharedPreferences(Utils.PREFERENCE_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("access_Token", response.body().getAccess_token());
                            editor.commit();
                            setResult(RESULT_OK);
                            finish(); // Signup 창 끝내기
                        } else {
                            // http status code 가 200이 아닌 경우
                            if(response.code()==400) {
                                Toast.makeText(SignUpActivity.this, "이미 회원가입이 되었습니다. 로그인 하세요", Toast.LENGTH_LONG).show();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<UserRes> call, Throwable t) {
                        dismissProgress();
                    }
                });


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