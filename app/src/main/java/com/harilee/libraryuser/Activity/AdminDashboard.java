package com.harilee.libraryuser.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.harilee.libraryuser.R;
import com.harilee.libraryuser.Utils.Utility;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdminDashboard extends AppCompatActivity {

    @BindView(R.id.add_user)
    Button addbook;
    @BindView(R.id.see_fine)
    Button seeFine;
    @BindView(R.id.manage_books)
    Button manageBooks;
    @BindView(R.id.view_inventory)
    Button viewInventory;
    @BindView(R.id.student_list)
    Button studentList;
    @BindView(R.id.log_out)
    ImageButton logOut;

    private String TAG = "add book";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("Admin");
    }

    @OnClick({R.id.add_user, R.id.see_fine, R.id.manage_books
            , R.id.view_inventory, R.id.student_list})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_user:
                openAddUserDailog();
                break;
            case R.id.see_fine:
                acceptBook();
                break;
            case R.id.manage_books:
                openAddBookDialog();
                break;
            case R.id.view_inventory:
                startActivity(new Intent(AdminDashboard.this, BookList.class));
                break;
            case R.id.student_list:
                startActivity(new Intent(AdminDashboard.this, StudentList.class));
                break;
        }
    }

    private void acceptBook() {

        RoundedBottomSheetDialog mBottomSheetDialog = new RoundedBottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.accept_book, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();

        EditText bookid = sheetView.findViewById(R.id.book_id);
        Button submit = sheetView.findViewById(R.id.accept_book_bt);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        submit.setOnClickListener(v -> {
            ProgressDialog dialog = ProgressDialog.show(AdminDashboard.this, "",
                    "checking bookid . Please wait...", true);
            String id = bookid.getText().toString().trim();

            db.collection("issue").get()
                    .addOnCompleteListener(task -> {
                        if (task.getResult().size() > 0) {
                            db.collection("issue").document(id)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        DocumentReference issuedBookRef = db.collection("inventory").document(id);
                                        issuedBookRef
                                                .update("available", "1")
                                                .addOnSuccessListener(aVoid1 -> {
                                                    dialog.cancel();
                                                    mBottomSheetDialog.cancel();
                                                    Toast.makeText(getApplicationContext(), "Book accepted successfully", Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, "DocumentSnapshot successfully updated!");

                                                })
                                                .addOnFailureListener(e -> {
                                                    dialog.cancel();
                                                    mBottomSheetDialog.cancel();
                                                    Log.w(TAG, "Error updating document", e);

                                                });
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    })
                                    .addOnFailureListener(e -> {
                                        dialog.cancel();
                                        mBottomSheetDialog.cancel();
                                        Log.w(TAG, "Error deleting document", e);
                                        Toast.makeText(getApplicationContext(), "Book could not be accepted", Toast.LENGTH_SHORT).show();

                                    });
                        } else {
                            dialog.cancel();
                            Toast.makeText(getApplicationContext(), "All books are available", Toast.LENGTH_SHORT).show();
                        }
                    });

        });


    }

    private void openAddUserDailog() {

        RoundedBottomSheetDialog mBottomSheetDialog = new RoundedBottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.add_user_dialog, null);
        mBottomSheetDialog.setContentView(sheetView);

        EditText studentNameEt = sheetView.findViewById(R.id.name_et);
        EditText departmentEt = sheetView.findViewById(R.id.department_et);
        EditText rollNumberEt = sheetView.findViewById(R.id.roll_number_et);
        Button addUser = sheetView.findViewById(R.id.add_user_bt);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        addUser.setOnClickListener(v -> {
            ProgressDialog dialog = ProgressDialog.show(AdminDashboard.this, "",
                    "Adding student. Please wait...", true);
            Map<String, Object> student = new HashMap<>();
            student.put("name", studentNameEt.getText().toString().trim());
            student.put("department", departmentEt.getText().toString().trim());
            student.put("roll_number", rollNumberEt.getText().toString().trim());

            db.collection("students")
                    .whereEqualTo("roll_number", rollNumberEt.getText().toString().trim())
                    .get()
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                Toast.makeText(getApplicationContext(), "Student already added", Toast.LENGTH_SHORT).show();
                            } else {
                                db.collection("students")
                                        .add(student)
                                        .addOnSuccessListener(documentReference -> {
                                            dialog.cancel();
                                            Toast.makeText(getApplicationContext(), "Student added successfully", Toast.LENGTH_SHORT).show();
                                            mBottomSheetDialog.cancel();

                                        })
                                        .addOnFailureListener(e -> {
                                            dialog.cancel();
                                            Toast.makeText(getApplicationContext(), "Error adding document", Toast.LENGTH_SHORT).show();
                                            mBottomSheetDialog.cancel();

                                        });
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                        }
                    });
            // Add a new document with a generated ID

        });

        mBottomSheetDialog.show();


    }

    private void openAddBookDialog() {

        RoundedBottomSheetDialog mBottomSheetDialog = new RoundedBottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.add_books, null);
        mBottomSheetDialog.setContentView(sheetView);

        EditText bookNameEt = sheetView.findViewById(R.id.book_name_et);
        EditText subtitleEt = sheetView.findViewById(R.id.subtitle_et);
        EditText descriptionEt = sheetView.findViewById(R.id.description_et);
        EditText genreEt = sheetView.findViewById(R.id.genre_et);
        EditText authorEt = sheetView.findViewById(R.id.author_et);
        Button addBook = sheetView.findViewById(R.id.add_book_bt);
        final Integer[] bookCount = {0};
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("inventory")
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Log.e(TAG, "openAddbookDialog: " + task.getResult().size());
                        bookCount[0] = task.getResult().size();
                        /*for (QueryDocumentSnapshot document : task.getResult()) {
                        }*/
                    } else {
                        Toast.makeText(getApplicationContext(), "Error getting books", Toast.LENGTH_SHORT).show();
                    }
                });
        addBook.setOnClickListener(v -> {
            ProgressDialog dialog = ProgressDialog.show(AdminDashboard.this, "",
                    "Adding Book. Please wait...", true);
            Map<String, Object> book = new HashMap<>();
            book.put("title", bookNameEt.getText().toString().trim());
            book.put("subtitle", subtitleEt.getText().toString().trim());
            book.put("genre", genreEt.getText().toString().trim());
            book.put("description", descriptionEt.getText().toString().trim());
            book.put("author", authorEt.getText().toString().trim());
            Integer count = bookCount[0];
            book.put("bookId", count);
            book.put("available", "1");


            // Add a new document with a generated ID

            db.collection("inventory").document("book" + count)
                    .set(book)
                    .addOnSuccessListener(documentReference -> {
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(), "Book added successfully", Toast.LENGTH_SHORT).show();
                        mBottomSheetDialog.cancel();

                    })
                    .addOnFailureListener(e -> {
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(), "Error adding document", Toast.LENGTH_SHORT).show();
                        mBottomSheetDialog.cancel();

                    });
        });

        mBottomSheetDialog.show();


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @OnClick(R.id.log_out)
    public void onViewClicked() {
        Utility.getUtilityInstance().setPreference(getApplicationContext(), "IS_LOGIN", "no");
        startActivity(new Intent(AdminDashboard.this, LoginActivity.class));
    }
}
