package view;

import controller.AccountController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ManagerView
{
    private final VBox root = new VBox(12);

    public ManagerView(AccountController accountController)
    {
        Button payInterestButton = new Button("Pay Monthly Interest");
        Button viewAllButton = new Button("View All Accounts");
        Label message = new Label();
        ListView<String> allAccounts = new ListView<>();
        allAccounts.setPrefHeight(300);

        payInterestButton.setOnAction(e ->
        {
            ViewHelper.show(message, accountController.payMonthlyInterest());
            allAccounts.setItems(FXCollections.observableArrayList(accountController.viewAllAccounts()));
        });

        viewAllButton.setOnAction(e ->
                allAccounts.setItems(FXCollections.observableArrayList(accountController.viewAllAccounts())));

        root.getChildren().addAll(
                ViewHelper.heading("Manager Tools"),
                new HBox(10, payInterestButton, viewAllButton),
                message, allAccounts);
        root.setPadding(new Insets(20));
    }

    public Parent getView()
    {
        return root;
    }
}