package com.example.calcul;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String MONTHLY_INCOME = "monthlyIncome";

    private TextView tvIncome, tvTotalExpense, tvRemaining;
    private RecyclerView rvExpenses;
    private ImageButton btnEditIncome;

    private DatabaseHelper dbHelper;
    private double monthlyIncome = 0;
    private ExpenseAdapter adapter;
    private List<Expense> expenseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DatabaseHelper(this);

        tvIncome = findViewById(R.id.tvIncome);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        tvRemaining = findViewById(R.id.tvRemaining);
        rvExpenses = findViewById(R.id.rvExpenses);
        btnEditIncome = findViewById(R.id.btnEditIncome);

        FloatingActionButton fab = findViewById(R.id.fabAddExpense);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, EditExpenseActivity.class);
            startActivity(intent);
        });

        btnEditIncome.setOnClickListener(v -> showSetIncomeDialog());

        loadIncome();
        updateSummary();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        expenseList = dbHelper.getAllExpenses();
        adapter = new ExpenseAdapter(expenseList, new ExpenseAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(int position) {
                Intent intent = new Intent(MainActivity.this, EditExpenseActivity.class);
                intent.putExtra("expense_id", expenseList.get(position).getId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(int position) {
                dbHelper.deleteExpense(expenseList.get(position).getId());
                updateSummary();
                setupRecyclerView();
            }

            @Override
            public void onItemClick(int position) {
                showExpenseDetailsDialog(expenseList.get(position));
            }
        });
        rvExpenses.setLayoutManager(new LinearLayoutManager(this));
        rvExpenses.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSummary();
        setupRecyclerView();
    }

    private void loadIncome() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        monthlyIncome = sharedPreferences.getFloat(MONTHLY_INCOME, 0);
    }

    private void saveIncome(double income) {
        monthlyIncome = income;
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(MONTHLY_INCOME, (float) monthlyIncome);
        editor.apply();
        updateSummary();
        Toast.makeText(this, "Income saved!", Toast.LENGTH_SHORT).show();
    }

    private void updateSummary() {
        String currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
        double totalExpense = dbHelper.getAllExpenses().stream()
                .filter(exp -> exp.getDate().startsWith(currentMonth))
                .mapToDouble(Expense::getAmount)
                .sum();
        double remaining = monthlyIncome - totalExpense;

        tvIncome.setText(String.format("%.2f MAD", monthlyIncome));
        tvTotalExpense.setText(String.format("Expenses: %.2f MAD", totalExpense));
        tvRemaining.setText(String.format("Remaining: %.2f MAD", remaining));
    }

    private void showSetIncomeDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_set_income, null);
        final TextInputEditText input = dialogView.findViewById(R.id.etIncomeInput);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Set Monthly Income")
                .setView(dialogView)
                .setPositiveButton("Save", null) // We override the listener
                .setNegativeButton("Cancel", (d, which) -> d.cancel())
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setTextColor(ContextCompat.getColor(this, R.color.primary));
            button.setOnClickListener(view -> {
                String incomeStr = input.getText().toString();
                if (incomeStr != null && !incomeStr.isEmpty()) {
                    try {
                        saveIncome(Double.parseDouble(incomeStr));
                        dialog.dismiss();
                    } catch (NumberFormatException e) {
                        input.setError("Invalid number format");
                    }
                } else {
                    input.setError("Please enter income");
                }
            });
        });

        dialog.show();
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
