package com.harilee.libraryuser.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.harilee.libraryuser.Adapter.BookAdapter;
import com.harilee.libraryuser.Models.BookModel;
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
    private ArrayList<BookModel> bookModels = new ArrayList<>();
    private BookAdapter adapter;
    private String TAG = "Book";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);
        ButterKnife.bind(this);

        adapter = new BookAdapter(this, bookModels, getApplicationContext());
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
        date.setText("Date: " + format);

        //checking if the book is available or not
        if (!available.equalsIgnoreCase("1")) {
            Toast.makeText(getApplicationContext(), "Book is not available", Toast.LENGTH_SHORT).show();
            issueBook.setEnabled(false);
        }

        issueBook.setOnClickListener(v -> {

            final Integer[] bookCount = {0};
            db.collection("issue")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            bookCount[0] = task.getResult().size();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error getting data", Toast.LENGTH_SHORT).show();
                        }
                    });
            Map<String, Object> book = new HashMap<>();
            book.put("bookid", id);
            book.put("date", format);
            book.put("student", Utility.getUtilityInstance().getPreference(getApplicationContext(), "ROLL_NUM"));
            Integer count = bookCount[0];

            db.collection("issue").document("book" + count)
                    .set(book)
                    .addOnSuccessListener(documentReference -> {

                        //updating the available book section to 0
                        DocumentReference issuedBookRef = db.collection("inventory").document(id);

                        issuedBookRef
                                .update("available", "0")
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));

                        Toast.makeText(getApplicationContext(), "Book issued successfully", Toast.LENGTH_SHORT).show();
                        mBottomSheetDialog.cancel();

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Error issuing book", Toast.LENGTH_SHORT).show();
                        mBottomSheetDialog.cancel();

                    });
        });


    }

    private void getBooks() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("inventory")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
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

    @OnClick(R.id.books_due)
    public void onViewClicked() {

        getDue();

    }

    private void getDue() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("issue")
                .whereEqualTo("student"
                        , Utility.getUtilityInstance().getPreference(getApplicationContext(), "ROLL_NUM"))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

    }
}
