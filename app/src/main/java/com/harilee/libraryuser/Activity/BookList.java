package com.harilee.libraryuser.Activity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.harilee.libraryuser.Adapter.BookAdapter;
import com.harilee.libraryuser.Models.BookModel;
import com.harilee.libraryuser.R;
import com.harilee.libraryuser.Utils.Utility;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookList extends AppCompatActivity {

    @BindView(R.id.book_list_admin)
    RecyclerView bookListAdmin;
    private BookAdapter bookAdapter ;
    private ArrayList<BookModel> bookModels = new ArrayList<>();
    private String TAG = "Admin Book";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_view_admin);
        ButterKnife.bind(this);

        bookAdapter = new BookAdapter( bookModels, getApplicationContext());
        bookListAdmin.setAdapter(bookAdapter);
        getBooks();
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

                        bookAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error getting books", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
