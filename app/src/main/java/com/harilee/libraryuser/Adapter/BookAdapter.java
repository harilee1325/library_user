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

import com.harilee.libraryuser.Models.BookModel;
import com.harilee.libraryuser.R;
import com.harilee.libraryuser.Activity.UserDashboard;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {


    private Context context;
    private ArrayList<BookModel> bookModels;
    private UserDashboard userDashboard;

    public BookAdapter(UserDashboard userDashboard, ArrayList<BookModel> bookModels, Context applicationContext) {
        this.context = applicationContext;
        this.bookModels = bookModels;
        this.userDashboard = userDashboard;
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

        holder.subtitleTv.setText(bookModels.get(position).getSubTitile());
        holder.titleBookTv.setText(bookModels.get(position).getName());

        holder.bookCard.setOnClickListener(v->{
            SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy MM dd");

            LocalDateTime now = LocalDateTime.now();
            try {
                Date day1 = myFormat.parse(dtf1.format(now));
                Date day2 = myFormat.parse(("27 04 1997"));
                long diff = day1.getTime() - day2.getTime();
                System.out.println ("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            userDashboard.issueBook(bookModels.get(position).getName(), bookModels.get(position).getAuthor()
            , bookModels.get(position).getDescription(),dtf.format(now) , bookModels.get(position).getId()
            , bookModels.get(position).getAvailable());
        });

    }

    @Override
    public int getItemCount() {
        return bookModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

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
