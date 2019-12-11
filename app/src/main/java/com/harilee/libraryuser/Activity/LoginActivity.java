package com.harilee.libraryuser.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.harilee.libraryuser.R;
import com.harilee.libraryuser.Utils.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {


    @BindView(R.id.roll_number_et)
    EditText rollNumberEt;
    @BindView(R.id.sumbit_bt)
    FloatingActionButton sumbitBt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.sumbit_bt)
    public void onViewClicked() {

        checkRollNumber();
    }

    private void checkRollNumber() {

        String rollNumnber = rollNumberEt.getText().toString().trim();

        if (rollNumnber.isEmpty()){
            rollNumberEt.setError("Enter roll number to continue");
        }else if (!rollNumnber.contains("RA")){
            rollNumberEt.setError("Enter a valid roll number");
        }else {
            Utility.getUtilityInstance().setPreference(getApplicationContext(), "ROLL_NUM", rollNumberEt.getText().toString().trim());
            startActivity(new Intent(LoginActivity.this, HomeDashboard.class));
        }
    }
}
