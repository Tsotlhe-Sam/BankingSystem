package view;

import controller.CustomerController;
import controller.Result;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class CustomerView
{
    private final VBox root = new VBox(12);

    public CustomerView(CustomerController customerController)
    {

        ToggleGroup typeGroup = new ToggleGroup();
        RadioButton individualOption = new RadioButton("Individual");
        RadioButton companyOption = new RadioButton("Company");
        individualOption.setToggleGroup(typeGroup);
        companyOption.setToggleGroup(typeGroup);
        individualOption.setSelected(true);


        TextField customerNumber = new TextField();
        TextField firstname = new TextField();
        TextField surname = new TextField();
        TextField nationalID = new TextField();
        TextField email = new TextField();
        TextField phone = new TextField();
        TextField address = new TextField();
        CheckBox employed = new CheckBox("Employed");
        TextField username = new TextField();
        PasswordField password = new PasswordField();

        Label label1 = new Label("First name:");
        Label label2 = new Label("Surname:");
        Label label3 = new Label("National ID (Omang):");

        // change the labels when the customer type changes (display only, no logic)
        companyOption.setOnAction(e ->
        {
            label1.setText("Company name:");
            label2.setText("Registration no.:");
            label3.setText("Contact person:");
            employed.setDisable(true);
        });
        individualOption.setOnAction(e ->
        {
            label1.setText("First name:");
            label2.setText("Surname:");
            label3.setText("National ID (Omang):");
            employed.setDisable(false);
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.addRow(0, new Label("Customer type:"), new HBox(15, individualOption, companyOption));
        form.addRow(1, new Label("Customer number:"), customerNumber);
        form.addRow(2, label1, firstname);
        form.addRow(3, label2, surname);
        form.addRow(4, label3, nationalID);
        form.addRow(5, new Label("Email:"), email);
        form.addRow(6, new Label("Phone:"), phone);
        form.addRow(7, new Label("Address:"), address);
        form.addRow(8, new Label(""), employed);
        form.addRow(9, new Label("Username:"), username);
        form.addRow(10, new Label("Password:"), password);

        Button registerButton = new Button("Register Customer");
        Label registerMessage = new Label();

        registerButton.setOnAction(e ->
        {
            Result result;
            if (individualOption.isSelected())
            {
                result = customerController.registerIndividual(customerNumber.getText(), firstname.getText(),
                        surname.getText(), nationalID.getText(), email.getText(), phone.getText(),
                        address.getText(), employed.isSelected(), username.getText(), password.getText());
            }
            else
            {
                result = customerController.registerCompany(customerNumber.getText(), firstname.getText(),
                        surname.getText(), nationalID.getText(), email.getText(), phone.getText(),
                        address.getText(), username.getText(), password.getText());
            }
            ViewHelper.show(registerMessage, result);
        });


        TextField searchNumber = new TextField();
        searchNumber.setPromptText("customer number");
        Button findButton = new Button("Find");
        Label foundName = new Label();
        ListView<String> accountList = new ListView<>();
        accountList.setPrefHeight(120);

        findButton.setOnAction(e ->
        {
            String name = customerController.getCustomerName(searchNumber.getText());
            if (name == null)
            {
                foundName.setText("No customer found");
                accountList.getItems().clear();
            }
            else
            {
                foundName.setText(name);
                accountList.setItems(FXCollections.observableArrayList(
                        customerController.getAccountSummaries(searchNumber.getText())));
            }
        });

        root.getChildren().addAll(
                ViewHelper.heading("Register a Customer"), form, registerButton, registerMessage,
                new Separator(),
                ViewHelper.heading("Find a Customer"), new HBox(10, searchNumber, findButton),
                foundName, accountList);
        root.setPadding(new Insets(20));
    }

    public Parent getView()
    {
        return new ScrollPane(root);
    }
}