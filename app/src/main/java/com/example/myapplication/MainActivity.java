package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText etMonthlyIncome, etAmount, etDescription;
    private Spinner spinnerCategory;
    private Button btnAddExpense, btnViewExpenses, btnSaveIncome;
    private TextView tvIncome, tvTotalExpense, tvRemaining;

    private DatabaseHelper dbHelper;
    private double monthlyIncome = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize DB
        dbHelper = new DatabaseHelper(this);

        // Initialize UI
        etMonthlyIncome = findViewById(R.id.etMonthlyIncome);
        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnAddExpense = findViewById(R.id.btnAddExpense);
        btnViewExpenses = findViewById(R.id.btnViewExpenses);
        btnSaveIncome = findViewById(R.id.btnSaveIncome);
        tvIncome = findViewById(R.id.tvIncome);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        tvRemaining = findViewById(R.id.tvRemaining);

        // Set Spinner Adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.categories_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Button Listeners
        btnSaveIncome.setOnClickListener(v -> saveIncome());
        btnAddExpense.setOnClickListener(v -> addExpense());
        btnViewExpenses.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ExpenseListActivity.class)));
    }

    private void saveIncome() {
        String incomeStr = etMonthlyIncome.getText().toString();
        if (incomeStr.isEmpty()) {
            Toast.makeText(this, "Please enter your income", Toast.LENGTH_SHORT).show();
            return;
        }
        monthlyIncome = Double.parseDouble(incomeStr);
        updateSummary();
        Toast.makeText(this, "Income saved!", Toast.LENGTH_SHORT).show();
    }

    private void addExpense() {
        Object selectedItem = spinnerCategory.getSelectedItem();
        if (selectedItem == null) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        String category = selectedItem.toString();
        String amountStr = etAmount.getText().toString();
        String description = etDescription.getText().toString();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        boolean success = dbHelper.addExpense(category, amount, currentDate, description);
        if (success) {
            Toast.makeText(this, "Expense added!", Toast.LENGTH_SHORT).show();
            etAmount.setText("");
            etDescription.setText("");
            updateSummary();
        } else {
            Toast.makeText(this, "Failed to add expense", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSummary() {
        String currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
        double totalExpense = dbHelper.getAllExpenses().stream()
                .filter(exp -> exp.getDate().startsWith(currentMonth))
                .mapToDouble(Expense::getAmount)
                .sum();
        double remaining = monthlyIncome - totalExpense;

        tvIncome.setText(String.format("Monthly Income: %.2f MAD", monthlyIncome));
        tvTotalExpense.setText(String.format("Total Spent: %.2f MAD", totalExpense));
        tvRemaining.setText(String.format("Remaining: %.2f MAD", remaining));
    }
}
