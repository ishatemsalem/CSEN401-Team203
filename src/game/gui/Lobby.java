package game.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class Lobby {
    private VBox view;
    private Main mainApp;

    public Lobby(Main mainApp) {
        this.mainApp = mainApp;
        
        view = new VBox(20);
        view.setAlignment(Pos.CENTER);
        view.setStyle("-fx-background-color: #2c3e50;"); // Placeholder styling

        Label title = new Label("DooR DasH");
        title.setFont(Font.font("Arial", 60));
        title.setStyle("-fx-text-fill: white;");

        // Side Selection
        Label sideLabel = new Label("Choose Your Path:");
        sideLabel.setStyle("-fx-text-fill: white;");
        ComboBox<String> sideSelection = new ComboBox<>();
        sideSelection.getItems().addAll("Scare (Tradition)", "Laugh (Revolution)");
        sideSelection.getSelectionModel().selectFirst();

        // Buttons
        Button startButton = new Button("Start Game");
        startButton.setPrefSize(200, 50);
        startButton.setOnAction(e -> mainApp.startGame(sideSelection.getValue()));

        Button instructionsButton = new Button("Instructions");
        instructionsButton.setPrefSize(200, 50);
        instructionsButton.setOnAction(e -> mainApp.switchToInstructions());

        view.getChildren().addAll(title, sideLabel, sideSelection, startButton, instructionsButton);
    }

    public VBox getView() {
        return view;
    }
}