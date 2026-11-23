package view;

import controller.AccountController;
import controller.Result;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class AccountView
{
    private final VBox root = new VBox(12);

    public AccountView(AccountController accountController)
    {
        TextField customerNumber = new TextField();
        ComboBox<String> accountType = new ComboBox<>(
                FXCollections.observableArrayList("SAVINGS", "INVESTMENT", "CHEQUE"));
        accountType.setValue("SAVINGS");
        TextField openingDeposit = new TextField();
        openingDeposit.setPromptText("e.g. 500.00");
        TextField employerName = new TextField();
        TextField employerAddress = new TextField();

        // employer fields only make sense for a Cheque account (display only, no logic)
        employerName.setDisable(true);
        employerAddress.setDisable(true);
        accountType.setOnAction(e ->
        {
            boolean cheque = "CHEQUE".equals(accountType.getValue());
            employerName.setDisable(!cheque);
            employerAddress.setDisable(!cheque);
        });

        GridPane openForm = new GridPane();
        openForm.setHgap(10);
        openForm.setVgap(8);
        openForm.addRow(0, new Label("Customer number:"), customerNumber);
        openForm.addRow(1, new Label("Account type:"), accountType);
        openForm.addRow(2, new Label("Opening deposit (BWP):"), openingDeposit);
        openForm.addRow(3, new Label("Employer name:"), employerName);
        openForm.addRow(4, new Label("Employer address:"), employerAddress);

        Button openButton = new Button("Open Account");
        Label openMessage = new Label();

        openButton.setOnAction(e ->
        {
            Result result = accountController.openAccount(customerNumber.getText(), accountType.getValue(),
                    openingDeposit.getText(), employerName.getText(), employerAddress.getText());
            ViewHelper.show(openMessage, result);
        });

        // ================= transactions =================
        TextField accountNumber = new TextField();
        TextField amount = new TextField();
        amount.setPromptText("e.g. 250.00");

        GridPane txForm = new GridPane();
        txForm.setHgap(10);
        txForm.setVgap(8);
        txForm.addRow(0, new Label("Account number:"), accountNumber);
        txForm.addRow(1, new Label("Amount (BWP):"), amount);

        Button depositButton = new Button("Deposit");
        Button withdrawButton = new Button("Withdraw");
        Button balanceButton = new Button("Check Balance");
        Button statementButton = new Button("Show Statement");
        Label txMessage = new Label();
        ListView<String> statement = new ListView<>();
        statement.setPrefHeight(160);

        depositButton.setOnAction(e ->
                ViewHelper.show(txMessage, accountController.deposit(accountNumber.getText(), amount.getText())));

        withdrawButton.setOnAction(e ->
                ViewHelper.show(txMessage, accountController.withdraw(accountNumber.getText(), amount.getText())));

        balanceButton.setOnAction(e ->
                ViewHelper.show(txMessage, accountController.getBalance(accountNumber.getText())));

        statementButton.setOnAction(e ->
                statement.setItems(FXCollections.observableArrayList(
                        accountController.getStatement(accountNumber.getText()))));

        root.getChildren().addAll(
                ViewHelper.heading("Open an Account"), openForm, openButton, openMessage,
                new Separator(),
                ViewHelper.heading("Deposits and Withdrawals"), txForm,
                new HBox(10, depositButton, withdrawButton, balanceButton, statementButton),
                txMessage, statement);
        root.setPadding(new Insets(20));
    }

    public Parent getView()
    {
        return new ScrollPane(root);
    }
}