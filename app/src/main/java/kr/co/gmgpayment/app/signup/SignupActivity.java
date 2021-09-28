package kr.co.gmgpayment.app.signup;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;

import kr.co.gmgpayment.app.BaseActivity;
import kr.co.gmgpayment.app.R;

public class SignupActivity extends BaseActivity {

    private Context mContext;
    private CheckBox[] mChkJungsan;
    private CheckBox[] mChkHando;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mContext = SignupActivity.this;

        mChkJungsan = new CheckBox[]{
          findViewById(R.id.signup_chk_5_day),
          findViewById(R.id.signup_chk_next_1),
          findViewById(R.id.signup_chk_now)
        };
        mChkHando = new CheckBox[]{
          findViewById(R.id.signup_chk_50man),
          findViewById(R.id.signup_chk_150man),
          findViewById(R.id.signup_chk_300man)
        };

        RadioGroup rgBusiness = findViewById(R.id.signup_rg_business);
        rgBusiness.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if(checkedId == R.id.signup_rg_btn_store) {
                }
                else if(checkedId == R.id.signup_rg_btn_no_store) {
                }
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signup_chk_5_day :
                setViewCheckJungsanLayout(0);
                break;
            case R.id.signup_chk_next_1 :
                setViewCheckJungsanLayout(1);
                break;
            case R.id.signup_chk_now :
                setViewCheckJungsanLayout(2);
                break;
            case R.id.signup_chk_50man :
                setViewCheckHandoLayout(0);
                break;
            case R.id.signup_chk_150man :
                setViewCheckHandoLayout(1);
                break;
            case R.id.signup_chk_300man :
                setViewCheckHandoLayout(2);
                break;
        }
    }

    private void setViewCheckJungsanLayout(int idx) {
        for (int i=0 ; i<mChkJungsan.length ; i++) {
            mChkJungsan[i].setChecked(false);
        }
        mChkJungsan[idx].setChecked(true);
    }
    private void setViewCheckHandoLayout(int idx) {
        for (int i=0 ; i<mChkHando.length ; i++) {
            mChkHando[i].setChecked(false);
        }
        mChkHando[idx].setChecked(true);
    }

}
