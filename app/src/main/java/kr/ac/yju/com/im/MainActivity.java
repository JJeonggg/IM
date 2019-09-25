package kr.ac.yju.com.im;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    SessionManager sessionManager;
    private Button btn_logout;
    public String MemberType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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



        // btn_logout = findViewById(R.id.logout);

        /*btn_logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sessionManager.logout();
                Toast.makeText(MainActivity.this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
            }
        });*/

        ImageButton btn2 = (ImageButton)findViewById(R.id.ar_btn);
        btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intent1 = new Intent(getApplicationContext(), AR_Activity.class);
                startActivity(intent1);
            }
        });

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView)findViewById(R.id.nav_View);

        mNavigationView.setItemIconTintList(null);

        View header = mNavigationView.getHeaderView(0);
        TextView mainnav_header_name = (TextView)header.findViewById(R.id.nav_header_name);
        TextView mainnav_header_membertype = (TextView)header.findViewById(R.id.nav_header_membertype);
        TextView mainnav_header_nickname = (TextView)header.findViewById(R.id.nav_header_nickname);
        ImageView mainnav_header_profile = (ImageView)header.findViewById(R.id.img_profilepic);

        try{
            String name = user.get(sessionManager.NICKNAME);
            HttpURLConnection.setFollowRedirects(false);

            HttpURLConnection con = (HttpURLConnection)new URL("http://101.101.162.32:8080/profilepic/"+name+".png").openConnection();

            con.setRequestMethod("HEAD");

            if(con.getResponseCode() == HttpURLConnection.HTTP_OK){
                mainnav_header_profile.setBackground(new ShapeDrawable(new OvalShape()));
                mainnav_header_profile.setClipToOutline(true);
                String pic = "http://101.101.162.32:8080/profilepic/"+name+".png";
                Glide.with(this).load(pic).into(mainnav_header_profile);
            }else{
                mainnav_header_profile.setBackground(new ShapeDrawable(new OvalShape()));
                mainnav_header_profile.setClipToOutline(true);
                String pic = "http://101.101.162.32:8080/profilepic/def.png";
                Glide.with(this).load(pic).into(mainnav_header_profile);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if(user.get(sessionManager.CLASSFI) == "0"){
            mainnav_header_membertype.setText("일반 회원");
        }
        else{
            mainnav_header_membertype.setText("사업자");
        }

        mainnav_header_name.setText(user.get(sessionManager.NAME));
        mainnav_header_nickname.setText(user.get(sessionManager.NICKNAME));


        ImageButton btn1 = (ImageButton)findViewById(R.id.sidebar_btn);
        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                if(!mDrawerLayout.isDrawerOpen(GravityCompat.START)) mDrawerLayout.openDrawer(Gravity.START);
                else mDrawerLayout.closeDrawer(Gravity.END);
            }
        });

        ImageButton btn3 = (ImageButton)findViewById(R.id.d3_btn);
        btn3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, threeD_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        ImageButton btn4 = (ImageButton)findViewById(R.id.myf_btn);
        btn4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent myf_Intent = new Intent(MainActivity.this, Custom_Activity.class);
                startActivity(myf_Intent);
                finish();
            }
        });

        ImageButton btn5 = (ImageButton)findViewById(R.id.comm_btn);
        btn5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, community_activity.class);
                startActivity(intent);
                finish();
            }
        });

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem item){
                item.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = item.getItemId();

                switch(id){
                    case R.id.nav_MyProfile:
                        Intent My_profile_intent = new Intent(getApplicationContext(), Myprofile_Activity.class);
                        startActivity(My_profile_intent);
                        break;

                    case R.id.nav_Help:
                        Intent help_intent = new Intent(getApplicationContext(), Help_Activity.class);
                        startActivity(help_intent);
                        break;


                    case R.id.nav_logout:
                        sessionManager.logout();
                        Toast.makeText(MainActivity.this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_About:
                        Intent about_intent = new Intent(getApplicationContext(), About_Activity.class);
                        startActivity(about_intent);
                        break;

                }

                return true;
            }
        });
        Log.e("Frag", "Fragment");
    }

    // 백 키를 눌렀을 시 경고창을 띄움
    public void onBackPressed()
    {
       android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
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
