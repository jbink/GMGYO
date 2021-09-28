package kr.co.gmgpayment.app;

import android.os.SystemClock;

import androidx.appcompat.app.AppCompatActivity;

import kr.co.gmgpayment.app.util.Constant;


public class BaseActivity extends AppCompatActivity {


    private long mLastClickTime = 0;

    /**
     * @return 이중 클릭을 막기 위하여
     */
    public boolean getLastClickTime() {
        long currentClickTime = SystemClock.uptimeMillis();
        long elapsedTime = currentClickTime - mLastClickTime;
        mLastClickTime = currentClickTime;

        // 중복 클릭인 경우
        if(elapsedTime <= Constant.MIN_CLICK_INTERVAL) {
            return false;
        }
        return true;
    }
}
