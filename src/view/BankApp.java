package view;

import controller.AccountController;
import controller.CustomerAccountController;
import controller.CustomerController;
import controller.DataLoader;
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

        loginController = new LoginController();
        customerController = new CustomerController(loginController);
        accountController = new AccountController(loginController, customerController);
        DataLoader.loadAll(loginController);      // create tables, sample data, load records

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

        if (loginController.isCustomerLoggedIn())
        {
            CustomerHomeView home = new CustomerHomeView(loginController,
                    new CustomerAccountController(loginController), this::showLogin);
            stage.setScene(new Scene(home.getView(), 700, 600));
            return;
        }

        MainView mainView = new MainView(loginController, customerController, accountController, this::showLogin);
        stage.setScene(new Scene(mainView.getView(), 760, 640));
    }
}