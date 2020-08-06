package com.hextech.smarttime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hextech.smarttime.util.DBHelper;
import com.hextech.smarttime.util.Utilities;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ToDoListViewAdapter extends RecyclerView.Adapter<ToDoListViewAdapter.ToDoListViewHolder> {

    Context context;
    private OnTodoListner onTodoListner;
    DBHelper dataBase;

    public ToDoListViewAdapter(Context ct, OnTodoListner onTodoListner, DBHelper dataBase1){
        context = ct;
        this.onTodoListner = onTodoListner;
        dataBase = dataBase1;
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
//        holder.title.setText(dataArray1[position]);
//        holder.description.setText(dataArray2[position]);

        holder.title.setText(dataBase.getAllData(context).get(position).getTitle());
        holder.description.setText(dataBase.getAllData(context).get(position).getDescription());
        String date1 = Utilities.convertDateToString(dataBase.getAllData(context).get(position).getCreatedDate());
        holder.date.setText("Due Date : " + date1);

    }

    @Override
    public int getItemCount() {
//        return dataArray1.length;
        return dataBase.getAllData(context).size();
    }

    public class ToDoListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title, description, date;
        OnTodoListner onTodoListner;

        public ToDoListViewHolder(@NonNull View itemView, OnTodoListner onTodoListner) {
            super(itemView);
            title = itemView.findViewById(R.id.todo_title_text);
            description = itemView.findViewById(R.id.todo_desc_text);
            date = itemView.findViewById(R.id.todo_date_text);
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
