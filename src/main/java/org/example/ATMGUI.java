package org.example;

import org.example.BankAccount;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * A completely redesigned GUI for the ATM machine.
 * Features a custom on-screen numeric keypad for all inputs,
 * state management for different operations, and a more realistic layout.
 */
public class ATMGUI extends JFrame {

    // --- Inner Enum for State Management ---
    private enum ATMState {
        PIN_ENTRY, MAIN_MENU, DEPOSIT_ENTRY, WITHDRAW_ENTRY, CHANGE_PIN_OLD, CHANGE_PIN_NEW
    }

    private ATMState currentState;
    private final BankAccount account;

    // --- UI Components ---
    private final JPasswordField displayField; // Using JPasswordField for masked input
    private final JTextArea messageArea;      // For messages and history
    private final JButton[] actionButtons;

    // --- UI Constants ---
    private final Color primaryColor = new Color(34, 47, 62);    // Dark Slate
    private final Color secondaryColor = new Color(87, 101, 116);   // Lighter Slate
    private final Color accentColor = new Color(255, 165, 2);     // Orange
    private final Color textColor = Color.WHITE;
    private final Font mainFont = new Font("Arial", Font.BOLD, 18);
    private final Font displayFont = new Font("Monospaced", Font.BOLD, 24);
    private final Font messageFont = new Font("Arial", Font.PLAIN, 16);

    private String oldPinBuffer = ""; // To store old PIN during change process

    public ATMGUI() {
        account = new BankAccount(1000.00, "1015");
        currentState = ATMState.PIN_ENTRY;

        // --- Frame Setup ---
        setTitle("Advanced ATM");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 750);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // --- Display Panel (Top) ---
        JPanel displayPanel = createDisplayPanel();
        add(displayPanel, BorderLayout.NORTH);

        // --- Main Content Panel (Center) ---
        JPanel mainContentPanel = new JPanel(new BorderLayout(15, 15));
        mainContentPanel.setBackground(primaryColor);
        mainContentPanel.setBorder(new EmptyBorder(10, 15, 15, 15));

        // --- Action Buttons (Left & Right) ---
        actionButtons = new JButton[4];
        JPanel leftButtonPanel = createSideButtonPanel("Check Balance", "Deposit");
        JPanel rightButtonPanel = createSideButtonPanel("Withdraw", "Change PIN");
        mainContentPanel.add(leftButtonPanel, BorderLayout.WEST);
        mainContentPanel.add(rightButtonPanel, BorderLayout.EAST);

        // --- Numeric Keypad (Center) ---
        JPanel keypadPanel = createKeypadPanel();
        mainContentPanel.add(keypadPanel, BorderLayout.CENTER);

        add(mainContentPanel, BorderLayout.CENTER);

        // --- Initialize UI State ---
        displayField = (JPasswordField) ((JPanel) displayPanel.getComponent(0)).getComponent(1);
        messageArea = (JTextArea) ((JScrollPane) displayPanel.getComponent(1)).getViewport().getView();
        setActionButtonsEnabled(false);
        updateDisplay();
    }

    // --- UI Creation Methods ---

    private JPanel createDisplayPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(primaryColor);
        panel.setBorder(new EmptyBorder(20, 20, 10, 20));

        // Input Field Panel
        JPanel inputFieldPanel = new JPanel(new BorderLayout(10, 0));
        inputFieldPanel.setBackground(primaryColor);
        JLabel inputLabel = new JLabel("PIN:");
        inputLabel.setFont(mainFont);
        inputLabel.setForeground(textColor);
        JPasswordField field = new JPasswordField();
        field.setFont(displayFont);
        field.setEditable(false);
        field.setHorizontalAlignment(SwingConstants.CENTER);
        field.setBackground(new Color(20, 30, 40));
        field.setForeground(accentColor);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor),
                new EmptyBorder(5, 5, 5, 5)
        ));
        inputFieldPanel.add(inputLabel, BorderLayout.WEST);
        inputFieldPanel.add(field, BorderLayout.CENTER);
        panel.add(inputFieldPanel);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Message Area
        JTextArea area = new JTextArea(8, 20);
        area.setFont(messageFont);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setForeground(textColor);
        area.setBackground(secondaryColor);
        area.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane);

        return panel;
    }

    private JPanel createSideButtonPanel(String topButtonText, String bottomButtonText) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 20));
        panel.setBackground(primaryColor);

        JButton topButton = createStyledButton(topButtonText);
        JButton bottomButton = createStyledButton(bottomButtonText);

        if (topButtonText.equals("Check Balance")) actionButtons[0] = topButton;
        if (topButtonText.equals("Withdraw")) actionButtons[2] = topButton;
        if (bottomButtonText.equals("Deposit")) actionButtons[1] = bottomButton;
        if (bottomButtonText.equals("Change PIN")) actionButtons[3] = bottomButton;

        topButton.addActionListener(e -> handleActionEvent(topButtonText));
        bottomButton.addActionListener(e -> handleActionEvent(bottomButtonText));

        panel.add(topButton);
        panel.add(bottomButton);
        return panel;
    }

    private JPanel createKeypadPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 3, 5, 5));
        panel.setBackground(primaryColor);
        String[] keys = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "Clear", "0", "Enter"};
        for (String key : keys) {
            JButton button = new JButton(key);
            button.setFont(mainFont);
            if (key.equals("Enter")) {
                button.setBackground(accentColor);
                button.setForeground(primaryColor);
            } else if (key.equals("Clear")) {
                button.setBackground(new Color(214, 48, 49)); // Red
                button.setForeground(textColor);
            } else {
                button.setBackground(secondaryColor);
                button.setForeground(textColor);
            }
            button.addActionListener(e -> handleKeypad(key));
            panel.add(button);
        }
        return panel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(mainFont);
        button.setForeground(textColor);
        button.setBackground(secondaryColor);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 60));
        return button;
    }

    // --- Event Handling & Logic ---

    private void handleKeypad(String key) {
        String currentText = new String(displayField.getPassword());
        if (key.equals("Enter")) {
            processEnter();
        } else if (key.equals("Clear")) {
            displayField.setText("");
        } else {
            displayField.setText(currentText + key);
        }
    }

    private void handleActionEvent(String command) {
        switch(command) {
            case "Check Balance":
                checkBalance();
                break;
            case "Deposit":
                currentState = ATMState.DEPOSIT_ENTRY;
                break;
            case "Withdraw":
                currentState = ATMState.WITHDRAW_ENTRY;
                break;
            case "Change PIN":
                currentState = ATMState.CHANGE_PIN_OLD;
                break;
        }
        updateDisplay();
    }

    private void processEnter() {
        String input = new String(displayField.getPassword());
        displayField.setText("");

        switch (currentState) {
            case PIN_ENTRY:
                if (account.validatePin(input)) {
                    currentState = ATMState.MAIN_MENU;
                    setActionButtonsEnabled(true);
                } else {
                    messageArea.setText("Incorrect PIN. Please try again.");
                }
                break;
            case DEPOSIT_ENTRY:
                try {
                    double amount = Double.parseDouble(input);
                    String result = account.deposit(amount);
                    messageArea.setText(result + String.format("\nNew balance: $%.2f", account.getBalance()));
                    currentState = ATMState.MAIN_MENU;
                } catch (NumberFormatException ex) {
                    messageArea.setText("Invalid amount entered. Please try again.");
                }
                break;
            case WITHDRAW_ENTRY:
                try {
                    double amount = Double.parseDouble(input);
                    String result = account.withdraw(amount);
                    messageArea.setText(result + String.format("\nNew balance: $%.2f", account.getBalance()));
                    currentState = ATMState.MAIN_MENU;
                } catch (NumberFormatException ex) {
                    messageArea.setText("Invalid amount entered. Please try again.");
                }
                break;
            case CHANGE_PIN_OLD:
                if (account.validatePin(input)) {
                    oldPinBuffer = input;
                    currentState = ATMState.CHANGE_PIN_NEW;
                } else {
                    messageArea.setText("Incorrect old PIN. Returning to main menu.");
                    currentState = ATMState.MAIN_MENU;
                }
                break;
            case CHANGE_PIN_NEW:
                if (input.matches("\\d{4,}")) {
                    account.changePin(oldPinBuffer, input);
                    messageArea.setText("PIN changed successfully!");
                } else {
                    messageArea.setText("Invalid new PIN format. Must be 4+ digits.");
                }
                currentState = ATMState.MAIN_MENU;
                break;
        }
        updateDisplay();
    }

    private void checkBalance() {
        messageArea.setText(String.format("Your current balance is: $%.2f", account.getBalance()));
        // Show transaction history as well
        List<String> history = account.getTransactionHistory();
        StringBuilder historyText = new StringBuilder();
        historyText.append(String.format("Your current balance is: $%.2f\n\n", account.getBalance()));
        historyText.append("--- Recent Transactions ---\n");
        if(history.isEmpty()) {
            historyText.append("No transactions found.");
        } else {
            for (int i = history.size() - 1; i >= 0; i--) {
                historyText.append(history.get(i)).append("\n");
            }
        }
        messageArea.setText(historyText.toString());
    }

    private void updateDisplay() {
        switch (currentState) {
            case PIN_ENTRY:
                messageArea.setText("Welcome! Please enter your PIN and press Enter.");
                break;
            case MAIN_MENU:
                messageArea.setText("Select an option.");
                break;
            case DEPOSIT_ENTRY:
                messageArea.setText("Enter amount to deposit, then press Enter.");
                break;
            case WITHDRAW_ENTRY:
                messageArea.setText("Enter amount to withdraw, then press Enter.");
                break;
            case CHANGE_PIN_OLD:
                messageArea.setText("Enter your OLD PIN, then press Enter.");
                break;
            case CHANGE_PIN_NEW:
                messageArea.setText("Enter your NEW PIN (4+ digits), then press Enter.");
                break;
        }
    }

    private void setActionButtonsEnabled(boolean enabled) {
        for (JButton button : actionButtons) {
            button.setEnabled(enabled);
            button.setBackground(enabled ? secondaryColor : new Color(50, 60, 70));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ATMGUI atm = new ATMGUI();
            atm.setVisible(true);
        });
    }
}
