package game.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class WinScreen {
    private VBox view;

    public WinScreen(Main mainApp, String winnerName, String role, int finalEnergy) {
        view = new VBox(20);
        view.setAlignment(Pos.CENTER);
        view.setStyle("-fx-background-color: #27ae60;"); // Victory Green Placeholder

        Label title = new Label("VICTORY!");
        title.setFont(Font.font("Arial", 80));
        title.setStyle("-fx-text-fill: white;");

        Label winnerDetails = new Label(winnerName + " (" + role + ") has won!");
        winnerDetails.setFont(Font.font("Arial", 30));
        winnerDetails.setStyle("-fx-text-fill: white;");

        Label energyDetails = new Label("Final Energy Collected: " + finalEnergy);
        energyDetails.setFont(Font.font("Arial", 25));
        energyDetails.setStyle("-fx-text-fill: yellow;");

        Button returnButton = new Button("Return to Lobby");
        returnButton.setPrefSize(250, 60);
        returnButton.setOnAction(e -> mainApp.switchToLobby());

        view.getChildren().addAll(title, winnerDetails, energyDetails, returnButton);
    }

    public VBox getView() {
        return view;
    }
}