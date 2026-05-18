package game.gui;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.io.File;

public class Credits {
    private Pane view;
    private Main mainApp;
    private MediaPlayer creditsAudio;
    private Label title;
    private VBox namesList;

    // =========================================================================
    // PASTE YOUR 121 LINES OF TEXT HERE (Separate each line with \n)
    // =========================================================================
    private final String LEFT_COLUMN_TEXT = 
        "game by:\n" +
        "Lead Programmer:\n" +
        "UI Design:\n" +
        "Quality Assurance:\n" +
        "Audio Direction:";

    private final String RIGHT_COLUMN_TEXT = 
        "islam\n" +
        "islam\n" +
        "islam\n" +
        "islam\n" +
        "islam";
    // =========================================================================

    public Credits(Main mainApp) {
        this.mainApp = mainApp;
        
        // 1. Raw absolute-coordinate Pane (prevents elements from vanishing)
        view = new Pane();
        view.setPrefSize(1280, 720);

        // 2. Forced black background layer
        Rectangle pitchBlack = new Rectangle(1280, 720, Color.BLACK);

        // 3. Audio Loader
        try {
            Media media = new Media(new File("assets/audio/EndCreditsAudio.mp3").toURI().toString());
            creditsAudio = new MediaPlayer(media);
        } catch (Exception e) {
            System.out.println("EndCreditsAudio.mp3 not found, skipping audio.");
        }

        // 4. Font Loader
        Font hoefler;
        try {
            hoefler = Font.loadFont(new File("assets/fonts/Hoefler Text Regular.ttf").toURI().toString(), 80);
        } catch (Exception e) {
            hoefler = Font.font("Serif", 80);
        }

        // 5. DECOUPLED ELEMENT A: The "CREDITS" Title
        title = new Label("CREDITS");
        title.setFont(hoefler);
        title.setTextFill(Color.WHITE);
        title.setPrefWidth(1280);
        title.setAlignment(Pos.CENTER);
        title.setLayoutY(300); // Dead center of the 720p screen
        title.setOpacity(0);   // Starts invisible for the dissolve

        // 6. DECOUPLED ELEMENT B: The 121 lines of names
        namesList = new VBox(20); // 20px vertical gap between lines
        namesList.setPrefWidth(1280);
        namesList.setLayoutY(750); // Starts at Y=750 (just safely below the 720p screen bottom)

        String[] leftLines = LEFT_COLUMN_TEXT.split("\n");
        String[] rightLines = RIGHT_COLUMN_TEXT.split("\n");
        int maxLines = Math.max(leftLines.length, rightLines.length);

        for (int i = 0; i < maxLines; i++) {
            HBox row = new HBox(60); // 60px gap between left column and right column
            row.setAlignment(Pos.CENTER);

            String lText = (i < leftLines.length) ? leftLines[i] : "";
            String rText = (i < rightLines.length) ? rightLines[i] : "";

            Label leftLabel = new Label(lText);
            leftLabel.setStyle("-fx-text-fill: white; -fx-font-family: 'Jua', sans-serif; -fx-font-size: 22px; -fx-min-width: 300px; -fx-alignment: center-right;");
            
            Label rightLabel = new Label(rText);
            rightLabel.setStyle("-fx-text-fill: white; -fx-font-family: 'Jua', sans-serif; -fx-font-size: 22px; -fx-min-width: 300px; -fx-alignment: center-left;");

            row.getChildren().addAll(leftLabel, rightLabel);
            namesList.getChildren().add(row);
        }

        // 7. Skip Button
        Button skipBtn = new Button("Skip");
        skipBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: gray; -fx-font-size: 18px; -fx-cursor: hand;");
        skipBtn.setPrefSize(100, 40);
        skipBtn.setLayoutX(1150); // Bottom Right
        skipBtn.setLayoutY(650);
        skipBtn.setOnAction(e -> endCredits());

        // Add everything to the view in the correct order (Background first, Button last)
        view.getChildren().addAll(pitchBlack, title, namesList, skipBtn);
    }

    public void startAnimation() {
        if (creditsAudio != null) {
            creditsAudio.play();
        }

        // Dissolve "CREDITS" in over 0.3 seconds
        FadeTransition fadeTitleIn = new FadeTransition(Duration.seconds(0.3), title);
        fadeTitleIn.setToValue(1.0);

        // Movement variables: Same Speed = Same Distance / Same Time
        double scrollDistance = -8500; // Guaranteed to pull 121 lines completely off the top of the screen
        Duration scrollTime = Duration.seconds(215); // Exactly 3 minutes 35 seconds

        // DECOUPLED ANIMATION A: Move the Title
        TranslateTransition moveTitle = new TranslateTransition(scrollTime, title);
        moveTitle.setByY(scrollDistance);

        // DECOUPLED ANIMATION B: Move the Names
        TranslateTransition moveNames = new TranslateTransition(scrollTime, namesList);
        moveNames.setByY(scrollDistance);

        // Play them simultaneously so their speed perfectly locks together
        ParallelTransition scrollBoth = new ParallelTransition(moveTitle, moveNames);

        // Timeline: Wait 0.3s -> Fade in -> Wait 1.7s -> Scroll
        SequentialTransition seq = new SequentialTransition(
            new PauseTransition(Duration.seconds(0.3)), 
            fadeTitleIn,                                
            new PauseTransition(Duration.seconds(1.7)), 
            scrollBoth                                  
        );
        
        seq.setOnFinished(e -> endCredits());
        seq.play();
    }

    private void endCredits() {
        if (creditsAudio != null) {
            creditsAudio.stop();
        }
        
        // Dissolve back to lobby
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), view);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> mainApp.switchToLobby());
        fadeOut.play();
    }

    public Pane getView() {
        return view;
    }
}