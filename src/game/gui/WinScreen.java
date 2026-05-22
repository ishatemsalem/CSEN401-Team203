package game.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class WinScreen {
    private VBox view;

    public WinScreen(Main mainApp, String winnerName, String role, int finalEnergy) {
        view = new VBox(20);
        view.setAlignment(Pos.CENTER);
        
        // Dark, semi-transparent overlay background
        view.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);"); 

        Label title = new Label("VICTORY!");
        title.setStyle("-fx-text-fill: #ffd700; -fx-font-family: 'Jua', sans-serif; -fx-font-size: 80px; -fx-effect: dropshadow(gaussian, black, 10, 0.5, 0, 0);");

        Label winnerDetails = new Label(winnerName + " (" + role + ") has won!");
        winnerDetails.setStyle("-fx-text-fill: white; -fx-font-family: 'Jua', sans-serif; -fx-font-size: 35px;");

        Label energyDetails = new Label("Final Energy Collected: " + finalEnergy);
        energyDetails.setStyle("-fx-text-fill: #00e5ff; -fx-font-family: 'Jua', sans-serif; -fx-font-size: 25px;");

        Button returnButton = new Button("Return to Lobby");
        returnButton.setStyle("-fx-background-color: #6a1b9a; -fx-text-fill: white; -fx-font-family: 'Jua', sans-serif; -fx-font-size: 20px; -fx-background-radius: 8; -fx-cursor: hand;");
        returnButton.setPrefSize(250, 60);
        returnButton.setOnAction(e -> mainApp.switchToLobby());

        view.getChildren().addAll(title, winnerDetails, energyDetails, returnButton);
    }

    public VBox getView() {
        return view;
    }
}