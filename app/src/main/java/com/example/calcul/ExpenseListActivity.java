package com.example.calcul;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ExpenseListActivity extends AppCompatActivity implements ExpenseAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<Expense> expenseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("All Expenses");

        recyclerView = findViewById(R.id.recyclerViewExpenses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);

        FloatingActionButton fab = findViewById(R.id.fabAddExpense);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(ExpenseListActivity.this, EditExpenseActivity.class);
            startActivity(intent);
        });

        loadExpenses();
    }

    private void loadExpenses() {
        expenseList = dbHelper.getAllExpenses();
        adapter = new ExpenseAdapter(expenseList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onEditClick(int position) {
        Expense expense = expenseList.get(position);
        Intent intent = new Intent(this, EditExpenseActivity.class);
        intent.putExtra("expense_id", expense.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(int position) {
        Expense expense = expenseList.get(position);
        boolean deleted = dbHelper.deleteExpense(expense.getId());
        if (deleted) {
            Toast.makeText(this, "Expense deleted", Toast.LENGTH_SHORT).show();
            loadExpenses();
        } else {
            Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(int position) {
        showExpenseDetailsDialog(expenseList.get(position));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenses();
    }

    private void showExpenseDetailsDialog(Expense expense) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_expense_details, null);

        TextView tvDetailCategory = dialogView.findViewById(R.id.tvDetailCategory);
        TextView tvDetailAmount = dialogView.findViewById(R.id.tvDetailAmount);
        TextView tvDetailDate = dialogView.findViewById(R.id.tvDetailDate);
        TextView tvDetailDescription = dialogView.findViewById(R.id.tvDetailDescription);

        tvDetailCategory.setText("Category: " + expense.getCategory());
        tvDetailAmount.setText("Amount: " + String.format("%.2f MAD", expense.getAmount()));
        tvDetailDate.setText("Date: " + expense.getDate());
        tvDetailDescription.setText("Description: " + expense.getDescription());

        new AlertDialog.Builder(this)
                .setTitle("Expense Details")
                .setView(dialogView)
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
