package view;

import controller.LoginController;
import controller.Result;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class LoginView
{
    private final VBox root = new VBox(15);

    public LoginView(LoginController loginController, Runnable onLoginSuccess)
    {
        Label title = ViewHelper.heading("Banking System - staff Login");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("password");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setAlignment(Pos.CENTER);
        form.addRow(0, new Label("Username:"),usernameField);
        form.addRow(1, new Label("password"), passwordField);

        Button loginButton = new Button ("Login");
        loginButton.setDefaultButton(true);
        Label message = new Label();

        loginButton.setOnAction(e ->
        {
            Result result = loginController.login(usernameField.getText(), passwordField.getText());
            ViewHelper.show (message,result);
            passwordField.clear();
            if (result.isSuccess())
            {
                onLoginSuccess.run();
            }
        });

        root.getChildren().addAll(title, form, loginButton, message);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

    }
    public Parent getView()
    {
        return  root;
    }
}
