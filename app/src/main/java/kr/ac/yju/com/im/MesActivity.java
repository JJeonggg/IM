package kr.ac.yju.com.im;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.http.util.EncodingUtils;

import java.util.HashMap;

public class MesActivity extends AppCompatActivity {

    SessionManager sessionManager;
    private WebView mWebView;
    private WebSettings mWebSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        String mName = user.get(sessionManager.NAME);
        String mId = user.get(sessionManager.ID);
        String mPw = user.get(sessionManager.PASSWORD);
        String mClassfi = user.get(sessionManager.CLASSFI);
        String mCompany = user.get(sessionManager.COMPANY);
        String mTele = user.get(sessionManager.TELE);
        String mAddress = user.get(sessionManager.ADDRESS);
        String mBirthday = user.get(sessionManager.BIRTHDAY);
        String mNickname = user.get(sessionManager.NICKNAME);
        String mPhoto = user.get(sessionManager.PHOTO);

        mWebView = (WebView)findViewById(R.id.webview_mes);
        mWebView.setWebViewClient(new WebViewClient());
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setSupportZoom(true);

        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        String postData = "mId="+mId + "&" + "mPw="+mPw;

        mWebView.postUrl("http://101.101.162.32:8080/Mlogin2.jsp", EncodingUtils.getBytes(postData, "BASE64"));
        setupFullscreenMode();
        setFullscreen();
    }

    private void setupFullscreenMode() {
        View decorView = setFullscreen();
        decorView
                .setOnSystemUiVisibilityChangeListener(visibility -> setFullscreen());
    }

    private View setFullscreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        return decorView;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            setFullscreen();
        }
    }

    // 백 키를 눌렀을 시 경고창을 띄움
    public void onBackPressed() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MesActivity.this);
        builder.setMessage("메인화면으로 돌아가시겠습니까?");
        builder.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        Intent intent = new Intent(MesActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
