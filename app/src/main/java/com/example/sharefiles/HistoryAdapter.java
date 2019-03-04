package com.example.sharefiles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private Context context;
    private List<HistoryModel> notesList;
    public SparseBooleanArray booleanArray = new SparseBooleanArray();
    public  static boolean selectAllFlag = false;

    public static void selectAll() {
        selectAllFlag = true;
    }

    public static void DisSelectAll() {
        selectAllFlag=false;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView note;
        public ImageView dot;
        public TextView timestamp;
        public CheckBox checkBox;
        RelativeLayout relativeLayout;

        public MyViewHolder(View view) {
            super(view);
            note = view.findViewById(R.id.note);
            dot = view.findViewById(R.id.dot);
            timestamp = view.findViewById(R.id.timestamp);
            checkBox = view.findViewById(R.id.checkbox1);
            relativeLayout = view.findViewById(R.id.history_list_row);
            checkBox.setOnClickListener(this);
            relativeLayout.setOnLongClickListener((HistoryActivity) context);

        }
        @Override
        public void onClick(View view) {

            if (booleanArray.get(getAdapterPosition())) {
                booleanArray.put(getAdapterPosition(), false);
            } else {
                booleanArray.put(getAdapterPosition(), true);
            }
            ((HistoryActivity) context).updateCheckedData(view, getAdapterPosition());
        }
    }


    public HistoryAdapter(Context context, List<HistoryModel> notesList) {
        this.context = context;
        this.notesList = notesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HistoryModel note = notesList.get(position);

        holder.note.setText(note.getUrlpath());
        Log.d("PathisThat", note.getUrlpath());

        // Displaying image from url
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(note.getUrlpath(), options);
        if (options.outWidth != -1 && options.outHeight != -1) {
            // This is an image file.
            Glide.with(context).load(note.getUrlpath()).into(holder.dot);

        } else if (note.getUrlpath().endsWith(".3gp") || note.getUrlpath().endsWith(".mp4") || note.getUrlpath().endsWith(".mov") || note.getUrlpath().endsWith(".flv")) {

            Glide.with(context).load(note.getUrlpath()).into(holder.dot);
        } else {
            // This is not an image file.
            Log.d("this is Video", "Not image");
            holder.dot.setImageResource(com.github.angads25.filepicker.R.mipmap.ic_type_file);

        }

        holder.timestamp.setText(formatDate(note.getTimestamp()));

        //onLong Press
        if (((HistoryActivity) context).is_Long_click_mode) {
            holder.checkBox.setVisibility(View.VISIBLE);
            //holder.checkBox.setChecked(false);
            if (booleanArray.get(position)) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }

        } else {
            holder.checkBox.setVisibility(View.GONE);
        }

/*        if (((HistoryActivity) context).selectAllFlag) {

            Log.d("HELOOOO", "HELLO");
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }*/
        if (selectAllFlag){
            holder.checkBox.setChecked(true);
        }else {
            holder.checkBox.setChecked(false);
        }
    }


    @Override
    public int getItemCount() {
        return notesList.size();
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }

}
