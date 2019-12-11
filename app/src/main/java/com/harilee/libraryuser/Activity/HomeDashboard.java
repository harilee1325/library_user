package com.harilee.libraryuser.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.harilee.libraryuser.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeDashboard extends AppCompatActivity {

    @BindView(R.id.student_card)
    CardView studentCard;
    @BindView(R.id.admin_card)
    CardView adminCard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.student_card, R.id.admin_card})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.student_card:
                startActivity(new Intent(HomeDashboard.this, UserDashboard.class));
                break;
            case R.id.admin_card:
                startActivity(new Intent(HomeDashboard.this, AdminDashboard.class));

                break;
        }
    }
}
