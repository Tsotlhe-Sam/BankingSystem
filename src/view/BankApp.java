package view;

import controller.AccountController;
import controller.CustomerController;
import controller.LoginController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BankApp extends Application
{
    private Stage stage;
    private LoginController loginController;
    private CustomerController customerController;
    private AccountController accountController;

    @Override
    public void start(Stage primaryStage)
    {
        stage = primaryStage;

        // the controllers are created once and shared by every view
        loginController = new LoginController();
        customerController = new CustomerController(loginController);
        accountController = new AccountController(loginController, customerController);
        loginController.addDefaultStaff();

        stage.setTitle("Banking System");
        showLogin();
        stage.show();
    }

    private void showLogin()
    {
        LoginView loginView = new LoginView(loginController, this::showMain);
        stage.setScene(new Scene(loginView.getView(), 420, 300));
    }

    private void showMain()
    {
        MainView mainView = new MainView(loginController, customerController, accountController, this::showLogin);
        stage.setScene(new Scene(mainView.getView(), 760, 640));
    }
}
