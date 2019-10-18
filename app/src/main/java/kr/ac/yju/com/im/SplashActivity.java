package kr.ac.yju.com.im;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {

    public static int mescheck = 0;

    @Override

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        try{
            Thread.sleep(3000);
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
        mescheck = 1;
        startActivity(new Intent(this, loginActivity.class));
        finish();
    }
}
