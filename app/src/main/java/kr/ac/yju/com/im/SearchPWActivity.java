package kr.ac.yju.com.im;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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

public class SearchPWActivity extends AppCompatActivity
{
    private EditText editTextID;
    private EditText editTextName;
    private EditText SearchPW;

    ViewFlipper Vf;
    Button BtnLogin;
    EditText inputID, inputPW;
    HttpPost httppost;
    StringBuffer buffer;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;
    private static String URL_LOGIN = "http://101.101.162.32/searchPW.php";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchpw_activity);

        editTextID = (EditText)findViewById(R.id.search_IDEdit) ;
        editTextName = (EditText)findViewById(R.id.search_NameEdit);
        SearchPW = (EditText)findViewById(R.id.search_IDEND);
        SearchPW.setEnabled(false);
    }


    public void onclickSearchCheck(View view)
    {

        dialog = ProgressDialog.show(SearchPWActivity.this, "",
                "Validating user...", true);
        new Thread(new Runnable() {
            public void run()
            {
                SearchID();
            }
        }).start();
    }



    public void onclickSearchBack(View v)
    {
        startActivity(new Intent(this, loginActivity.class));
        finish();
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

                                SearchPW.setText(PASSWORD);

                                /*sessionManager.createSession(NAME, ID,CLASSFI,COMPANY,TELE,ADDRESS,BIRTHDAY,NICKNAME,PHOTO);

                                Intent intent = new Intent(SearchIDActivity.this, MainActivity.class);
                                intent.putExtra("ID", ID);
                                intent.putExtra("NAME", NAME);
                                intent.putExtra("CLASSFI", CLASSFI);
                                intent.putExtra("COMPANY", COMPANY);
                                intent.putExtra("TELE", TELE);
                                intent.putExtra("ADDRESS", ADDRESS);
                                intent.putExtra("BIRTHDAY", BIRTHDAY);
                                intent.putExtra("NICKNAME", NICKNAME);
                                intent.putExtra("PHOTO", PHOTO);
                                startActivity(intent);
                                finish();*/

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SearchPWActivity.this, "Error " +e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SearchPWActivity.this, "Error " +error.toString(), Toast.LENGTH_SHORT).show();
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




    void SearchID() {
        try {
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://101.101.162.32/IDValidate.php");
            nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("ID", editTextID.getText().toString()));
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

            if (response.equalsIgnoreCase("User Found")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(SearchIDActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                        String ID = editTextID.getText().toString().trim();
                        LoginCheck(ID);

                    }
                });

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SearchPWActivity.this, "입력된 정보와 일치하는 회원이 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

            }

        } catch (Exception e) {
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }
    public void onBackPressed()
    {
        Toast.makeText(SearchPWActivity.this, "올바른 버튼을 눌러주세요.", Toast.LENGTH_SHORT).show();
    }
}
