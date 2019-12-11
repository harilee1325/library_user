package com.harilee.libraryuser.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.auth.User;
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
    private String TAG = "Login";

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
        } else if (rollNumnber.equalsIgnoreCase("admin")){
            Utility.getUtilityInstance().setPreference(getApplicationContext(), "IS_LOGIN", "admin");
            startActivity(new Intent(LoginActivity.this, AdminDashboard.class));

        }
        else {
            ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "",
                    "Issuing. Please wait...", true);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("students")
                    .whereEqualTo("roll_number"
                            , rollNumnber)
                    .get()
                    .addOnCompleteListener(task -> {
                        dialog.cancel();
                        if (task.isSuccessful()) {
                            if (task.getResult().size()>0){
                                Utility.getUtilityInstance().setPreference(getApplicationContext(), "ROLL_NUM", rollNumberEt.getText().toString().trim());
                                startActivity(new Intent(LoginActivity.this, UserDashboard.class));
                                Utility.getUtilityInstance().setPreference(getApplicationContext(), "IS_LOGIN", "yes");
                            }else{
                                Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
