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
    private OnTodoListner onTodoListner;

    public ToDoListViewAdapter(Context ct, String listArray[], String descriptionArray[], OnTodoListner onTodoListner){
        context = ct;
        dataArray1 = listArray;
        dataArray2 = descriptionArray;
        this.onTodoListner = onTodoListner;
    }

    @NonNull
    @Override
    public ToDoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.to_do_list_row, parent, false);
        return new ToDoListViewHolder(view, onTodoListner);
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

    public class ToDoListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title, description;
        OnTodoListner onTodoListner;

        public ToDoListViewHolder(@NonNull View itemView, OnTodoListner onTodoListner) {
            super(itemView);
            title = itemView.findViewById(R.id.todo_title_text);
            description = itemView.findViewById(R.id.todo_desc_text);
            this.onTodoListner = onTodoListner;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onTodoListner.onTodoClick(getAdapterPosition());
        }
    }

    public interface OnTodoListner{
        void onTodoClick(int position);

    }
}
