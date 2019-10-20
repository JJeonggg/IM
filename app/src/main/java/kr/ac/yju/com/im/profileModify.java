package kr.ac.yju.com.im;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class profileModify  extends AppCompatActivity
{
    public String memberNumber;

    ViewFlipper Vf;
    Button BtnLogin;
    EditText inputID, inputPW;
    HttpPost httppost;
    StringBuffer buffer;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;

    Button BtnBirth;
    Button BtnCheckNick;
    Button BtnADD;
    String Check;

    EditText moprofile_id;
    EditText moprofile_birth;
    EditText moprofile_nickname;
    EditText moprofile_address;
    EditText moprofile_phone;


    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_modify);

        TextView Header_name = (TextView)findViewById(R.id.myprofile_header_name);
        TextView Header_membertype = (TextView)findViewById(R.id.myprofile_header_membertype);
        TextView Header_nickname = (TextView)findViewById(R.id.myprofile_header_nickname);



        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        HashMap<String, String> user = sessionManager.getUserDetail();

       moprofile_id = (EditText) findViewById(R.id.JoinID);
       moprofile_birth = (EditText) findViewById(R.id.JoinBirth);
       moprofile_nickname = (EditText) findViewById(R.id.JoinNickname);
       moprofile_address = (EditText) findViewById(R.id.JoinAD);
       moprofile_phone = (EditText) findViewById(R.id.JoinPhone);



        moprofile_id.setText(user.get(sessionManager.ID));
        moprofile_birth.setText(user.get(sessionManager.BIRTHDAY));
        moprofile_nickname.setText(user.get(sessionManager.NICKNAME));
        moprofile_address.setText(user.get(sessionManager.ADDRESS));
        moprofile_phone.setText(user.get(sessionManager.TELE));


        BtnCheckNick = (Button) findViewById(R.id.CheckNick);
        BtnLogin = (Button) findViewById(R.id.Checkid);
        BtnBirth = (Button) findViewById(R.id.Checkbirth);
        BtnADD = (Button) findViewById(R.id.CheckAD);

        ImageView myprofile_circle = (ImageView)findViewById(R.id.myprofile_circle);

        Header_name.setText(user.get(sessionManager.NAME));
        Header_nickname.setText(user.get(sessionManager.NICKNAME));

        if(sessionManager.CLASSFI == "1")
        {
            Header_membertype.setText("사업자");
        }
        else
        {
            Header_membertype.setText("일반 회원");
        }

        try {
            Intent intent = getIntent();
            Check = intent.getStringExtra("Check");
        } catch (Exception e) {

        }

        try{
            String name = user.get(sessionManager.NICKNAME);
            HttpURLConnection.setFollowRedirects(false);

            HttpURLConnection con = (HttpURLConnection)new URL("http://101.101.162.32:8080/profilepic/"+name+".png").openConnection();

            con.setRequestMethod("HEAD");

            if(con.getResponseCode() == HttpURLConnection.HTTP_OK){
                myprofile_circle.setBackground(new ShapeDrawable(new OvalShape()));
                myprofile_circle.setClipToOutline(true);
                String pic = "http://101.101.162.32:8080/profilepic/"+name+".png";
                Glide.with(this).load(pic).into(myprofile_circle);
            }else{
                myprofile_circle.setBackground(new ShapeDrawable(new OvalShape()));
                myprofile_circle.setClipToOutline(true);
                String pic = "http://101.101.162.32:8080/profilepic/def.png";
                Glide.with(this).load(pic).into(myprofile_circle);
            }
        }catch (Exception e){
            e.printStackTrace();
        }




        try {
            if (Check.equals("1") == true) {
                Intent intent = getIntent(); //보내는 타입과 받는 타입이 같아야
                String Add = intent.getStringExtra("Add");
                String Id = intent.getStringExtra("Id");
                String Tel = intent.getStringExtra("Tel");
                String Birth = intent.getStringExtra("Birth");
                String Nick = intent.getStringExtra("Nick");

                moprofile_address.setText(Add);
                moprofile_id.setText(Id);
                moprofile_phone.setText(Tel);
                moprofile_birth.setText(Birth);
                moprofile_nickname.setText(Nick);
            } else {

            }
        } catch (Exception e) {

        }


        BtnADD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Id = moprofile_id.getText().toString();
                String Tel = moprofile_phone.getText().toString();
                String Add = moprofile_address.getText().toString();
                String Birth = moprofile_birth.getText().toString();
                String Nick = moprofile_nickname.getText().toString();

                Intent intent = new Intent(profileModify.this, AddModify.class);
                intent.putExtra("Id", Id);
                intent.putExtra("Tel", Tel);
                intent.putExtra("Add", Add);
                intent.putExtra("Birth", Birth);
                intent.putExtra("Nick", Nick);
                startActivity(intent);
                finish();
            }

        });

        BtnCheckNick.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog = ProgressDialog.show(profileModify.this, "",
                        "Validating user...", true);
                new Thread(new Runnable() {
                    public void run()
                    {
                        CheckNick();
                    }
                }).start();
            }
        });

        //Calendar를 이용하여 년, 월, 일, 시간, 분을 PICKER에 넣어준다.
        final Calendar cal = Calendar.getInstance();

        BtnBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog dialog = new DatePickerDialog(profileModify.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {

                        String msg = String.format("%d 년 %d 월 %d 일", year, month + 1, date);
                        // Toast.makeText(JoinActivity.this, msg, Toast.LENGTH_SHORT).show();
                        if (month + 1 >= 10 && date >= 10)
                            moprofile_birth.setText(year + "-" + (month + 1) + "-" + date);
                        else if (month + 1 < 10 && date < 10)
                            moprofile_birth.setText(year + "-" + "0" + (month + 1) + "-" + "0" + date);
                        else if (month + 1 < 10 && date >= 10)
                            moprofile_birth.setText(year + "-" + "0" + (month + 1) + "-" + date);
                        else if (month + 1 >= 10 && date < 10)
                            moprofile_birth.setText(year + "-" + (month + 1) + "-" + "0" + date);

                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

                dialog.getDatePicker().setMaxDate(new Date().getTime());    //입력한 날짜 이후로 클릭 안되게 옵션
                dialog.show();
            }

        });





    }


    void CheckNick()
    {
        try {
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://101.101.162.32/NameValidate.php");
            nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("nickname", moprofile_nickname.getText().toString()));
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
                        Toast.makeText(profileModify.this, "닉네임이 존재 합니다." , Toast.LENGTH_SHORT).show();

                    }
                });

            }
            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(profileModify.this, "사용 가능한 닉네임 입니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                moprofile_nickname.setEnabled(false);

            }

        }
        catch(Exception e)
        {
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }


    public void insert(View view)
    {
            String Id = moprofile_id.getText().toString();
            String Tel = moprofile_phone.getText().toString();
            String Add = moprofile_address.getText().toString();
            String Birth = moprofile_birth.getText().toString();
            String Nick = moprofile_nickname.getText().toString();

            insertoToDatabase(Id, Tel, Add, Birth, Nick);

            startActivity(new Intent(this, loginActivity.class));
            finish();


    }
    private void insertoToDatabase(String Id, String Tel, String Add, String Birth, String Nick)
    {
        class InsertData extends AsyncTask<String, Void, String>
        {
            ProgressDialog loading;
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                loading = ProgressDialog.show(profileModify.this, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s)
            {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
            @Override
            protected String doInBackground(String... params)
            {

                try
                {
                    String Id = (String) params[0];
                    String Tel = (String) params[1];
                    String Add = (String) params[2];
                    String Birth = (String) params[3];
                    String Nick = (String) params[4];

                    String link = "http://101.101.162.32/modify.php";
                    String data = URLEncoder.encode("Id", "UTF-8") + "=" + URLEncoder.encode(Id, "UTF-8");
                    data += "&" + URLEncoder.encode("Tel", "UTF-8") + "=" + URLEncoder.encode(Tel, "UTF-8");
                    data += "&" + URLEncoder.encode("Add", "UTF-8") + "=" + URLEncoder.encode(Add, "UTF-8");
                    data += "&" + URLEncoder.encode("Birth", "UTF-8") + "=" + URLEncoder.encode(Birth, "UTF-8");
                    data += "&" + URLEncoder.encode("Nick", "UTF-8") + "=" + URLEncoder.encode(Nick, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                }
                catch (Exception e)
                {
                    return new String("Exception: " + e.getMessage());
                }
            }
        }
        InsertData task = new InsertData();
        task.execute(Id, Tel, Add, Birth,Nick);
    }


    public void onclickBack(View v)
    {
        startActivity(new Intent(this, Myprofile_Activity.class));
        finish();
    }

    public void onBackPressed()
    {
        Toast.makeText(profileModify.this, "올바른 버튼을 눌러주세요.", Toast.LENGTH_SHORT).show();
    }
}
