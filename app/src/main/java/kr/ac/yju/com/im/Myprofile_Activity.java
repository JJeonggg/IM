package kr.ac.yju.com.im;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class Myprofile_Activity extends AppCompatActivity {

    SessionManager sessionManager;
    public String MemberType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile_);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        HashMap<String, String> user = sessionManager.getUserDetail();

        ImageButton myprof_back = (ImageButton)findViewById(R.id.myprofile_back_btn);
        ImageButton edit_info_btn = (ImageButton)findViewById(R.id.edit_info_btn);
        ImageButton signe_btn = (ImageButton)findViewById(R.id.signout_btn);

        TextView Header_name = (TextView)findViewById(R.id.myprofile_header_name);
        TextView Header_membertype = (TextView)findViewById(R.id.myprofile_header_membertype);
        TextView Header_nickname = (TextView)findViewById(R.id.myprofile_header_nickname);

        TextView myprofile_id = (TextView)findViewById(R.id.myprofile_id);
        TextView myprofile_name = (TextView)findViewById(R.id.myprofile_name);
        TextView myprofile_dob = (TextView)findViewById(R.id.myprofile_dob);
        TextView myprofile_nickname = (TextView)findViewById(R.id.myprofile_nickname);
        TextView myprofile_address = (TextView)findViewById(R.id.myprofile_address);
        TextView myprofile_phone = (TextView)findViewById(R.id.myprofile_phone);
        TextView myprofile_membertype = (TextView)findViewById(R.id.myprofile_membertype);

        ImageView myprofile_circle = (ImageView)findViewById(R.id.myprofile_circle);

        Header_name.setText(user.get(sessionManager.NAME));
        Header_nickname.setText(user.get(sessionManager.NICKNAME));
        if(sessionManager.CLASSFI == "1")
        {
            Header_membertype.setText("사업자");
            myprofile_membertype.setText("사업자");
        }
        else
        {
            Header_membertype.setText("일반 회원");
            myprofile_membertype.setText("일반 회원");
        }

        myprofile_id.setText(user.get(sessionManager.ID));
        myprofile_name.setText(user.get(sessionManager.NAME));
        myprofile_dob.setText(user.get(sessionManager.BIRTHDAY));
        myprofile_nickname.setText(user.get(sessionManager.NICKNAME));
        myprofile_address.setText(user.get(sessionManager.ADDRESS));
        myprofile_phone.setText(user.get(sessionManager.TELE));


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

        myprof_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotohome = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(gotohome);
            }
        });

        edit_info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent modify = new Intent(getApplicationContext(), profileModify.class);
                startActivity(modify);
            }
        });

        signe_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signout = new Intent(getApplicationContext(), Signout_Activity.class);
                startActivity(signout);
            }
        });
    }
}