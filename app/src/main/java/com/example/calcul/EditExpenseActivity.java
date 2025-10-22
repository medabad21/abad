package com.example.calcul;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditExpenseActivity extends AppCompatActivity {

    private TextInputEditText etCategory, etAmount, etDescription;
    private DatabaseHelper dbHelper;

    private int expenseId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        // --- Toolbar Setup ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // --- Initialize Views ---
        etCategory = findViewById(R.id.etCategory);
        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);

        // --- Database Helper ---
        dbHelper = new DatabaseHelper(this);

        // --- Get Expense ID from Intent ---
        expenseId = getIntent().getIntExtra("expense_id", -1);

        // --- Setup UI based on Add or Edit mode ---
        if (expenseId != -1) {
            getSupportActionBar().setTitle("Edit Expense");
            loadExpenseData();
        } else {
            getSupportActionBar().setTitle("Add Expense");
        }

        // --- FAB Listener ---
        FloatingActionButton fab = findViewById(R.id.fabSaveExpense);
        fab.setOnClickListener(view -> saveExpense());
    }

    private void loadExpenseData() {
        Expense expense = dbHelper.getExpenseById(expenseId);
        if (expense != null) {
            etCategory.setText(expense.getCategory());
            etAmount.setText(String.valueOf(expense.getAmount()));
            etDescription.setText(expense.getDescription());
        } else {
            Toast.makeText(this, "Error: Expense not found", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if expense is not found
        }
    }

    private void saveExpense() {
        // --- Get Input Data ---
        String category = etCategory.getText() != null ? etCategory.getText().toString() : "";
        String amountStr = etAmount.getText() != null ? etAmount.getText().toString() : "";
        String description = etDescription.getText() != null ? etDescription.getText().toString() : "";

        // --- Validate Input ---
        if (category.isEmpty()) {
            etCategory.setError("Category cannot be empty");
            return;
        }
        if (amountStr.isEmpty()) {
            etAmount.setError("Amount cannot be empty");
            return;
        }

        // --- Parse Amount ---
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            etAmount.setError("Invalid number format");
            return;
        }

        // --- Get Current Date ---
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // --- Save to Database (Add or Update) ---
        boolean success;
        if (expenseId == -1) {
            success = dbHelper.addExpense(category, amount, currentDate, description);
        } else {
            success = dbHelper.updateExpense(expenseId, category, amount, currentDate, description);
        }

        // --- Show Feedback ---
        if (success) {
            Toast.makeText(this, (expenseId == -1) ? "Expense added" : "Expense updated", Toast.LENGTH_SHORT).show();
            finish(); // Go back to the previous screen
        } else {
            Toast.makeText(this, "Error saving expense", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Handle the back button press
        return true;
    }
}
