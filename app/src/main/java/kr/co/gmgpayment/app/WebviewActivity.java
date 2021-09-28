package kr.co.gmgpayment.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import kr.co.gmgpayment.app.util.BackPressCloseHandler;
import kr.co.gmgpayment.app.util.SharedPreUtil;


public class WebviewActivity extends AppCompatActivity {

    // 실서버
    private String START_URL = "http://appgmg.gmgpayment.com/card_payment.php";
    //개발서버

    private Context mContext;
    private WebView mWebview;
    private ProgressBar mLoading;

    private BackPressCloseHandler backPressCloseHandler;

    private boolean checkToken = false;

    private String mPageurl = null;
    private String mStrToken = null;

    RelativeLayout webViewContainer;
    WebView windowWebview = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        mContext = WebviewActivity.this;




        backPressCloseHandler = new BackPressCloseHandler(WebviewActivity.this);


        mWebview = findViewById(R.id.main_webview);
        mLoading = findViewById(R.id.main_progress);
        webViewContainer = findViewById(R.id.web_container);

        mWebview.setWebViewClient(new myWebClient());
        mWebview.setWebChromeClient(new ChromeClient(WebviewActivity.this));
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setUseWideViewPort(true);
//        mWebview.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebview.getSettings().setSupportMultipleWindows(true);
        mWebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebview.getSettings().setDomStorageEnabled(true);
        mWebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebview.getSettings().setGeolocationEnabled(true);

        mWebview.addJavascriptInterface(new AndroidBridge(), "AndroidBridge");

        //zoom
        mWebview.getSettings().setLoadWithOverviewMode(true);
        mWebview.getSettings().setUseWideViewPort(true);
        mWebview.getSettings().setBuiltInZoomControls(false);
        mWebview.getSettings().setSupportZoom(true);
        mWebview.getSettings().setDisplayZoomControls(false);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //noinspection deprecation
            CookieSyncManager.createInstance(this);
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mWebview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            mWebview.getSettings().setTextZoom(100);

            PackageInfo pi = null;
            try {
                pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }


            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            Log.d("where", "쿠키 값 : "+SharedPreUtil.getCookie(mContext));
            if (!TextUtils.isEmpty(SharedPreUtil.getCookie(mContext))) {
                String cookie = "store_id_cookie="+SharedPreUtil.getCookie(mContext);
                cookieManager.setCookie(START_URL, cookie);
            } else {
            }
            cookieManager.setAcceptThirdPartyCookies(mWebview, true);
        }




//        if(!TextUtils.isEmpty(getIntent().getStringExtra("url"))){
//            START_URL = getIntent().getStringExtra("url");
//        }

        mWebview.loadUrl(START_URL);

    }


    Handler bridgeHandler = new Handler();

    public class AndroidBridge {
        @JavascriptInterface
        public void set_devicetoken() {
            bridgeHandler.post(new Runnable() {
                public void run() {
                    Log.d("where", "토큰전송 : "+mStrToken);
                    mWebview.loadUrl("javascript:setDeviceToken('"+mStrToken+"')");
                }
            });
        }

        @JavascriptInterface
        public void auto_login(String autoLogin) {
            Log.d("where", "autoLogin? : " + autoLogin);

        }

        @JavascriptInterface
        public void cookie_update(final String cookie) {
            bridgeHandler.post(new Runnable() {
                public void run() {
                    SharedPreUtil.setCookie(mContext, cookie);
                }
            });
        }

        @JavascriptInterface
        public void logout() {
            SharedPreUtil.setCookie(mContext, "");
        }

    }

    private long backKeyPressedTime = 0;
    private long clickTime = 0;
    private long endTime = 0;
    public void onBackPressed() {//웹에서 뒤로가기 처리

        if (System.currentTimeMillis() <= backKeyPressedTime + 500) {
            if (System.currentTimeMillis() <= endTime) {
                backPressCloseHandler.onBackPressed();
                return;
            }
            backKeyPressedTime = System.currentTimeMillis();
            endTime = backKeyPressedTime + 500;
            Toast.makeText(mContext, "뒤로 버튼을 한번 더 클릭 시 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("where", "'url : "+mPageurl);
            backKeyPressedTime = System.currentTimeMillis();
            if (!TextUtils.isEmpty(mPageurl) && !mPageurl.contains("Account/Login")) {
                mWebview.loadUrl("javascript:back()");
            }
        }
    }

    public class myWebClient extends WebViewClient {

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mLoading.setVisibility(View.VISIBLE);

//            if(TextUtils.isEmpty(mPageurl))
//                mPageurl = url;
        }

        public void onPageFinished(WebView view, String url) {
            mLoading.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
//            if(!url.equals(mPageurl)){
//                mPageurl = null;
//            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mPageurl = url;

            if (url != null && url.startsWith("intent://")) {
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    intent.addCategory("android.intent.category.BROWSABLE");
                    intent.setComponent(null);
                    intent.setSelector(null);
                    Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                    if (existPackage != null) {
                        startActivity(intent);
                    } else {
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                        marketIntent.setData(Uri.parse("market://details?id=" + intent.getPackage()));
                        startActivity(marketIntent);
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if(url != null && url.contains("kakaonavi")){
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    intent.addCategory("android.intent.category.BROWSABLE");
                    intent.setComponent(null);
                    intent.setSelector(null);

                    Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                    if (existPackage != null) {
                        startActivity(intent);
                    } else {
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                        marketIntent.setData(Uri.parse("market://details?id=" + intent.getPackage()));
                        startActivity(marketIntent);
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);
            }else if (url.startsWith("sms:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW,  Uri.parse(url));
                startActivity(intent);
                return true;
            } else if( url.startsWith("http://") || url.startsWith("https://") )
            {
                // vguard의 경우 자체 링크로 앱을 실행함
                if(url.equals("http://m.vguard.co.kr/card/vguard_webstandard.apk"))
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("market://details?id=kr.co.shiftworks.vguardweb"));
                    startActivity(intent);

                }
                view.loadUrl(url);
                return true;
            } else {
                if (Build.VERSION_CODES.M >= Build.VERSION.SDK_INT ) {
                    //18 = JellyBean MR2, KITKAT=19, M(마시멜로6.0) = 23
                    if(view.canGoBack()){
                        view.loadUrl(url);
                        return true;
                    }
                    return false;
                }
            }
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url=request.getUrl().toString();
            Log.d("where", "url 11 : "+url);

            mPageurl = url;


            if (url != null && url.startsWith("intent://")) {
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    intent.addCategory("android.intent.category.BROWSABLE");
                    intent.setComponent(null);
                    intent.setSelector(null);
                    Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                    if (existPackage != null) {
                        startActivity(intent);
                    } else {
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                        marketIntent.setData(Uri.parse("market://details?id=" + intent.getPackage()));
                        startActivity(marketIntent);
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }  else if(url != null && url.contains("kakaonavi")){
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    intent.addCategory("android.intent.category.BROWSABLE");
                    intent.setComponent(null);
                    intent.setSelector(null);

                    Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                    if (existPackage != null) {
                        startActivity(intent);
                    } else {
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                        marketIntent.setData(Uri.parse("market://details?id=" + intent.getPackage()));
                        startActivity(marketIntent);
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);
                return true;
            } else if (url.startsWith("sms:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW,  Uri.parse(url));
                //sendIntent.putExtra("sms_body", );
                startActivity(intent);
                return true;
            } else if (request.isRedirect()){
                mWebview.loadUrl(url);
                return true;
            }


            return false;
        }

    }

    public class ChromeClient extends WebChromeClient {
        private View mCustomView;
        private Activity mActivity;

        public ChromeClient(Activity activity) {
            this.mActivity = activity;
        }

        private int mOriginalOrientation;
        private FullscreenHolder mFullscreenContainer;
        private CustomViewCallback mCustomViewCollback;

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {

            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }

            mOriginalOrientation = mActivity.getRequestedOrientation();

            FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();

            mFullscreenContainer = new FullscreenHolder(mActivity);
            mFullscreenContainer.addView(view, ViewGroup.LayoutParams.MATCH_PARENT);
            decor.addView(mFullscreenContainer, ViewGroup.LayoutParams.MATCH_PARENT);
            mCustomView = view;
            mCustomViewCollback = callback;
            mActivity.setRequestedOrientation(mOriginalOrientation);

        }

        @Override
        public void onHideCustomView() {
            if (mCustomView == null) {
                return;
            }

            FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
            decor.removeView(mFullscreenContainer);
            mFullscreenContainer = null;
            mCustomView = null;
            mCustomViewCollback.onCustomViewHidden();

            mActivity.setRequestedOrientation(mOriginalOrientation);
        }


        class FullscreenHolder extends FrameLayout {

            public FullscreenHolder(Context ctx) {
                super(ctx);
                setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
            }

            @Override
            public boolean onTouchEvent(MotionEvent evt) {
                return true;
            }
        }

        @Override
        public void onCloseWindow(WebView window) {
            webViewContainer.removeView(window);    // 화면에서 제거
            windowWebview = null;
            super.onCloseWindow(window);
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {



            WebView childView = new WebView(WebviewActivity.this);
            final WebSettings settings = childView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDatabaseEnabled(true);
            childView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onCloseWindow(WebView window) {
                    super.onCloseWindow(window);
                    window.setVisibility(View.GONE);
                    window.destroy();
                    webViewContainer.removeView(window);
                    windowWebview = null;
                }
            });
            childView.setWebViewClient(new WebViewClient());
            windowWebview = childView;
            childView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            childView.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            ));
            webViewContainer.addView(childView);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(childView);
            resultMsg.sendToTarget();
            return true;

        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mLoading.setProgress(newProgress);
            if (newProgress == 100) {
                mLoading.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
        }


        public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result) {
            new AlertDialog.Builder(WebviewActivity.this)
//                    .setTitle("")
                    .setMessage(message)
                    .setPositiveButton("확인", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
            return true;
        }

    }


}
