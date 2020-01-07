package com.harilee.libraryuser.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.harilee.libraryuser.Models.IssueModel;
import com.harilee.libraryuser.Models.StudentModel;
import com.harilee.libraryuser.R;
import com.harilee.libraryuser.Utils.Utility;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentList extends AppCompatActivity {

    @BindView(R.id.fine_list)
    RecyclerView fineList;
    private StudentAdapter adapter;
    private ArrayList<StudentModel> studentData = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fine_list);
        ButterKnife.bind(this);

        adapter = new StudentAdapter(this, getApplicationContext(), studentData);
        fineList.setAdapter(adapter);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ProgressDialog dialog = ProgressDialog.show(StudentList.this, "",
                "Getting students data. Please wait...", true);
        db.collection("students")
                .get()
                .addOnCompleteListener(task -> {
                    dialog.cancel();
                    if (task.isSuccessful()) {

                        if (task.getResult().size()>0) {
                            StudentModel studentModel;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                studentModel = new StudentModel();
                                studentModel.setDepartment(String.valueOf((document.getData().get("department"))));
                                studentModel.setName(String.valueOf(document.getData().get("name")));
                                studentModel.setRollNumber(String.valueOf(document.getData().get("roll_number")));
                                studentData.add(studentModel);

                            }
                            adapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(getApplicationContext(), "No books issued in your name", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Some error occurred please try after sometime.", Toast.LENGTH_SHORT).show();

                    }
                });

    }

    public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {


        private Context context;
        private ArrayList<StudentModel> studentData;

        public StudentAdapter(StudentList studentList, Context applicationContext, ArrayList<StudentModel> studentData) {
            this.context = applicationContext;
            this.studentData = studentData;

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.fine_adapter, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bookIdTv.setText("Student Name: " + studentData.get(position).getName());
            holder.daysLeftTv.setText("Department: " + studentData.get(position).getDepartment());
            holder.studentNumberTv.setText("Roll Number: " + studentData.get(position).getRollNumber());
        }

        @Override
        public int getItemCount() {
            return studentData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.student_number_tv)
            TextView studentNumberTv;
            @BindView(R.id.book_id_tv)
            TextView bookIdTv;
            @BindView(R.id.days_left_tv)
            TextView daysLeftTv;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
