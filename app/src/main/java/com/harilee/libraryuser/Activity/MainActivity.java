package com.harilee.libraryuser.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.harilee.libraryuser.R;
import com.harilee.libraryuser.Utils.Utility;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String isLogin = Utility.getUtilityInstance().getPreference(getApplicationContext(), "IS_LOGIN");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isLogin.equalsIgnoreCase("yes")) {
                    startActivity(new Intent(MainActivity.this, UserDashboard.class));
                }else if (isLogin.equalsIgnoreCase("admin")){
                    startActivity(new Intent(MainActivity.this, AdminDashboard.class));
                }else{
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));

                }
            }
        }, 2000);


    }
}
