package view;

import controller.AccountController;
import controller.CustomerController;
import controller.LoginController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;


public class MainView
{
    private final BorderPane root = new BorderPane();

    public MainView(LoginController loginController, CustomerController customerController,
                    AccountController accountController, Runnable onLogout)
    {
        Label who = new Label("Logged in as " + loginController.getCurrentUser().getUsername()
                + (loginController.isManager() ? " (Manager)" : " (Teller)"));
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
        who.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");


        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().add(new Tab("Customers", new CustomerView(customerController).getView()));
        tabs.getTabs().add(new Tab("Accounts", new AccountView(accountController).getView()));
        if (loginController.isManager())
        {
            tabs.getTabs().add(new Tab("Manager", new ManagerView(accountController).getView()));
        }

        root.setTop(topBar);
        root.setCenter(tabs);
    }

    public Parent getView()
    {
        return root;
    }
}