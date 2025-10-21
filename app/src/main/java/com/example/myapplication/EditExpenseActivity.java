package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditExpenseActivity extends AppCompatActivity {

    private EditText etAmount, etDescription;
    private Spinner spinnerCategory;
    private Button btnUpdate;
    private DatabaseHelper dbHelper;

    private int expenseId;
    private Expense expense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnUpdate = findViewById(R.id.btnUpdate);

        dbHelper = new DatabaseHelper(this);

        expenseId = getIntent().getIntExtra("expense_id", -1);
        if (expenseId == -1) {
            Toast.makeText(this, "Invalid expense ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadExpense();

        btnUpdate.setOnClickListener(v -> updateExpense());
    }
    private void loadExpense() {
        expense = dbHelper.getExpenseById(expenseId);
        if (expense == null) {
            Toast.makeText(this, "Expense not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // set values to views
        etAmount.setText(String.valueOf(expense.getAmount()));
        etDescription.setText(expense.getDescription());

        // Set spinner selection based on category
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
        int spinnerPosition = adapter.getPosition(expense.getCategory());
        spinnerCategory.setSelection(spinnerPosition);
    }

    private void updateExpense() {
        String category = spinnerCategory.getSelectedItem().toString();
        String amountStr = etAmount.getText().toString();
        String description = etDescription.getText().toString();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show();
            return;
        }
        double amount = Double.parseDouble(amountStr);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        boolean success = dbHelper.updateExpense(expenseId, category, amount, currentDate, description);
        if (success) {
            Toast.makeText(this, "Expense updated!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update expense", Toast.LENGTH_SHORT).show();
        }
    }
}


