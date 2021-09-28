package kr.co.gmgpayment.app.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.co.gmgpayment.app.BaseActivity;
import kr.co.gmgpayment.app.MainActivity;
import kr.co.gmgpayment.app.R;
import kr.co.gmgpayment.app.WebviewActivity;
import kr.co.gmgpayment.app.api.API_Adapter;
import kr.co.gmgpayment.app.util.Constant;
import kr.co.gmgpayment.app.util.SharedPreUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActicity extends BaseActivity {

    Context mContext;

    private EditText mEdtId, mEdtPw;
    CheckBox mChkAutoLogin;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mContext = LoginActicity.this;


        mEdtId = findViewById(R.id.login_edt_id);
        mEdtPw = findViewById(R.id.login_edt_pw);
        mChkAutoLogin = findViewById(R.id.login_chk_auto);
    }


    public void onClick(View v) {
        if (getLastClickTime() == false)
            return;
        switch (v.getId()) {
            case R.id.login_btn_go :
                if(isCheckValue() == true) {
                    api_Login(
                            mEdtId.getText().toString(),
                            mEdtPw.getText().toString()
                    );
                }
                break;
            case R.id.login_layout_auto :
                if(mChkAutoLogin.isChecked()) {
                    mChkAutoLogin.setChecked(false);
                    SharedPreUtil.setAutoLogin(mContext, false);
                } else {
                    mChkAutoLogin.setChecked(true);
                    SharedPreUtil.setAutoLogin(mContext, true);
                }
        }
    }


    private boolean isCheckValue() {
        if (TextUtils.isEmpty(mEdtId.getText().toString())) {
            Toast.makeText(mContext, "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(mEdtPw.getText().toString())) {
            Toast.makeText(mContext, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void api_Login(String id, String pw) {

        Call<LoginDTO> call = API_Adapter.getInstance().login_chk(
                id, pw
        );
        call.enqueue(new Callback<LoginDTO>() {
            @Override
            public void onResponse(Call<LoginDTO> call, Response<LoginDTO> response) {
                if (response.isSuccessful()) {
                    LoginDTO result = response.body();
                    if (Constant.API_RESPONSE_SUCCESS.equals(result.getRESULT())) {
                        if(mChkAutoLogin.isChecked()) {
                            SharedPreUtil.setID(mContext, mEdtId.getText().toString());
                            SharedPreUtil.setPW(mContext, mEdtPw.getText().toString());
                        }

                        SharedPreUtil.setCookie(mContext, result.getDATA().getStore_id());
                        Intent intent = new Intent(mContext, WebviewActivity.class);
                        startActivity(intent);
                        finish();
                    } 
//                    else if (Constant.API_RESPONSE_FAIL2.equals(result.getRESULT())){
//                        Toast.makeText(mContext, "패스워드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
//                        mEdtId.setText("");
//                        mEdtPw.setText("");
//                    }
                } else {
                    Log.d("####", "response error");
                }
            }

            @Override
            public void onFailure(Call<LoginDTO> call, Throwable throwable) {

            }
        });
    }
}
