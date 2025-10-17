package com.example.calcul;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText etMonthlyIncome, etAmount, etDescription;
    private Spinner spinnerCategory;
    private Button btnAddExpense, btnCalculate;
    private TextView tvFoodExpense, tvTravelExpense, tvTotalExpense, tvSavings, tvCapital;

    private DatabaseHelper dbHelper;
    private double monthlyIncome = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        etMonthlyIncome = findViewById(R.id.etMonthlyIncome);
        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnAddExpense = findViewById(R.id.btnAddExpense);
        btnCalculate = findViewById(R.id.btnCalculate);
        tvFoodExpense = findViewById(R.id.tvFoodExpense);
        tvTravelExpense = findViewById(R.id.tvTravelExpense);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        tvSavings = findViewById(R.id.tvSavings);
        tvCapital = findViewById(R.id.tvCapital);

        btnAddExpense.setOnClickListener(v -> addExpense());
        btnCalculate.setOnClickListener(v -> calculateSavings());
    }

    private void addExpense() {
        String category = spinnerCategory.getSelectedItem().toString();
        String amountStr = etAmount.getText().toString();
        String description = etDescription.getText().toString();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        boolean success = dbHelper.addExpense(category, amount, currentDate, description);

        if (success) {
            Toast.makeText(this, "Expense added successfully!", Toast.LENGTH_SHORT).show();
            etAmount.setText("");
            etDescription.setText("");
        } else {
            Toast.makeText(this, "Failed to add expense", Toast.LENGTH_SHORT).show();
        }
    }

    private void calculateSavings() {
        String incomeStr = etMonthlyIncome.getText().toString();

        if (incomeStr.isEmpty()) {
            Toast.makeText(this, "Please enter your monthly income", Toast.LENGTH_SHORT).show();
            return;
        }

        monthlyIncome = Double.parseDouble(incomeStr);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String currentMonth = sdf.format(new Date());

        double foodExpense = dbHelper.getMonthlyExpenseByCategory("Food", currentMonth);
        double travelExpense = dbHelper.getMonthlyExpenseByCategory("Motorcycle Travel", currentMonth);
        double totalExpense = dbHelper.getTotalMonthlyExpense(currentMonth);

        double monthlySavings = monthlyIncome - totalExpense;
        double yearlyCapital = monthlySavings * 12;

        tvFoodExpense.setText(String.format("Food: %.2f MAD", foodExpense));
        tvTravelExpense.setText(String.format("Motorcycle Travel: %.2f MAD", travelExpense));
        tvTotalExpense.setText(String.format("Total Expenses: %.2f MAD", totalExpense));
        tvSavings.setText(String.format("Monthly Savings: %.2f MAD", monthlySavings));
        tvCapital.setText(String.format("Yearly Capital: %.2f MAD", yearlyCapital));

        if (monthlySavings > 0) {
            Toast.makeText(this, "Great! You're saving money!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Warning: You're spending more than you earn!", Toast.LENGTH_LONG).show();
        }
    }
}
