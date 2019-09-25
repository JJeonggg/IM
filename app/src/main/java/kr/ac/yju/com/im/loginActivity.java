package kr.ac.yju.com.im;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.kakao.auth.ApiErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class loginActivity extends AppCompatActivity
{

    String snsNumber;

    private static final String TAG = "gapps";
    private OAuthLoginButton naverLogInButton;
    private static OAuthLogin naverLoginInstance;
    Button btnGetApi, btnLogout;
    private static String URL_LOGIN = "http://101.101.162.32/logincheck.php";
    SessionManager sessionManager;

    static final String CLIENT_ID = "pbfYWCGBR6xZ51HvqI15";
    static final String CLIENT_SECRET = "LBUQQPRepM";
    static final String CLIENT_NAME = "네이버 아이디로 로그인 테스트";

    static String Nemail;

    static Context context;


    // 카카오톡
    public SessionCallback sessionCallback;
    private ImageButton btn_custom_login;
    private LoginButton btn_kakao_login;


    //페이스북
    CallbackManager callbackManager;
    com.facebook.login.widget.LoginButton loginButton;
    ImageButton loginbtn;
    Button logoutbtn;

    ViewFlipper Vf;
    Button BtnLogin;
    EditText inputID, inputPW;
    HttpPost httppost;
    StringBuffer buffer;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;

    TextView searchID;
    TextView searchPW;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        sessionManager = new SessionManager(this);


        init();
        init_View();

        faceinit();

        btn_custom_login = (ImageButton) findViewById(R.id.btn_custom_login);

        btn_custom_login.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view)
            {
                btn_kakao_login.performClick();

            }
        });
        btn_kakao_login = (LoginButton) findViewById(R.id.kkbtn);


        sessionCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(sessionCallback);
       // 카카오톡 자동 로그인 Session.getCurrentSession().checkAndImplicitOpen();




        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        BtnLogin = (Button)findViewById(R.id.loginbtn);
        inputID = (EditText)findViewById(R.id.UserID);
        inputPW = (EditText)findViewById(R.id.UserPW);
        searchID = (TextView) findViewById(R.id.search_id);
        searchPW = (TextView) findViewById(R.id.search_pw);

        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Id = inputID.getText().toString().trim();
                String Pw = inputPW.getText().toString().trim();
                if(Id.isEmpty() || Pw.isEmpty())
                {
                    inputID.setError("아이디를 입력 해주세요");
                    inputPW.setError("비밀번호를 입력 해주세요");
                }
                else {
                    dialog = ProgressDialog.show(loginActivity.this, "",
                            "Validating user...", true);
                    new Thread(new Runnable() {
                        public void run() {
                            login();
                        }
                    }).start();
                }
            }
        });

        searchID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginActivity.this, SearchIDActivity.class);
                startActivity(intent);

                finish();
            }
        });


        searchPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginActivity.this, SearchPWActivity.class);
                startActivity(intent);

                finish();
            }
        });



    }

    private void LoginCheck(final String ID)
    {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray jsonArray = (JSONArray) jsonObject.get("sendData");

                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                JSONObject object = jsonArray.getJSONObject(i);

                                String ID = object.getString("ID").trim();
                                String NAME = object.getString("NAME").trim();
                                String PASSWORD = object.getString("PASSWORD").trim();
                                String CLASSFI = object.getString("CLASSFI").trim();
                                String COMPANY = object.getString("COMPANY").trim();
                                String TELE = object.getString("TELE").trim();
                                String ADDRESS = object.getString("ADDRESS").trim();
                                String BIRTHDAY = object.getString("BIRTHDAY").trim();
                                String NICKNAME = object.getString("NICKNAME").trim();
                                String PHOTO = object.getString("PHOTO").trim();

                                sessionManager.createSession(NAME, ID,PASSWORD,CLASSFI,COMPANY,TELE,ADDRESS,BIRTHDAY,NICKNAME,PHOTO);

                                Intent intent = new Intent(loginActivity.this, MainActivity.class);
                                intent.putExtra("ID", ID);
                                intent.putExtra("NAME", NAME);
                                intent.putExtra("PASSWORD", PASSWORD);
                                intent.putExtra("CLASSFI", CLASSFI);
                                intent.putExtra("COMPANY", COMPANY);
                                intent.putExtra("TELE", TELE);
                                intent.putExtra("ADDRESS", ADDRESS);
                                intent.putExtra("BIRTHDAY", BIRTHDAY);
                                intent.putExtra("NICKNAME", NICKNAME);
                                intent.putExtra("PHOTO", PHOTO);
                                startActivity(intent);
                                finish();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(loginActivity.this, "Error " +e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(loginActivity.this, "Error " +error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })


        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ID", ID);
                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }



    void login() {
        try {
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://101.101.162.32/login.php");
            nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("username", inputID.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("password", inputPW.getText().toString()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpclient.execute(httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            System.out.println("Response : " + response);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    dialog.dismiss();
                }
            });

            if (response.equalsIgnoreCase("User Found"))
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(loginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                        String Id = inputID.getText().toString().trim();
                        LoginCheck(Id);

                    }
                });
            } else {
                Toast.makeText(loginActivity.this, "Login Fail", Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception e)
        {
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }




    // 네이버
    //초기화
    public void init()
    {
        context = this;
        naverLoginInstance = OAuthLogin.getInstance();
        naverLoginInstance.init(this,CLIENT_ID,CLIENT_SECRET,CLIENT_NAME);
    }
    //변수 붙이기
    public void init_View()
    {
        init();

        naverLogInButton = (OAuthLoginButton)findViewById(R.id.buttonNaverLogin);

        //로그인 핸들러
        OAuthLoginHandler naverLoginHandler  = new OAuthLoginHandler()
        {
            @Override
            public void run(boolean success)
            {
                if (success) {//로그인 성공
                    new RequestApiTask().execute();


                } else {//로그인 실패
                    String errorCode = naverLoginInstance.getLastErrorCode(context).getCode();
                    String errorDesc = naverLoginInstance.getLastErrorDesc(context);
                    Toast.makeText(context, "errorCode:" + errorCode + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
                }
            }

        };;

        naverLogInButton.setOAuthLoginHandler(naverLoginHandler);

        //tv_name = (TextView)findViewById(R.id.tv_name);
       // tv_id = (TextView)findViewById(R.id.id);
       // btnGetApi = (Button)findViewById(R.id.btngetapi);
        //btnGetApi.setOnClickListener(this);
        //btnLogout = (Button)findViewById(R.id.btnlogout);
       // btnLogout.setOnClickListener(this);

    }



    class RequestApiTask extends AsyncTask<Void, Void, String>
    {
        @Override
        protected void onPreExecute() {//작업이 실행되기 전에 먼저 실행.
            // Nid = ((String) "");//메일 란 비우기
           // Nemail = ((String)"");
        }

        @Override
        protected String doInBackground(Void... params) {//네트워크에 연결하는 과정이 있으므로 다른 스레드에서 실행되어야 한다.
            String url = "https://openapi.naver.com/v1/nid/me";
            String at = naverLoginInstance.getAccessToken(context);
            return naverLoginInstance.requestApi(context, at, url);//url, 토큰을 넘겨서 값을 받아온다.json 타입으로 받아진다.
        }

        protected void onPostExecute(String content) {//doInBackground 에서 리턴된 값이 여기로 들어온다.
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONObject response = jsonObject.getJSONObject("response");

                String Nname = response.getString("name");
                String Nnickname = response.getString("nickname");
                String Nid = response.getString("id");
              //  String Nbirth = response.getString("birthday");
               // Toast.makeText(context, Nname,Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(loginActivity.this, NaverActivity.class);

                intent.putExtra( "Nname", Nname);
                intent.putExtra( "Nnickname", Nnickname);
                intent.putExtra( "Nid", Nid);

                startActivity(intent);
                finish();

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    //카카오톡

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data))
        {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
    }*/

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    private class SessionCallback implements ISessionCallback
    {
        @Override
        public void onSessionOpened()
        {
            UserManagement.getInstance().me(new MeV2ResponseCallback()
            {
                @Override
                public void onFailure(ErrorResult errorResult)
                {
                    int result = errorResult.getErrorCode();

                    if(result == ApiErrorCode.CLIENT_ERROR_CODE)
                    {
                        Toast.makeText(getApplicationContext(), "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"로그인 도중 오류가 발생했습니다: "+errorResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult)
                {
                    Toast.makeText(getApplicationContext(),"세션이 닫혔습니다. 다시 시도해 주세요: "+errorResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(MeV2Response result)
                {
                    Intent intent = new Intent(getApplicationContext(), KakaoActivity.class);
                    intent.putExtra("Kname", result.getNickname());
                    intent.putExtra("Kid", String.valueOf(result.getId()));
                    intent.putExtra("Knickname",result.getNickname());


                    startActivity(intent);
                    finish();
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException e)
        {
            Toast.makeText(getApplicationContext(), "로그인 도중 오류가 발생했습니다. 인터넷 연결을 확인해주세요: "+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }









    public void onclickJoin(View v)
    {
        startActivity(new Intent(this, JoinActivity.class));
        finish();
    }

    // 페이스북

    private void faceinit(){
        callbackManager = CallbackManager.Factory.create();//페이스북의 로그인 콜백을 담당하는 클래스.

        loginButton = (com.facebook.login.widget.LoginButton) findViewById(R.id.login_button);//로그인 버튼. 실제 기능 다수가 이 안에 담겨있다.
        loginButton.setReadPermissions("email");

        loginbtn = (ImageButton)findViewById(R.id.login_2);  //로그인 버튼
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //boolean loggedIn = AccessToken.getCurrentAccessToken() == null;
               // if(loggedIn == true)//현재 로그인이 아닐 때만 로그인한다.
                    loginButton.performClick();//클릭 효과를 옮겨서 이 버튼을 클릭한 효과를 낸다.
            }
        });
       /* logoutbtn = (Button)findViewById(R.id.logout);  //로그아웃 버튼
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance (). logOut ();
                Toast.makeText(loginActivity.this,"로그아웃 성공",Toast.LENGTH_SHORT).show();
            }
        });
*/
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult)
            {//로그인 성공
                AccessToken accessToken = loginResult.getAccessToken();
                String Fid =  loginResult.getAccessToken().getUserId();


                Intent intent = new Intent(getApplicationContext(), FaceActivity.class);
                intent.putExtra("Fid",Fid);

                startActivity(intent);
                finish();


              //  Toast.makeText(loginActivity.this,"로그인 성공",Toast.LENGTH_SHORT).show();



                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback()
                        {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response)
                            {
                                try {
                                   // Intent intent = new Intent(loginActivity.this, MainActivity.class);
                                    String email = object.getString("name");
                                    Toast.makeText(getApplicationContext(), email, Toast.LENGTH_SHORT).show();
                                    //startActivity(intent);
                                }
                                catch (Exception e){
                                    e.printStackTrace();

                                }
                                // Application code
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "email");//데이터를 전부 받아오지 않고 email만 받아온다. "email,name,age" 의 형식으로 받아올수 있음.
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(getApplicationContext(),"로그인 실패",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(getApplicationContext(),"로그인 에러",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    void kakaoCheck(String id)
    {
        try {
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://101.101.162.32/NameValidate.php");
            nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("Kid", id));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpclient.execute(httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            System.out.println("Response : " + response);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    dialog.dismiss();
                }
            });

            if (response.equalsIgnoreCase("User Found"))
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(loginActivity.this, "가입된 회원입니다." , Toast.LENGTH_SHORT).show();

                    }
                });

            }
            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(loginActivity.this, "사용 가능한 입니다.", Toast.LENGTH_SHORT).show();
                    }
                });

            }

        }
        catch(Exception e)
        {
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }




    }

    // 백 키를 눌렀을 시 경고창을 띄움
    public void onBackPressed()
    {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(loginActivity.this);
        builder.setMessage("앱을 종료하시겠습니까?");
        builder .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        finishAffinity();
                        System.runFinalization();
                        System.exit(0);
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