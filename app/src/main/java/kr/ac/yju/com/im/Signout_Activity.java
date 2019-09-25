package kr.ac.yju.com.im;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Signout_Activity extends AppCompatActivity {

    Button BtnLogin;
    EditText inputPW;
    HttpPost httppost;
    StringBuffer buffer;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;
    SessionManager sessionManager;
    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signout_);

        ImageButton signout_confirm = (ImageButton) findViewById(R.id.signout_confirm_btn);
        ImageButton signout_cancel = (ImageButton) findViewById(R.id.signout_cancel_btn);
        inputPW = (EditText) findViewById(R.id.signout_password_input);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        HashMap<String, String> user = sessionManager.getUserDetail();

        username = user.get(sessionManager.ID);


        signout_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Pw = inputPW.getText().toString().trim();
                if(Pw.isEmpty())
                {
                    inputPW.setError("비밀번호를 입력 해주세요");
                }
                else {
                    dialog = ProgressDialog.show(Signout_Activity.this, "",
                            "Validating user...", true);
                    new Thread(new Runnable() {
                        public void run() {
                            userdelete();
                        }
                    }).start();
                }
            }
        });




        signout_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Signout_Activity.this, "취소하셨습니다.", Toast.LENGTH_LONG).show();
                Intent sign_canel = new Intent(getApplicationContext(), Myprofile_Activity.class);
                startActivity(sign_canel);
            }
        });
    }


    void userdelete() {
        try {
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://101.101.162.32/deleteuser.php");
            nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("username", username));
            nameValuePairs.add(new BasicNameValuePair("password", inputPW.getText().toString()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpclient.execute(httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            System.out.println("Response : " + response);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //sessionManager.logout();
                    dialog.dismiss();
                    Toast.makeText(Signout_Activity.this, "회원탈퇴 하셨습니다.", Toast.LENGTH_LONG).show();
                    startActivity((new Intent(Signout_Activity.this, loginActivity.class)));
                    finish();


                }
            });

            if (response.equalsIgnoreCase("User Found")) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                    }
                });


            } else {
                Toast.makeText(Signout_Activity.this, "Login Fail", Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception e)
        {
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }








    /*void userdelete()
    {
        try {

            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://101.101.162.32/deleteuser.php");
            nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("username", username));
            nameValuePairs.add(new BasicNameValuePair("password", inputPW.getText().toString()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpclient.execute(httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            System.out.println("Response : " + response);
            runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    dialog.dismiss();
                }
            });

            if(response.equalsIgnoreCase("User Found"))
            {
                Toast.makeText(Signout_Activity.this, "삭제성공", Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(Signout_Activity.this, "Login Success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Signout_Activity.this, NaverActivity.class);
                        startActivity(intent);
                    }
                });
            } else {
                Toast.makeText(Signout_Activity.this, "Login Fail", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }*/
}
