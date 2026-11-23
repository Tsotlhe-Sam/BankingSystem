package view;

import controller.Result;
import javafx.scene.control.Label;

public class ViewHelper
{
    public static void show(Label label, Result result)
    {
        label.setText(result.getMessage());
        label.setStyle(result.isSuccess()
                ? "-fx-text-fill: #1b7f3b; -fx-font-weight: bold;"
                : "-fx-text-fill: #c0392b; -fx-font-weight: bold;");
    }

    public static Label heading(String text)
    {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        return label;
    }
}
