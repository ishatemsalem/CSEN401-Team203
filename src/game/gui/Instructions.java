package game.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class Instructions {
    private VBox view;

    public Instructions(Main mainApp) {
        view = new VBox(20);
        view.setAlignment(Pos.CENTER);
        view.setStyle("-fx-background-color: #121212;"); // Dark background
        view.setPadding(new Insets(40));

        Label title = new Label("How to Play: DooR DasH");
        title.setStyle("-fx-text-fill: #00e5ff; -fx-font-family: 'Jua', sans-serif; -fx-font-size: 40px;");

        VBox contentBox = new VBox(15);
        contentBox.setStyle("-fx-background-color: #1e1e1e; -fx-padding: 20; -fx-background-radius: 10;");
        
        String rules = 
            "GAME OBJECTIVE:\n" +
            "Be the first monster to reach Boo's Door (Cell 99) with at least 1000 Energy.\n\n" +
            "THE COMPETITORS:\n" +
            "• Scarers: Traditional scream energy collectors.\n" +
            "• Laughers: Revolutionary laughter energy collectors.\n\n" +
            "CELL TYPES:\n" +
            "• Doors (50): Match your role = Team gains energy. Wrong role = Team loses energy. Doors are exhausted after 1 use.\n" +
            "• Monster Cells (6): Land on a teammate to use your powerup for free. Land on an opponent to swap energy if they have more.\n" +
            "• Conveyor Belts (5): Rapidly transports you forward.\n" +
            "• Contamination Socks (5): CDA emergency! Transports you backward and drains 100 energy.\n" +
            "• Card Cells (10): Draw a random card (Steal, Shield, Swap, Start Over, Confusion).\n\n" +
            "MONSTER CLASSES & POWERUPS (Costs 500 Energy):\n" +
            "• Dasher (Speed): Moves 2x normally. Powerup: 3x speed for 3 turns.\n" +
            "• Dynamo (Power): Double energy gains AND losses. Powerup: Freezes opponent for 1 turn.\n" +
            "• MultiTasker (Balance): Moves 0.5x, gets +200 flat bonus to energy changes. Powerup: Moves at normal speed for 2 turns.\n" +
            "• Schemer (Cunning): +10 bonus to all changes. Powerup: Steals 10 energy from EVERY other monster on the board.\n\n" +
            "TURN SEQUENCE:\n" +
            "1. Choose whether to activate Powerup.\n" +
            "2. Roll the Dice.\n" +
            "3. Move and resolve cell effects.\n" +
            "4. End turn (or win if at cell 99 with >= 1000 Energy!).";

        Label text = new Label(rules);
        text.setWrapText(true);
        text.setStyle("-fx-text-fill: white; -fx-font-family: 'Jua', sans-serif; -fx-font-size: 18px; -fx-line-spacing: 5px;");

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #121212; -fx-border-color: transparent;");
        
        contentBox.getChildren().add(text);

        Button backBtn = new Button("Back to Lobby");
        backBtn.setStyle("-fx-background-color: crimson; -fx-text-fill: white; -fx-font-family: 'Jua', sans-serif; -fx-font-size: 20px; -fx-cursor: hand;");
        backBtn.setOnAction(e -> mainApp.switchToLobby());

        view.getChildren().addAll(title, scrollPane, backBtn);
    }   

    public VBox getView() {
        return view;
    }
}