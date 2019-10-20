package kr.ac.yju.com.im;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
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

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    SessionManager sessionManager;
    private Button btn_logout;
    public String MemberType;
    HttpClient httpclient;
    HttpPost httppost;
    HttpResponse response;
    List<NameValuePair> nameValuePairs;

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


        if(SplashActivity.mescheck == 1){
            messeageCheck(mId);
        }


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

        if(sessionManager.CLASSFI == "1")
        {
            mainnav_header_membertype.setText("사업자");
        }
        else
        {
            mainnav_header_membertype.setText("일반 회원");
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

    void messeageCheck(String mId) {
        try{
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://101.101.162.32/MessageCheck.php");
            nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("username",mId));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpclient.execute(httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost,responseHandler);
            System.out.print("Response:" + response);

            if(response.equalsIgnoreCase("Message Found")){
                SplashActivity.mescheck = 0;
                createNotification();
            }else {
                SplashActivity.mescheck = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void createNotification() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, MesActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_notiicon)) //BitMap 이미지 요구
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setContentText("새로운 메시지가 있습니다.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 MesActivity로 이동하도록 설정
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_stat_notiicon); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            builder.setColor(ContextCompat.getColor(this, R.color.colorAccent));
            CharSequence channelName  = "IM 채널";
            String description = "오레오 이상을 위한 것";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel("default", channelName , importance);
            channel.setDescription(description);

            // 노티피케이션 채널을 시스템에 등록
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        }else {
            builder.setSmallIcon(R.mipmap.ic_stat_notiicon); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남
            builder.setColor(ContextCompat.getColor(this, R.color.colorAccent));
        }

        assert notificationManager != null;
        notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작시킴

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
