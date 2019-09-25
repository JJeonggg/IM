package kr.ac.yju.com.im;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class KakaolicenseeActivity extends AppCompatActivity {
    private EditText editTextCom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kakaolicensee_activity);

        editTextCom = (EditText) findViewById(R.id.liName);
    }
    public void onclickliCheck(View view)
    {
        Intent intent = getIntent(); //보내는 타입과 받는 타입이 같아야

        String Id = intent.getStringExtra("Id");
        String Pw = intent.getStringExtra("Pw");
        String Cl = intent.getStringExtra("Cl");
        String Name = intent.getStringExtra("Name");
        String Tel = intent.getStringExtra("Tel");
        String Add = intent.getStringExtra("Add");
        String Birth = intent.getStringExtra("Birth");
        String Nick = intent.getStringExtra("Nick");
        String Kid = intent.getStringExtra("Kid");
        String Com = editTextCom.getText().toString();


        insertoToDatabase(Id, Pw, Cl, Name, Tel, Add, Birth, Nick, Com,Kid);

        startActivity(new Intent(this, loginActivity.class));
        finish();
    }

    private void insertoToDatabase(String Id, String Pw, String Cl, String Name, String Tel, String Add, String Birth, String Nick, String Com, String Kid)
    {
        class InsertData extends AsyncTask<String, Void, String>
        {
            ProgressDialog loading;
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                loading = ProgressDialog.show(KakaolicenseeActivity.this, "Please Wait", null, true, true);
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
                    String Com = (String) params[8];
                    String Kid = (String) params[9];

                    String link = "http://101.101.162.32/Kakaolice.php";
                    String data = URLEncoder.encode("Id", "UTF-8") + "=" + URLEncoder.encode(Id, "UTF-8");
                    data += "&" + URLEncoder.encode("Pw", "UTF-8") + "=" + URLEncoder.encode(Pw, "UTF-8");
                    data += "&" + URLEncoder.encode("Cl", "UTF-8") + "=" + URLEncoder.encode(Cl, "UTF-8");
                    data += "&" + URLEncoder.encode("Name", "UTF-8") + "=" + URLEncoder.encode(Name, "UTF-8");
                    data += "&" + URLEncoder.encode("Tel", "UTF-8") + "=" + URLEncoder.encode(Tel, "UTF-8");
                    data += "&" + URLEncoder.encode("Add", "UTF-8") + "=" + URLEncoder.encode(Add, "UTF-8");
                    data += "&" + URLEncoder.encode("Birth", "UTF-8") + "=" + URLEncoder.encode(Birth, "UTF-8");
                    data += "&" + URLEncoder.encode("Nick", "UTF-8") + "=" + URLEncoder.encode(Nick, "UTF-8");
                    data += "&" + URLEncoder.encode("Com", "UTF-8") + "=" + URLEncoder.encode(Com, "UTF-8");
                    data += "&" + URLEncoder.encode("Kid", "UTF-8") + "=" + URLEncoder.encode(Kid, "UTF-8");

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
        task.execute(Id, Pw,Cl, Name, Tel, Add, Birth,Nick,Com,Kid);
    }

    public void onclickliBack(View v)
    {
        startActivity(new Intent(this, loginActivity.class));
        finish();
    }

    public void onBackPressed()
    {
        Toast.makeText(KakaolicenseeActivity.this, "올바른 버튼을 눌러주세요.", Toast.LENGTH_SHORT).show();
    }
}