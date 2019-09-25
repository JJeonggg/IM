package kr.ac.yju.com.im;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AddNaverClass extends AppCompatActivity {

    private WebView webView;
    private TextView result;
    private Handler handler;
    Button BtnCheck;
    boolean addC = false;
    String Add;
    String Id;
    String Pw;
    String Cl;
    String Name;
    String Tel;
    String Birth;
    String Nick;
    String Nid;
    String Check = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addnaver_activity);

        BtnCheck = (Button) findViewById(R.id.result);

        Intent intent = getIntent(); //보내는 타입과 받는 타입이 같아야

        Id = intent.getStringExtra("Id");
        Pw = intent.getStringExtra("Pw");
        Cl = intent.getStringExtra("Cl");
        Name = intent.getStringExtra("Name");
        Tel = intent.getStringExtra("Tel");
        Add = intent.getStringExtra("Add");
        Birth = intent.getStringExtra("Birth");
        Nick = intent.getStringExtra("Nick");
        Nid = intent.getStringExtra("Nid");

        // WebView 초기화
        init_webView();

        // 핸들러를 통한 JavaScript 이벤트 반응
        handler = new Handler();


        BtnCheck.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AddNaverClass.this, NaverActivity.class);
                intent.putExtra( "Add", Add);
                intent.putExtra( "Id", Id);
                intent.putExtra( "Pw", Pw);
                intent.putExtra( "Cl", Cl);
                intent.putExtra( "Name", Name);
                intent.putExtra( "Tel", Tel);
                intent.putExtra( "Add", Add);
                intent.putExtra( "Birth", Birth);
                intent.putExtra( "Nick", Nick);
                intent.putExtra( "Nid", Nid);
                intent.putExtra("Check",Check);
                startActivity(intent);
                finish();
            }

        });
    }

    public void init_webView()
    {
        // WebView 설정
        webView = (WebView) findViewById(R.id.webView);
        // JavaScript 허용
        webView.getSettings().setJavaScriptEnabled(true);
        // JavaScript의 window.open 허용
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        // 두 번째 파라미터는 사용될 php에도 동일하게 사용해야함
        webView.addJavascriptInterface(new AndroidBridge(), "TestApp");
        // web client 를 chrome 으로 설정
        webView.setWebChromeClient(new WebChromeClient());
        // webview url load
        webView.loadUrl("http://101.101.162.32/daumtest.php");
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Add = (String.format("(%s) %s %s", arg1, arg2, arg3));
                    // WebView를 초기화 하지않으면 재사용할 수 없음
                    init_webView();
                }
            });
        }
    }

    public void onBackPressed()
    {
        Toast.makeText(AddNaverClass.this, "올바른 버튼을 눌러주세요.", Toast.LENGTH_SHORT).show();
    }
}
