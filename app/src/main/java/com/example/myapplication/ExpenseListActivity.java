package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

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

        recyclerView = findViewById(R.id.recyclerViewExpenses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);

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
    protected void onResume() {
        super.onResume();
        loadExpenses();
    }
}
