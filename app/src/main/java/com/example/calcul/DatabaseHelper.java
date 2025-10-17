package com.example.calcul;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ExpenseDB";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_EXPENSES = "expenses";
    private static final String COL_ID = "id";
    private static final String COL_CATEGORY = "category";
    private static final String COL_AMOUNT = "amount";
    private static final String COL_DATE = "date";
    private static final String COL_DESCRIPTION = "description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_EXPENSES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CATEGORY + " TEXT, " +
                COL_AMOUNT + " REAL, " +
                COL_DATE + " TEXT, " +
                COL_DESCRIPTION + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        onCreate(db);
    }

    // Add expense
    public boolean addExpense(String category, double amount, String date, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CATEGORY, category);
        values.put(COL_AMOUNT, amount);
        values.put(COL_DATE, date);
        values.put(COL_DESCRIPTION, description);
        long result = db.insert(TABLE_EXPENSES, null, values);
        db.close();
        return result != -1;
    }

    // Get monthly expenses by category
    public double getMonthlyExpenseByCategory(String category, String month) {
        SQLiteDatabase db = this.getReadableDatabase();
        double total = 0;
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE_EXPENSES +
                        " WHERE " + COL_CATEGORY + " = ? AND " + COL_DATE + " LIKE ?",
                new String[]{category, month + "%"}
        );
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    // Get total monthly expenses
    public double getTotalMonthlyExpense(String month) {
        SQLiteDatabase db = this.getReadableDatabase();
        double total = 0;
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE_EXPENSES +
                        " WHERE " + COL_DATE + " LIKE ?",
                new String[]{month + "%"}
        );
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    // Get all expenses for a month
    public List<Expense> getMonthlyExpenses(String month) {
        List<Expense> expenseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_EXPENSES + " WHERE " + COL_DATE + " LIKE ?",
                new String[]{month + "%"}
        );

        while (cursor.moveToNext()) {
            Expense expense = new Expense(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COL_AMOUNT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION))
            );
            expenseList.add(expense);
        }
        cursor.close();
        db.close();
        return expenseList;
    }
}
