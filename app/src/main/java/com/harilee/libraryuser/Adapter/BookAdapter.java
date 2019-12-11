package com.harilee.libraryuser.Adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.harilee.libraryuser.Activity.UserDashboard;
import com.harilee.libraryuser.Models.BookModel;
import com.harilee.libraryuser.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {



    private Context context;
    private ArrayList<BookModel> bookModels;
    private UserDashboard userDashboard;
    private Boolean isClickable;

    public BookAdapter(UserDashboard userDashboard, ArrayList<BookModel> bookModels, Context applicationContext) {
        this.context = applicationContext;
        this.bookModels = bookModels;
        this.userDashboard = userDashboard;
        isClickable = true;
    }

    public BookAdapter(ArrayList<BookModel> bookModels, Context applicationContext) {
        this.context = applicationContext;
        this.bookModels = bookModels;
        isClickable = false;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.book_list, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (isClickable) {
            holder.subtitleTv.setText(bookModels.get(position).getSubTitile());
        }else {
            holder.subtitleTv.setText(bookModels.get(position).getId());

        }
        holder.titleBookTv.setText(bookModels.get(position).getName());
        if (bookModels.get(position).getAvailable().equalsIgnoreCase("1")){
            holder.availableTv.setText("Available");
            holder.availableTv.setTextColor(context.getResources().getColor(R.color.green));
        }else{
            holder.availableTv.setText("Un available");
            holder.availableTv.setTextColor(context.getResources().getColor(R.color.red));
        }
        if (isClickable) {
            holder.bookCard.setOnClickListener(v -> {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Calendar c = Calendar.getInstance();
                c.setTime(new Date()); // Now use today date.
                c.add(Calendar.DATE, 15); // Adding 5 days
                String output = sdf.format(c.getTime());


                userDashboard.issueBook(bookModels.get(position).getName(), bookModels.get(position).getAuthor()
                        , bookModels.get(position).getDescription(), output, bookModels.get(position).getId()
                        , bookModels.get(position).getAvailable());
            });
        }

    }

    @Override
    public int getItemCount() {
        return bookModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.available_tv)
        TextView availableTv;
        @BindView(R.id.title_book_tv)
        TextView titleBookTv;
        @BindView(R.id.subtitle_tv)
        TextView subtitleTv;
        @BindView(R.id.book_card)
        CardView bookCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
