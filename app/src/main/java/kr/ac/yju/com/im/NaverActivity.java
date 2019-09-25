package kr.ac.yju.com.im;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.ViewFlipper;

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
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class NaverActivity extends AppCompatActivity
{
    public String memberNumber;
    public EditText editTextId;
    public EditText editTextPw;
    public EditText editTextName;
    public EditText editTextTel;
    public EditText editTextAdd;
    public EditText editTextBirth;
    public EditText editTextNick;

    ViewFlipper Vf;
    Button BtnLogin;
    EditText inputID, inputPW;
    HttpPost httppost;
    StringBuffer buffer;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;

    Intent intent; //보내는 타입과 받는 타입이 같아야

    String Nname;
    String Nnickname;
    String Nid;

    Button BtnBirth;
    Button BtnADD;
    Button BtnCheckNick;

    String Check;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.naver_join);

        editTextId = (EditText) findViewById(R.id.JoinID);
        editTextPw = (EditText) findViewById(R.id.JoinPW);
        editTextName = (EditText) findViewById(R.id.JoinName);
        editTextTel = (EditText) findViewById(R.id.JoinPhone);
        editTextAdd = (EditText) findViewById(R.id.JoinAD);
        editTextBirth = (EditText) findViewById(R.id.JoinBirth);
        editTextNick = (EditText) findViewById(R.id.JoinNickname);

        intent = getIntent();

        Nname = intent.getStringExtra("Nname");
        Nnickname = intent.getStringExtra("Nnickname");
        Nid = intent.getStringExtra("Nid");

        editTextName.setText(Nname);
        editTextNick.setText(Nnickname);

        BtnCheckNick = (Button)findViewById(R.id.CheckNick);
        BtnLogin = (Button)findViewById(R.id.Checkid);
        BtnBirth = (Button) findViewById(R.id.Checkbirth);
        BtnADD = (Button) findViewById(R.id.CheckAD);

        try
        {
            Intent intent = getIntent();
            Check = intent.getStringExtra("Check");
        }
         catch (Exception e)
         {

        }


            try {
                if(Check.equals("1") == true) {
                    Intent intent = getIntent(); //보내는 타입과 받는 타입이 같아야
                    String Add = intent.getStringExtra("Add");
                    String Id = intent.getStringExtra("Id");
                    String Pw = intent.getStringExtra("Pw");
                    String Cl = intent.getStringExtra("Cl");
                    String Name = intent.getStringExtra("Name");
                    String Tel = intent.getStringExtra("Tel");
                    String Birth = intent.getStringExtra("Birth");
                    String Nick = intent.getStringExtra("Nick");
                    Nid = intent.getStringExtra("Nid");

                    editTextAdd.setText(Add);
                    editTextId.setText(Id);
                    editTextPw.setText(Pw);
                    editTextName.setText(Name);
                    editTextTel.setText(Tel);
                    editTextBirth.setText(Birth);
                    editTextNick.setText(Nick);
                }
                else
                {

                }
            } catch (Exception e) {

            }


        BtnADD.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                intent = getIntent();
                Nid = intent.getStringExtra("Nid");
                String Id = editTextId.getText().toString();
                String Pw = editTextPw.getText().toString();
                String Cl = memberNumber;
                String Name = editTextName.getText().toString();
                String Tel = editTextTel.getText().toString();
                String Add = editTextAdd.getText().toString();
                String Birth = editTextBirth.getText().toString();
                String Nick = editTextNick.getText().toString();


                Intent intent = new Intent(NaverActivity.this, AddNaverClass.class);
                intent.putExtra( "Id", Id);
                intent.putExtra( "Pw", Pw);
                intent.putExtra( "Cl", Cl);
                intent.putExtra( "Name", Name);
                intent.putExtra( "Tel", Tel);
                intent.putExtra( "Add", Add);
                intent.putExtra( "Birth", Birth);
                intent.putExtra( "Nick", Nick);
                intent.putExtra("Nid",Nid);
                startActivity(intent);
                finish();
            }

        });

        BtnCheckNick.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog = ProgressDialog.show(NaverActivity.this, "",
                        "Validating user...", true);
                new Thread(new Runnable() {
                    public void run()
                    {
                        CheckNick();
                    }
                }).start();
            }
        });

        BtnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog = ProgressDialog.show(NaverActivity.this, "",
                        "Validating user...", true);
                new Thread(new Runnable() {
                    public void run()
                    {
                        login();
                    }
                }).start();
            }
        });


        //Calendar를 이용하여 년, 월, 일, 시간, 분을 PICKER에 넣어준다.
        final Calendar cal = Calendar.getInstance();

        BtnBirth.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                DatePickerDialog dialog = new DatePickerDialog(NaverActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {

                        String msg = String.format("%d 년 %d 월 %d 일", year, month + 1, date);
                        // Toast.makeText(JoinActivity.this, msg, Toast.LENGTH_SHORT).show();
                        if(month+1 >= 10 && date >= 10)
                            editTextBirth.setText(year+ "-" + (month+1) + "-" + date);
                        else if(month+1 < 10 && date < 10)
                            editTextBirth.setText(year+ "-" + "0" + (month+1) + "-" + "0" + date);
                        else if(month+1 < 10 && date >= 10)
                            editTextBirth.setText(year+ "-" + "0" + (month+1) + "-" + date);
                        else if(month+1 >= 10 && date < 10)
                            editTextBirth.setText(year+ "-"  + (month+1) + "-" + "0" + date);

                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

                dialog.getDatePicker().setMaxDate(new Date().getTime());    //입력한 날짜 이후로 클릭 안되게 옵션
                dialog.show();
            }

        });
    }

    void login()
    {
        try {
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://101.101.162.32/UserValidate.php");
            nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("username", editTextId.getText().toString()));
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
                        Toast.makeText(NaverActivity.this, "아이디가 존재 합니다.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(NaverActivity.this, "사용 가능한 아이디 입니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                editTextId.setEnabled(false);

            }

        }
        catch(Exception e)
        {
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }

    void CheckNick()
    {
        try {
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://101.101.162.32/NameValidate.php");
            nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("nickname", editTextNick.getText().toString()));
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
                        Toast.makeText(NaverActivity.this, "닉네임이 존재 합니다." , Toast.LENGTH_SHORT).show();

                    }
                });

            }
            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(NaverActivity.this, "사용 가능한 닉네임 입니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                editTextNick.setEnabled(false);

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
        if(memberNumber == "1")
        {
            String Id = editTextId.getText().toString();
            String Pw = editTextPw.getText().toString();
            String Cl = memberNumber;
            String Name = editTextName.getText().toString();
            String Tel = editTextTel.getText().toString();
            String Add = editTextAdd.getText().toString();
            String Birth = editTextBirth.getText().toString();
            String Nick = editTextNick.getText().toString();

            intent = getIntent();
            Nid = intent.getStringExtra("Nid");

            intent = new Intent(NaverActivity.this, NaverlicenseeActivity.class);
            intent.putExtra( "Id", Id);
            intent.putExtra( "Pw", Pw);
            intent.putExtra( "Cl", Cl);
            intent.putExtra( "Name", Name);
            intent.putExtra( "Tel", Tel);
            intent.putExtra( "Add", Add);
            intent.putExtra( "Birth", Birth);
            intent.putExtra( "Nick", Nick);
            intent.putExtra("Nid",Nid);
            startActivity(intent);
        }
        if(memberNumber == "0") // memberNumber == 0 이면 바로 회원 가입.
        {
            String Id = editTextId.getText().toString();
            String Pw = editTextPw.getText().toString();
            String Cl = memberNumber;
            String Name = editTextName.getText().toString();
            String Tel = editTextTel.getText().toString();
            String Add = editTextAdd.getText().toString();
            String Birth = editTextBirth.getText().toString();
            String Nick = editTextNick.getText().toString();
            intent = getIntent();
            Nid = intent.getStringExtra("Nid");

            insertoToDatabase(Id, Pw, Cl, Name, Tel, Add, Birth, Nick,Nid);
            startActivity(new Intent(this, loginActivity.class));
            finish();
        }

    }


    private void insertoToDatabase(String Id, String Pw, String Cl, String Name, String Tel, String Add, String Birth, String Nick, String Nid)
    {
        class InsertData extends AsyncTask<String, Void, String>
        {
            ProgressDialog loading;
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                loading = ProgressDialog.show(NaverActivity.this, "Please Wait", null, true, true);
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
                    String Pw = (String) params[1];
                    String Cl = (String) params[2];
                    String Name = (String) params[3];
                    String Tel = (String) params[4];
                    String Add = (String) params[5];
                    String Birth = (String) params[6];
                    String Nick = (String) params[7];
                    String Nid = (String) params[8];

                    String link = "http://101.101.162.32/NaverJoin.php";
                    String data = URLEncoder.encode("Id", "UTF-8") + "=" + URLEncoder.encode(Id, "UTF-8");
                    data += "&" + URLEncoder.encode("Pw", "UTF-8") + "=" + URLEncoder.encode(Pw, "UTF-8");
                    data += "&" + URLEncoder.encode("Cl", "UTF-8") + "=" + URLEncoder.encode(Cl, "UTF-8");
                    data += "&" + URLEncoder.encode("Name", "UTF-8") + "=" + URLEncoder.encode(Name, "UTF-8");
                    data += "&" + URLEncoder.encode("Tel", "UTF-8") + "=" + URLEncoder.encode(Tel, "UTF-8");
                    data += "&" + URLEncoder.encode("Add", "UTF-8") + "=" + URLEncoder.encode(Add, "UTF-8");
                    data += "&" + URLEncoder.encode("Birth", "UTF-8") + "=" + URLEncoder.encode(Birth, "UTF-8");
                    data += "&" + URLEncoder.encode("Nick", "UTF-8") + "=" + URLEncoder.encode(Nick, "UTF-8");
                    data += "&" + URLEncoder.encode("Nid", "UTF-8") + "=" + URLEncoder.encode(Nid, "UTF-8");

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
        task.execute(Id, Pw,Cl, Name, Tel, Add, Birth,Nick,Nid);
    }


    public void onPopupButtonClick(View button)
    {

        //PopupMenu 객체 생성.
        PopupMenu popup = new PopupMenu(this, button);

        //설정한 popup XML을 inflate.
        popup.getMenuInflater().inflate(R.menu.join_menu, popup.getMenu());

        //팝업메뉴 클릭 시 이벤트
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.normal:
                        memberNumber = "0";
                        Toast.makeText(NaverActivity.this, "일반 회원 선택" , Toast.LENGTH_SHORT).show();
                        Button setNormalMemeber = findViewById(R.id.JoinMember);
                        setNormalMemeber.setText("일반 회원");
                        /* Search를 선택했을 때 이벤트 실행 코드 작성 */
                        break;
                    case R.id.licensee:
                        memberNumber = "1";
                        Toast.makeText(NaverActivity.this, "사업자 선택" , Toast.LENGTH_SHORT).show();
                        Button setliMemeber = findViewById(R.id.JoinMember);
                        setliMemeber.setText("사업자");
                        /* Add를 선택했을 때 이벤트 실행 코드 작성 */
                        break;
                }
                return true;
            }
        });
        popup.show();
    }


    public void onclickBack(View v)
    {
        startActivity(new Intent(this, loginActivity.class));
        finish();
    }

    public void onBackPressed()
    {
        Toast.makeText(NaverActivity.this, "올바른 버튼을 눌러주세요.", Toast.LENGTH_SHORT).show();
    }


}
