package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test class for the BankAccount class.
 * This class contains unit tests to verify the functionality of the BankAccount methods.
 */
class BankAccountTest {

    private BankAccount account;
    private final String correctPin = "1015";

    /**
     * This method is run before each test.
     * It sets up a new BankAccount instance to ensure tests are independent.
     */
    @BeforeEach
    void setUp() {
        // Initialize a new account with a balance of 1000.00 and a specific PIN
        account = new BankAccount(1000.00, correctPin);
    }

    @Test
    @DisplayName("Test constructor with a valid initial balance")
    void testConstructorWithPositiveBalance() {
        // The balance should be exactly what was set in the constructor
        assertEquals(1000.00, account.getBalance(), "Constructor should set the initial balance correctly.");
    }

    @Test
    @DisplayName("Test constructor with a negative initial balance")
    void testConstructorWithNegativeBalance() {
        // A negative initial balance should default to 0
        BankAccount negativeBalanceAccount = new BankAccount(-500, "1234");
        assertEquals(0, negativeBalanceAccount.getBalance(), "Constructor should default a negative balance to 0.");
    }

    @Test
    @DisplayName("Test PIN validation with correct PIN")
    void testValidatePinCorrect() {
        // validatePin should return true for the correct PIN
        assertTrue(account.validatePin(correctPin), "Should return true for the correct PIN.");
    }

    @Test
    @DisplayName("Test PIN validation with incorrect PIN")
    void testValidatePinIncorrect() {
        // validatePin should return false for an incorrect PIN
        assertFalse(account.validatePin("0000"), "Should return false for an incorrect PIN.");
        assertFalse(account.validatePin(null), "Should return false for a null PIN.");
    }

    @Test
    @DisplayName("Test depositing a positive amount")
    void testDepositPositiveAmount() {
        // Deposit 200 to the initial 1000
        account.deposit(200.50);
        // The new balance should be 1200.50
        assertEquals(1200.50, account.getBalance(), "Balance should increase after a valid deposit.");
    }

    @Test
    @DisplayName("Test depositing a negative or zero amount")
    void testDepositNonPositiveAmount() {
        // Try to deposit a negative amount
        account.deposit(-100);
        // The balance should not change
        assertEquals(1000.00, account.getBalance(), "Balance should not change after a negative deposit.");

        // Try to deposit zero
        account.deposit(0);
        // The balance should still not change
        assertEquals(1000.00, account.getBalance(), "Balance should not change after depositing zero.");
    }

    @Test
    @DisplayName("Test a valid withdrawal")
    void testWithdrawValidAmount() {
        // Withdraw 300 from the initial 1000
        account.withdraw(300.00);
        // The new balance should be 700.00
        assertEquals(700.00, account.getBalance(), "Balance should decrease after a valid withdrawal.");
    }

    @Test
    @DisplayName("Test withdrawing an amount greater than the balance")
    void testWithdrawInsufficientFunds() {
        // Try to withdraw more money than is in the account
        account.withdraw(1500.00);
        // The balance should remain unchanged
        assertEquals(1000.00, account.getBalance(), "Balance should not change when withdrawal amount exceeds balance.");
    }

    @Test
    @DisplayName("Test withdrawing the exact balance")
    void testWithdrawExactBalance() {
        // Withdraw the entire balance
        account.withdraw(1000.00);
        // The new balance should be 0
        assertEquals(0, account.getBalance(), "Balance should be 0 after withdrawing the exact amount.");
    }

    @Test
    @DisplayName("Test withdrawing a negative or zero amount")
    void testWithdrawNonPositiveAmount() {
        // Try to withdraw a negative amount
        account.withdraw(-200);
        // The balance should not change
        assertEquals(1000.00, account.getBalance(), "Balance should not change after a negative withdrawal.");

        // Try to withdraw zero
        account.withdraw(0);
        // The balance should still not change
        assertEquals(1000.00, account.getBalance(), "Balance should not change after withdrawing zero.");
    }
}
