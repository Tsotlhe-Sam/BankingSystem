package view;

import controller.CustomerAccountController;
import controller.LoginController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class CustomerHomeView
{
    private final BorderPane root = new BorderPane();

    public CustomerHomeView(LoginController loginController, CustomerAccountController controller, Runnable onLogout)
    {

        Label who = new Label("Logged in as " + loginController.getCurrentCustomerName() + " (Customer)");
        who.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e ->
        {
            loginController.logout();
            onLogout.run();
        });
        HBox topBar = new HBox(10, who, spacer, logoutButton);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #1f3a5f;");

        ListView<String> accountList = new ListView<>();
        accountList.setPrefHeight(120);
        Label total = new Label();

        TextField accountNumber = new TextField();
        accountNumber.setPromptText("account number");
        TextField amount = new TextField();
        amount.setPromptText("e.g. 250.00");

        accountList.setOnMouseClicked(e ->
        {
            String line = accountList.getSelectionModel().getSelectedItem();
            if (line != null)
            {
                accountNumber.setText(line.split(" ")[1]);
            }
        });

        Runnable refresh = () ->
        {
            accountList.setItems(FXCollections.observableArrayList(controller.getMyAccounts()));
            total.setText(controller.getTotalBalance().getMessage());
        };
        refresh.run();

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.addRow(0, new Label("Account number:"), accountNumber);
        form.addRow(1, new Label("Amount (BWP):"), amount);

        Button depositButton = new Button("Deposit");
        Button withdrawButton = new Button("Withdraw");
        Button statementButton = new Button("Show Statement");
        Label message = new Label();
        ListView<String> statement = new ListView<>();
        statement.setPrefHeight(160);

        depositButton.setOnAction(e ->
        {
            ViewHelper.show(message, controller.deposit(accountNumber.getText(), amount.getText()));
            refresh.run();
        });
        withdrawButton.setOnAction(e ->
        {
            ViewHelper.show(message, controller.withdraw(accountNumber.getText(), amount.getText()));
            refresh.run();
        });
        statementButton.setOnAction(e ->
                statement.setItems(FXCollections.observableArrayList(controller.getMyStatement(accountNumber.getText()))));

        VBox content = new VBox(12,
                ViewHelper.heading("My Accounts"), accountList, total,
                new Separator(),
                ViewHelper.heading("Deposits and Withdrawals"), form,
                new HBox(10, depositButton, withdrawButton, statementButton),
                message, statement);
        content.setPadding(new Insets(20));

        root.setTop(topBar);
        root.setCenter(new ScrollPane(content));
    }

    public Parent getView()
    {
        return root;
    }
}