package com.harilee.libraryuser.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.harilee.libraryuser.Adapter.BookAdapter;
import com.harilee.libraryuser.Models.BookModel;
import com.harilee.libraryuser.Models.IssueModel;
import com.harilee.libraryuser.R;
import com.harilee.libraryuser.Utils.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserDashboard extends AppCompatActivity {

    @BindView(R.id.book_list)
    RecyclerView bookList;
    @BindView(R.id.books_due)
    FloatingActionButton booksDue;
    @BindView(R.id.log_out)
    Button logOut;
    private ArrayList<BookModel> bookModels = new ArrayList<>();
    private BookAdapter adapter;
    private String TAG = "Book";
    private ArrayList<Drawable> bookImages = new ArrayList<android.graphics.drawable.Drawable>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);
        ButterKnife.bind(this);
        bookImages.add(this.getDrawable(R.drawable.book));
        bookImages.add(this.getDrawable(R.drawable.book2));
        bookImages.add(this.getDrawable(R.drawable.book3));
        bookImages.add(this.getDrawable(R.drawable.book4));
        bookImages.add(this.getDrawable(R.drawable.book5));
        adapter = new BookAdapter(this, bookModels, getApplicationContext(), bookImages);
        bookList.setAdapter(adapter);

        getBooks();
    }

    public void issueBook(String name, String author, String description, String format, String id, String available) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        RoundedBottomSheetDialog mBottomSheetDialog = new RoundedBottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.issue_book, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();

        TextView nameTv = sheetView.findViewById(R.id.name_tv);
        TextView authorTv = sheetView.findViewById(R.id.author_tv);
        TextView descriptionTv = sheetView.findViewById(R.id.description_tv);
        TextView date = sheetView.findViewById(R.id.date_tv);
        Button issueBook = sheetView.findViewById(R.id.issue_book_bt);

        nameTv.setText("Name: " + name);
        authorTv.setText("Author: " + author);
        descriptionTv.setText("Description: " + description);
        date.setText("Due Date: " + format);

        //checking if the book is available or not
        Log.e(TAG, "issueBook: " + available);
        if (!available.equalsIgnoreCase("1")) {
            Toast.makeText(getApplicationContext(), "Book is not available", Toast.LENGTH_SHORT).show();
            issueBook.setEnabled(false);
        }

        issueBook.setOnClickListener(v -> {
            ProgressDialog dialog = ProgressDialog.show(UserDashboard.this, "",
                    "Issuing. Please wait...", true);
            Map<String, Object> book = new HashMap<>();
            book.put("bookid", id);
            book.put("date", format);
            book.put("student", Utility.getUtilityInstance().getPreference(getApplicationContext(), "ROLL_NUM"));

            db.collection("issue").document(id)
                    .set(book)
                    .addOnSuccessListener(documentReference -> {

                        //updating the available book section to 0
                        DocumentReference issuedBookRef = db.collection("inventory").document(id);

                        issuedBookRef
                                .update("available", "0")
                                .addOnSuccessListener(aVoid -> {
                                    dialog.cancel();
                                    getBooks();
                                    Log.d(TAG, "DocumentSnapshot successfully updated!");

                                })
                                .addOnFailureListener(e -> {
                                    dialog.cancel();
                                    Log.w(TAG, "Error updating document", e);

                                });

                        Toast.makeText(getApplicationContext(), "Book issued successfully", Toast.LENGTH_SHORT).show();
                        mBottomSheetDialog.cancel();

                    })
                    .addOnFailureListener(e -> {

                        dialog.cancel();
                        Toast.makeText(getApplicationContext(), "Error issuing book", Toast.LENGTH_SHORT).show();
                        mBottomSheetDialog.cancel();

                    });
        });


    }

    private void getBooks() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ProgressDialog dialog = ProgressDialog.show(UserDashboard.this, "",
                "Loading. Please wait...", true);
        db.collection("inventory")
                .get()
                .addOnCompleteListener(task -> {
                    dialog.cancel();
                    if (task.isSuccessful()) {
                        bookModels.clear();
                        Log.e(TAG, "openAddbookDialog: " + task.getResult().size());
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.e(TAG, document.getId() + " => " + document.getData().get("author"));
                            BookModel bookModel = new BookModel();
                            bookModel.setAuthor(String.valueOf(document.getData().get("author")));
                            bookModel.setDescription(String.valueOf(document.getData().get("description")));
                            bookModel.setGenre(String.valueOf(document.getData().get("genre")));
                            bookModel.setSubTitile(String.valueOf(document.getData().get("subtitle")));
                            bookModel.setName(String.valueOf(document.getData().get("title")));
                            bookModel.setAvailable(String.valueOf(document.getData().get("available")));
                            bookModel.setId(document.getId());
                            bookModels.add(bookModel);
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error getting books", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getDue() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ProgressDialog dialog = ProgressDialog.show(UserDashboard.this, "",
                "Getting due books. Please wait...", true);
        db.collection("issue")
                .whereEqualTo("student"
                        , Utility.getUtilityInstance().getPreference(getApplicationContext(), "ROLL_NUM"))
                .get()
                .addOnCompleteListener(task -> {
                    dialog.cancel();
                    if (task.isSuccessful()) {

                        if (task.getResult().size()>0) {
                            RoundedBottomSheetDialog mBottomSheetDialog = new RoundedBottomSheetDialog(this);
                            View sheetView = getLayoutInflater().inflate(R.layout.fine_list, null);
                            mBottomSheetDialog.setContentView(sheetView);
                            mBottomSheetDialog.show();

                            RecyclerView dueList = sheetView.findViewById(R.id.fine_list);
                            ArrayList<IssueModel> issueModels = new ArrayList<>();
                            FineAdapter adapter = new FineAdapter(this, getApplicationContext(), issueModels);
                            dueList.setAdapter(adapter);

                            IssueModel issueModel;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                issueModel = new IssueModel();
                                issueModel.setBookId((document.getId()));
                                issueModel.setStudentNumber(String.valueOf(document.getData().get("student")));
                                issueModel.setDayLeft(String.valueOf(document.getData().get("date")));
                                issueModels.add(issueModel);

                            }
                            adapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(getApplicationContext(), "No books issued in your name", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

    }

    @OnClick({R.id.log_out, R.id.books_due})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.log_out:
                Utility.getUtilityInstance().setPreference(getApplicationContext(), "IS_LOGIN", "no");
                startActivity(new Intent(UserDashboard.this, LoginActivity.class));
                break;
            case R.id.books_due:
                getDue();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public class FineAdapter extends RecyclerView.Adapter<FineAdapter.ViewHolder> {


        private Context context;
        private ArrayList<IssueModel> issueModels;

        public FineAdapter(UserDashboard userDashboard, Context applicationContext, ArrayList<IssueModel> issueModels) {

            this.context = applicationContext;
            this.issueModels = issueModels;

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.fine_adapter, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.bookIdTv.setText("Book ID: " + issueModels.get(position).getBookId());
            holder.daysLeftTv.setText("Due in: " + issueModels.get(position).getDayLeft());
            holder.studentNumberTv.setText("Student: " + issueModels.get(position).getStudentNumber());
        }

        @Override
        public int getItemCount() {
            return issueModels.size();
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
