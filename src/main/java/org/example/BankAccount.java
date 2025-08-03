package org.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class to represent a user's bank account.
 * It stores the balance, PIN, and transaction history.
 */
public class BankAccount {
    private double balance;
    private String pin;
    private final List<String> transactionHistory;
    private static final int MAX_HISTORY_ITEMS = 10;

    /**
     * Constructor to initialize the bank account.
     * @param initialBalance The starting balance.
     * @param pin The personal identification number.
     */
    public BankAccount(double initialBalance, String pin) {
        this.balance = Math.max(0, initialBalance);
        this.pin = pin;
        this.transactionHistory = new ArrayList<>();
        if (initialBalance > 0) {
            addTransaction("DEPOSIT", initialBalance);
        }
    }

    public double getBalance() {
        return balance;
    }

    public boolean validatePin(String enteredPin) {
        return this.pin.equals(enteredPin);
    }

    /**
     * Changes the account's PIN after validating the old one.
     * @param oldPin The current PIN.
     * @param newPin The new PIN to set.
     * @return true if the PIN was changed successfully, false otherwise.
     */
    public boolean changePin(String oldPin, String newPin) {
        if (validatePin(oldPin)) {
            this.pin = newPin;
            return true;
        }
        return false;
    }

    /**
     * Deposits money and records the transaction.
     * @param amount The amount to deposit.
     * @return A status message.
     */
    public String deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            addTransaction("DEPOSIT", amount);
            return "Successfully deposited $" + String.format("%.2f", amount);
        } else {
            return "Deposit amount must be positive.";
        }
    }

    /**
     * Withdraws money and records the transaction.
     * @param amount The amount to withdraw.
     * @return A status message.
     */
    public String withdraw(double amount) {
        if (amount <= 0) {
            return "Withdrawal amount must be positive.";
        } else if (amount > balance) {
            return "Insufficient funds.";
        } else {
            balance -= amount;
            addTransaction("WITHDRAWAL", amount);
            return "Successfully withdrew $" + String.format("%.2f", amount);
        }
    }

    /**
     * Adds a formatted transaction string to the history.
     * @param type The type of transaction (e.g., DEPOSIT).
     * @param amount The amount involved.
     */
    private void addTransaction(String type, double amount) {
        if (transactionHistory.size() >= MAX_HISTORY_ITEMS) {
            transactionHistory.removeFirst(); // Remove the oldest transaction
        }
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String sign = type.equals("DEPOSIT") ? "+" : "-";
        transactionHistory.add(String.format("%s | %-10s | %s$%.2f", timestamp, type, sign, amount));
    }

    /**
     * Returns an unmodifiable list of the transaction history.
     * @return The list of transactions.
     */
    public List<String> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }
}
