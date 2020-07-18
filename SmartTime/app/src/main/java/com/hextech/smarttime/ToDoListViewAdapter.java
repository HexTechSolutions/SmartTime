package com.hextech.smarttime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ToDoListViewAdapter extends RecyclerView.Adapter<ToDoListViewAdapter.ToDoListViewHolder> {

    String dataArray1[];
    String dataArray2[];
    Context context;

    public ToDoListViewAdapter(Context ct, String listArray[], String descriptionArray[]){
        context = ct;
        dataArray1 = listArray;
        dataArray2 = descriptionArray;
    }

    @NonNull
    @Override
    public ToDoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.to_do_list_row, parent, false);
        return new ToDoListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoListViewHolder holder, int position) {
        holder.title.setText(dataArray1[position]);
        holder.description.setText(dataArray2[position]);

    }

    @Override
    public int getItemCount() {
        return dataArray1.length;
    }

    public class ToDoListViewHolder extends RecyclerView.ViewHolder {

        TextView title, description;


        public ToDoListViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.todo_title_text);
            description = itemView.findViewById(R.id.todo_desc_text);
        }
    }
}
