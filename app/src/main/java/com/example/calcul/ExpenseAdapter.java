package com.example.calcul;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    public interface OnItemClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
        void onItemClick(int position);
    }

    private List<Expense> expenseList;
    private OnItemClickListener listener;

    public ExpenseAdapter(List<Expense> expenseList, OnItemClickListener listener) {
        this.expenseList = expenseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.tvCategory.setText(expense.getCategory());
        holder.tvAmount.setText(String.format("%.2f MAD", expense.getAmount()));
        holder.tvDate.setText(expense.getDate());
        holder.tvDescription.setText(expense.getDescription());

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(position));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(position));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(position));
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvAmount, tvDate, tvDescription;
        ImageButton btnEdit, btnDelete;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
